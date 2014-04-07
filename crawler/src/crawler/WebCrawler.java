package crawler;

import general.LogClass;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import storage.*;

public class WebCrawler {
	
	private static class CrawlUrl {
		String url;
		int depth;
		
		public CrawlUrl(String url, int depth) {
			this.url = url;
			this.depth = depth;
		}
		
		public String getUrl() { return url; }
		int getDepth() { return depth; }
	}
	
	private static class CrawlInfo {
		CrawlUrl crawl_url;
		long delay;

		public CrawlInfo(CrawlUrl crawl_url, long delay) {
			this.crawl_url = crawl_url;
			this.delay = delay;
		}

		public CrawlUrl getCrawlUrl() { return crawl_url; }

		public long getDelay() { return delay; }
	}

	private final static int DEPTH = 10;
	
	private final static String[] initUrls = new String[] {
		/*"http://stackoverflow.com/", */"http://www.upenn.edu/",/*
		"http://www.oracle.com/index.html"*/
	};
	/*
	private final static String[] initUrls = new String[] {
		"http://crawltest.cis.upenn.edu/"
	};
	*/
	
	// The queue store url for crawling; each host have one queue of url; Host : Queue
	private Map<String, Queue<CrawlUrl>> frontierQueue;
	// tmp set hold the url of robot
	private Set<String> visitedRobots;
	// The set store url already crawled
	private Set<String> visitedUrl;
	// The database directory
	private String db_directory;
	// Max size of document
	private int max_size;
	// Max number of files to be downloaded
	private int max_number;
	private AtomicInteger count;
	// Database 
	private BerkerleyDB database;
	// RobotsParser
	RobotsParser robotParser;
	// ExecuteService
	ExecutorService service;
	
	
	public WebCrawler(String dir, int size, int number) throws MalformedURLException {
		db_directory = dir;
		max_size = size;
		max_number = number;
		count = new AtomicInteger(1);
		frontierQueue = Collections.synchronizedMap(new HashMap<String, Queue<CrawlUrl>>());
		visitedRobots = Collections.synchronizedSet(new HashSet<String>());
		visitedUrl = Collections.synchronizedSet(new HashSet<String>());
		database = new BerkerleyDB(db_directory);
		robotParser = new RobotsParser(database);
		service = Executors.newFixedThreadPool(initUrls.length);
		initFrontierQueue();
	}

	private void initFrontierQueue() throws MalformedURLException {
		for(String init_url : initUrls) {
			Queue<CrawlUrl> tmpqueue = new LinkedList<CrawlUrl>();
			CrawlUrl crawl_url = new CrawlUrl(init_url, 1);
			tmpqueue.offer(crawl_url);
			String hostname = UrlNormalize.getHostName(init_url);
			frontierQueue.put(hostname, tmpqueue);
		}
	}
	
	public void init() {
		for(int i = 0; i < initUrls.length; i++)
			service.execute(new CrawlerThread());
		service.shutdown();
	}
	
