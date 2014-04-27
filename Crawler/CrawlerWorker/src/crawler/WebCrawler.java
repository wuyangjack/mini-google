package crawler;

import crawldatatype.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.upenn.cis455.mapreduce.worker.LogClass;

import general.UrlNormalizer;
import hash.SHA1;
import hash.SHA1Partition;
import storage.*;

public class WebCrawler implements CrawlerInterface{
  
  
    private final static String CRLF = "\r\n";
    // we need to get the BigInteger of range and section
    public final static int THREADNUM = 8;
    // don't crawl url include /cgi-bin/
    private final static String CGIPATTERN = ".*/cgi-bin/.*";
    private final static String NONENGPAGE = "^.*(%[A-Fa-f0-9][A-Fa-f0-9])+.*$";
    private final static int MAXURLLENGTH = 500;
    private final static int MAXSIZE = 1000000;

    // 1. the field always not change (total: 2)
    // The database directory
    private String db_directory;
    // Count of how many url we download
    private AtomicInteger count;
    // only when finish we close them
    private PrintStream links_writer = null;
    private PrintStream title_writer = null;
    private PrintStream meta_writer = null;
    private PrintStream body_writer = null;

    // 2. the field need refresh each time to start (total: 5)
    // Database 
    private BerkerleyDB database;
    // The queue store url for crawling; each host have one queue of url; Host : Queue
    private Map<Integer, LinkedBlockingQueue<CrawlUrl> > frontierQueue = Collections.synchronizedMap(new HashMap<Integer, LinkedBlockingQueue<CrawlUrl>>());
    // tmp set hold the url of robot
    private Set<String> visitedRobots = new HashSet<String>();
    // ExecuteService
    private ExecutorService service;
    // output write (write extract links to output)
    private PrintStream file_writer;
    
    // 3. control finish or not
    private boolean finishFlag = false;
    
    
    // 1. Only init once
    /**
     *   dir: database directory; path: the path to store title.txt (/output)
     * @throws IOException 
     */
    public WebCrawler(String dir) throws InterruptedException, IOException {
        this.db_directory = dir;
        BerkerleyDB.setEnvPath(db_directory);
        this.count = new AtomicInteger(1);
    }
    
    
    // 2. Init each round
    /**
     * Init the frontier queue, robots set, output_writer
     * @param outputPath
     * @throws InterruptedException
     * @throws IOException
     */
    public void init(String outputDir) throws InterruptedException, IOException {
        // init database
        this.database = BerkerleyDB.getInstance();
        // reset visitedRobots
        visitedRobots.clear();
        // reset frontier queue;
        for(int i = 0; i < THREADNUM; i++) {
          LinkedBlockingQueue<CrawlUrl> tmpqueue = new LinkedBlockingQueue<CrawlUrl>();
          frontierQueue.put(i, tmpqueue);
          LogClass.info("Queue Size: " + tmpqueue.size());
        }
        // reset Threadpool
        service = Executors.newFixedThreadPool(THREADNUM);
        file_writer = new PrintStream(new FileOutputStream(outputDir, true));
    }
    
    public void closeFileWriter() {
    	file_writer.flush();
    	file_writer.close();
    }
    
   // only init once
    public void initWriter(String path) throws IOException {
        title_writer = new PrintStream(new FileOutputStream(path + "/title.txt", true));
        meta_writer = new PrintStream(new FileOutputStream(path + "/meta.txt", true));
        links_writer = new PrintStream(new FileOutputStream(path + "/pagelink.txt", true));
        body_writer = new PrintStream(new FileOutputStream(path + "/body.txt", true));
    }
	
    // only close once
    private void closeWriter() throws IOException {
    	// 1. flush
    	title_writer.flush();
    	meta_writer.flush();
    	links_writer.flush();
    	body_writer.flush();
    	// 2. close
        title_writer.close();
        meta_writer.close();
        links_writer.close();
        body_writer.close();
    }
    
    
    /**
     * Add the init_url into the queue
     * @param init_url
     * @param level
     * @throws MalformedURLException
     * @throws InterruptedException
     */
    public void initQueue(String hostname, String init_url) throws MalformedURLException, InterruptedException {
        int num = SHA1Partition.getThreadIndex(hostname);
        //int num = SHA1Partition.getWorkerIndex(hostname);
        LinkedBlockingQueue<CrawlUrl> tmpqueue = frontierQueue.get(num);
        CrawlUrl crawl_url = new CrawlUrl(init_url);
        tmpqueue.put(crawl_url);
        LogClass.info("init_url: " + init_url + " into section: " + num);
        frontierQueue.put(num, tmpqueue);
    }
    