	// return sleep time
	public synchronized CrawlInfo dequeue() throws Exception {
		// store the best delay and the mapping host name
		String bestHost = null;
		CrawlUrl best_crawl_url = null;
		long best_delay = Long.MAX_VALUE;
		
		// Creat Map iterator
		Iterator<Entry<String, Queue<CrawlUrl>>> iterator = frontierQueue.entrySet().iterator();
		while(iterator.hasNext()) {
			// 1. init the entry
			Entry<String, Queue<CrawlUrl>> entry = iterator.next();
			String hostname = entry.getKey();
			Queue<CrawlUrl> queue = entry.getValue();
			// if the queue is empty
			if(queue == null || queue.isEmpty()) {
				// remove this entry
				iterator.remove();
				continue;
			}
			
			// 2. Check if we seen the url or the url already got the depth
			// get the crawl url from queue
			CrawlUrl tmp_crawl_url = queue.peek();
			// get url
			String url = tmp_crawl_url.getUrl();
			// Already Seen the URL or already got the depth, this maybe the case we add the same url into queue
			if(visitedUrl.contains(url) || tmp_crawl_url.getDepth() >= DEPTH) {
				// take the url from queue and set crawl_url = null
				queue.poll();
				if(queue == null || queue.isEmpty())
					iterator.remove();
				continue;
			}
			
			//3. Check if this url compatible the robotText
			// get robot text of this url
			String robotUrl = "http://" + hostname + "/robots.txt";
			RobotText rt;
			System.out.println("VisitedRobots Contains: " + visitedRobots.contains(robotUrl));
			if(! visitedRobots.contains(robotUrl)) {
				robotParser.downloadRobot(url);
				rt = robotParser.getRobot(url);
			}
			else 
				rt = robotParser.getRobot(url);
			
			//4. the server don't have robots.txt
			if(rt == null) {
				// use this url and set crawl_url to this url
				if(best_crawl_url == null || best_crawl_url.getDepth() < tmp_crawl_url.getDepth()) {
					bestHost = hostname;
					best_delay = 0;
					best_crawl_url = tmp_crawl_url;
				}
			}
			//5. the server have robots.txt
			else {
				System.out.println("Robot Delay: " + rt.getDelay());
				// don't allow this url
				if(! robotParser.allowUrl(rt, url)) {
					visitedRobots.add(robotUrl);
					queue.poll();
					// we add this url (means we need to consider the delay later)
					if(queue == null || queue.isEmpty())
						iterator.remove();
					continue;
				}

				// the first time got the robot.txt, we ignore the delay return delay;
				if(! visitedRobots.contains(robotUrl)) {
					// the first time got the robot.txt, we ignore the delay
					bestHost = hostname;
					best_delay = 0;
					visitedRobots.add(robotUrl);
					System.out.println("add robot url: " + robotUrl);
					break;
				}
				//delay = delay < 0 ? robotParser.timeAllow(rt) : Math.min(delay, robotParser.timeAllow(rt));
				// delay is bigger than robotParser.timeAllow
				long time_allow = robotParser.timeAllow(rt);
				//if time_allow < 0, we see the depth, chose the small one
				if(time_allow <= 0) {
					if(best_crawl_url == null || best_crawl_url.getDepth() < tmp_crawl_url.getDepth()) {
						bestHost = hostname;
						best_delay = time_allow;
						best_crawl_url = tmp_crawl_url;
					}
				}
				else if(best_delay > time_allow) {
					// update crawl_url and delay
					bestHost = hostname;
					best_delay = time_allow;
					best_crawl_url = tmp_crawl_url;
				}
			}
		}
		// we don't have anything to process
		if(best_delay == Long.MAX_VALUE)
			return null;
		if(bestHost != null) {
			Queue<CrawlUrl> queue = frontierQueue.get(bestHost);
			best_crawl_url = queue.poll();
			if(queue.isEmpty())
				frontierQueue.remove(bestHost);
			robotParser.updateRobot(best_crawl_url.getUrl());
			System.out.println("Best Url: " + best_crawl_url.getUrl());
		}
		return new CrawlInfo(best_crawl_url, best_delay);
	}
	
	private class CrawlerThread implements Runnable {
		
		public void run() {
			try {
				init();
			} catch (Exception e) {
				System.out.println("Error");
				e.printStackTrace();
			}
		}
		