    // 3. Run the web crawler
    public void run() throws IOException {
    	LogClass.info("Crawler Run");
        for(int i = 0; i < THREADNUM; i++)
          service.execute(new CrawlerThread(i, this, database, MAXSIZE));
        service.shutdown();
        try {
          while(! service.awaitTermination(100, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
        	e.printStackTrace();
        } finally {
        	// close database and writer
        	database.close();
        	closeFileWriter();
        	closeWriter();
        }
    }
    
    /**
     *  Process HTML document, add the url to the frontierQueue
     *  need to synchronize the output meta data, title, appenurl
     * @throws InterruptedException 
     */
    public void processHtmlDocument(Document doc, CrawlUrl crawl_url) throws IOException, InterruptedException {
        if(doc == null)
          return;
        String url = crawl_url.getUrl();
        System.out.println("Processing Html Document: " + url);
        Set<String> links_set = new HashSet<String>();
        outputBody(url, doc.body().text());
        // 1. Get all title and meta content tag
        String title = doc.title();
        if(validTitle(title)) {
        	// we will use this as title
            incrementCount();
            outputTitle(url, title.replaceAll("\t", ""));
        }
        else {
        	return;
        }
        // 2. if it is a valid url, we parse its meta and links
        String meta_data = "";
        Elements meta_list = doc.select("meta[name=description]");
        for(Element meta: meta_list)
           meta_data += meta.attr("content") + "; ";
        meta_list = doc.select("meta[name=keywords]");
        for(Element meta: meta_list)
            meta_data += meta.attr("content") + "; ";
        if(! meta_data.equals(""))
            outputMetaTag(url, meta_data.replaceAll("\t", ""));
        // 2. Get all tag <a>
        Elements links = doc.select("a[href]");
        for(Element link: links) {
            String abs_url = link.absUrl("href");
            if(abs_url.equals(""))
                continue;
            abs_url = UrlNormalizer.getCanonicalUrl(null, abs_url);
            //System.out.println(abs_url);
            if(abs_url == null)
                continue;
            String encoded_url = SHA1.encodeString(abs_url);
            // if this is valid url, we add this to link
            if(validUrl(abs_url, encoded_url, false)) {
            	//System.out.println(abs_url);
            	database.storeUrl(encoded_url, abs_url, false);
                writeUrl(abs_url);
                links_set.add(abs_url);
            }
        }
        writeUrlLinks(url, links_set);
    }
    
    private boolean validTitle(String title) {
    	if(title == null || title.length() == 0)
    		return false;
    	for(int i = 0; i < title.length(); i++) {
    		if(title.codePointAt(i) > 127)
    			return false;
    	}
    	return true;
    }
    private synchronized void writeUrl(String url) throws IOException {
    	String hostname = UrlNormalizer.getHostName(url);
        String output = hostname + "\t" + url + CRLF;
        file_writer.print(output);
        //output_writer.flush();
    }
    
    private synchronized void outputBody(String url, String body) throws IOException {
    	String output = url + "\t" + body + CRLF;
    	body_writer.print(output);
    }
    
    private synchronized void outputMetaTag(String url, String meta_data) throws IOException {
        String output = url + "\t" + meta_data + CRLF;
        meta_writer.print(output);
        //meta_writer.flush();
    }

    private synchronized void outputTitle(String url, String title) throws IOException {
        String output = url + "\t" + title + CRLF;
        title_writer.print(output);
        //title_writer.flush();
        database.storeTitle(url, title);
    }

    private synchronized void writeUrlLinks(String url, Set<String> links_set) throws IOException {
    	links_writer.print(url + "\t");
        for(String s : links_set) 
            links_writer.print(s + " ");
        links_writer.print(CRLF);
        //links_writer.flush();
    }


    // 4. Overwrite
    public synchronized boolean isDone() {
      return frontierQueue.isEmpty() || finishFlag;
    }

    public synchronized int getCount() {
      return count.get();
    }

    public synchronized void incrementCount() {
      count.incrementAndGet();
    }

    public synchronized LinkedBlockingQueue<CrawlUrl> getLinkedBlockingQueue(int number) {
        return frontierQueue.get(number);
    }

    public synchronized boolean deleteLinkedBlockingQueue(int number) {
        LinkedBlockingQueue<CrawlUrl> delete_queue = frontierQueue.remove(number);
        return delete_queue != null;
    }

    public synchronized boolean containsRobot(String robotUrl) {
        return visitedRobots.contains(robotUrl);
    }

    public synchronized void addRobot(String robotUrl) {
        visitedRobots.add(robotUrl);
    }

	public synchronized boolean validUrl(String abs_url, String encoded_url, boolean flag) {
        if(abs_url == null)
            return false;
        // 1. if it is not english encode page or it contains # or ?
        if(abs_url.matches(NONENGPAGE) || abs_url.contains("#") || abs_url.contains("?"))
        	return false;
        // 2. if we already download this url
        UrlText ut =  database.getUrl(encoded_url);
        if(flag) {
        	// if we have ut and ut is visited
	        if(ut != null && ut.isVisited())
	        	return false;
        } else {
        	// if we have ut return false
        	if(ut != null)
        		return false;
        }
        // 3. length < max, schema is http
        int length = abs_url.length();
        return length < MAXURLLENGTH && UrlNormalizer.schemaIsHttp(abs_url) && ! abs_url.matches(CGIPATTERN);
	}
	
	public void setFinishFlag() { finishFlag = true; }
    
	public int getThreadNum() { return THREADNUM; }
    
	/*
    public static void main(String[] args) throws InterruptedException, IOException {
    	WebCrawler crawler = new WebCrawler("/home/cloudera/Documents/berkerleydb", 10, 3, 1);
    	crawler.init("test");
    	crawler.initQueue("http://www.upenn.edu/", 1);
    	crawler.run();
    	crawler.init("test");
    	crawler.initQueue("http://www.upenn.edu/#content", 1);
    	crawler.initQueue("http://www.upenn.edu/#", 1);
    	crawler.run();
    }
    */
  
}