		public void init() throws Exception{
			// put the url into database
			while(! frontierQueue.isEmpty() && count.get() < max_number) {
				System.out.println("Begin /********************************/");
				System.out.println("Crawled Success Number: " + count);
				// Take item from the Queue and get the delay
				CrawlInfo crawl_info = dequeue();
				// no one meet the crawl-delay
				long delay = crawl_info.getDelay();
				CrawlUrl crawl_url = crawl_info.getCrawlUrl();
				
				if(delay > 0) {
					System.out.println("Waiting for Crawl-Delay: " + delay + " milliseconds");
					Thread.sleep(delay);
				}
				
				// we don't get anything
				if(crawl_url == null) {
					continue;
				}	
				
				System.out.println("Thread get: " + crawl_url.getUrl() + "; " + delay);
				// we can crawl this url
				// Add this url to visited
				String url = crawl_url.getUrl();
				visitedUrl.add(url);
	
				WebClient wc = new WebClient();
				DocumentRetriever dr = new DocumentRetriever(url);
				// check header type and if modified or find new location
				HeadNode hn = checkHeader(wc, url);
				// get the host name
				if(hn != null) {
					// new location, get new url and new head node
					int i = 3;
					while(hn.getLocation() != null && i > 0) {
						String tmpurl = hn.getLocation();
						// get absolute path of getLocation
						url = UrlNormalize.getAbsoluteUrl(tmpurl, url);
						hn = checkHeader(wc, url);
						visitedUrl.add(url);
						i--;
						// hn is null or url is https we break;
						if(hn == null || !UrlNormalize.schemaIsHttp(url))
							break;
					}
					if(hn == null || !UrlNormalize.schemaIsHttp(url)) continue;
					Document doc;
					System.out.println("HeadNode status: " + hn.getState() + "; Type valid: " + hn.typeValid());
					// if not modified
					if(hn.getState() == 1) {
						robotParser.updateRobot(url);
						System.out.println(url + ": Not Modified");
						// html_data can't be null as it is not modified
						HtmlDocument html_data = database.getHtmlDocument(url);
						String content = html_data.getHtmlData();
						// update information in database
						database.storeHtmlDocument(html_data.getUrl() , content, new Date());
						doc = dr.convertToHTML(content);
						// retrieve 1 document
						count.incrementAndGet();
						// we didn't get this content before, visitedContent add the hash key
						if(doc != null) {
							processHtmlDocument(doc, crawl_url);
						}
					}
					// else response is ok
					else if(hn.getState() == 0){
						robotParser.updateRobot(url);
						// type is not html or xml, we ignore
						if(! hn.typeValid() || (int)(hn.getLength() / Math.pow(10, 6)) > max_size) {
							continue;
						}
						else {
							dr.setPath(url);
							doc = dr.retrieve();
							// if we retrieved the document
							if(doc != null) {
								System.out.println(url + ": Downloading");
								// retrieve 1 document
								count.incrementAndGet();
								// store in the database, maybe different url store same content
								String content = dr.getResponse();
								database.storeHtmlDocument(url, content, new Date());
								processHtmlDocument(doc, crawl_url);
							}
						}
					}
				}
				System.out.println("End /*********************************/");
			}
		}
	}

	// Process HTML document, add the url to the frontierQueue
	public void processHtmlDocument(Document doc, CrawlUrl crawl_url) throws MalformedURLException {
		if(doc == null)
			return;
		String url = crawl_url.getUrl();
		String hostname = UrlNormalize.getHostName(url);
		System.out.println("Processing Html Document: " + url);
		// Get all tag <a>
		NodeList list = doc.getElementsByTagName("a");
		for(int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			// see all attribute in tag <a>
			NamedNodeMap map = n.getAttributes();
			for(int j = 0; j < map.getLength(); j++) {
				String name = map.item(j).getNodeName().toLowerCase();
				String value = map.item(j).getNodeValue();
				// get the absolute url of value
				value = UrlNormalize.getAbsoluteUrl(value, url);
				if(name.equals("href") && ! visitedUrl.contains(value)) {
					appendUrl(hostname, value, crawl_url.getDepth());
				}
			}
		}
	}
	
	/**
	 * Synchronized methods for url add to queue
	 * @param hostname
	 * @param url
	 * @param depth
	 */
	public synchronized void appendUrl(String hostname, String url, int depth) {
		Queue<CrawlUrl> tmpqueue = frontierQueue.get(hostname);
		if(tmpqueue == null) {
			tmpqueue = new LinkedList<CrawlUrl>();
		}
		// if schema is http not https
		if(UrlNormalize.schemaIsHttp(url)) {
			tmpqueue.add(new CrawlUrl(url, depth+1));
			frontierQueue.put(hostname, tmpqueue);
		}
	}


	// Check get headnode, use Head to query the server
	public HeadNode checkHeader(WebClient wc, String url) {
		// Get HtmlDocument 
		HtmlDocument html_data = database.getHtmlDocument(url);
		// Query Header
		if(html_data == null)
			// we don't have the record in database
			return wc.inspectHeader(url, null);
		// return the HeadNode
		else {
			String dateString = DateTransfer.getDate(html_data.getDate());
			return wc.inspectHeader(url, dateString);
		}
	}

	
	public Map<String, Queue<CrawlUrl>> getQueue() {
		return frontierQueue;
	}
	
	public static void main(String args[]) {
		WebCrawler crawler;
	    if(args.length < 2 || args.length > 3) {
	    	throw new IllegalArgumentException("Wrong Argument Number");
	    }
	    try {
	    	String dir = args[0];
	    	int size = Integer.parseInt(args[1]);
	    	int number = args.length == 3 ? Integer.parseInt(args[2]) : Integer.MAX_VALUE;
	    	crawler = new WebCrawler(dir, size, number);
	    	crawler.init();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	throw new IllegalArgumentException("Wrong Argument format");
	    }
	}
	
}
