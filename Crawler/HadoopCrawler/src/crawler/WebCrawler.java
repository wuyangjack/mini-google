package crawler;

import crawldatatype.*;

import java.io.IOException;
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

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import general.UrlNormalizer;
import hash.SHA1;
import hash.SHA1Partition;
import storage.*;

public class WebCrawler implements CrawlerInterface{
  
  
    private final static String CRLF = "\r\n";
    // we need to get the BigInteger of range and section
    private final static int THREADNUM = 5;
    // don't crawl url include /cgi-bin/
    private final static String CGIPATTERN = ".*/cgi-bin/.*";
    private final static int MAXURLLENGTH = 500;
    private final static String NONENGPAGE = "^.*(%[A-Fa-f0-9][A-Fa-f0-9])+.*$";

    // 1. the field always not change (total: 6)
    // The database directory
    private String db_directory;
    // The range of our SHA1
    //private BigInteger range;
    // The section of SHA1(each thread)
    //private BigInteger section;
    // Max size of document
    private int max_size;
    // Max number
    private int max_num;
    // Count of how many url we download
    private AtomicInteger count;

    // 2. the field need refresh each time to start (total: 5)
    // Database 
    private BerkerleyDB database;
    // The queue store url for crawling; each host have one queue of url; Host : Queue
    private Map<Integer, LinkedBlockingQueue<CrawlUrl> > frontierQueue = Collections.synchronizedMap(new HashMap<Integer, LinkedBlockingQueue<CrawlUrl>>());
    // tmp set hold the url of robot
    private Set<String> visitedRobots = new HashSet<String>();
    // ExecuteService
    private ExecutorService service;
    // output write
    private FSDataOutputStream file_writer;
    /*
    private PrintWriter links_writer = null;
    private PrintWriter title_writer = null;
    private PrintWriter meta_writer = null;
    private PrintWriter body_writer = null;
    */
    private FSDataOutputStream links_writer = null;
    private FSDataOutputStream title_writer = null;
    private FSDataOutputStream meta_writer = null;
    private FSDataOutputStream body_writer = null;
    
    /**
     *   dir: database directory; size: document max size; num: max document number; worker_num: hadoop worker number
     * @throws IOException 
     */
    public WebCrawler(String dir, int size, int num, int worker_num, FileSystem fs) throws InterruptedException, IOException {
        this.db_directory = dir;
        BerkerleyDB.setEnvPath(db_directory);
        this.max_size = size;
        this.max_num = num;
        // SHA1Partition.setRange(worker_num);
        //SHA1Partition.setSection(THREADNUM);
        // Set Thread range
        SHA1Partition.setRange(THREADNUM);
        this.count = new AtomicInteger(1);
        links_writer = fs.create(new Path("/data/pagelink.txt"), true);
        title_writer = fs.create(new Path("/data/title.txt"), true);
        meta_writer = fs.create(new Path("/data/meta.txt"), true);
        body_writer = fs.create(new Path("/data/body.txt"), true);
    }

    /**
     * Init the frontier queue, robots set, output_writer
     * @param outputPath
     * @throws InterruptedException
     * @throws IOException
     */
    public void init(FSDataOutputStream file_writer) throws InterruptedException, IOException {
        // init database
        this.database = BerkerleyDB.getInstance();
        // reset visitedRobots
        visitedRobots.clear();
        // reset frontier queue;
        for(int i = 0; i < THREADNUM; i++) {
          LinkedBlockingQueue<CrawlUrl> tmpqueue = new LinkedBlockingQueue<CrawlUrl>();
          frontierQueue.put(i, tmpqueue);
        }
        // reset Threadpool
        service = Executors.newFixedThreadPool(THREADNUM);
        this.file_writer = file_writer;
        //initWriter();
    }

    /*
    private void initWriter() throws IOException {
        title_writer = new PrintWriter(new FileWriter("/home/cloudera/Documents/data/title.txt", true));
        meta_writer = new PrintWriter(new FileWriter("/home/cloudera/Documents/data/meta.txt", true));
        links_writer = new PrintWriter(new FileWriter("/home/cloudera/Documents/data/pagelink.txt", true));
        body_writer = new PrintWriter(new FileWriter("/home/cloudera/Documents/data/body.txt", true));
    }
	*/
    public void closeWriter() throws IOException {
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
    public void initQueue(String init_url, int level) throws MalformedURLException, InterruptedException {
        // get the hostname, and put host name in the same queue
        String hostname = UrlNormalizer.getHostName(init_url);
        //int num = SHA1Partition.getThreadIndex(hostname);
        int num = SHA1Partition.getWorkerIndex(hostname);
        LinkedBlockingQueue<CrawlUrl> tmpqueue = frontierQueue.get(num);
        CrawlUrl crawl_url = new CrawlUrl(init_url, level);
        tmpqueue.put(crawl_url);
        System.out.println("init_url: " + init_url + " into section: " + num);
        frontierQueue.put(num, tmpqueue);
    }
    
    public void run() {
        for(int i = 0; i < THREADNUM; i++)
          service.execute(new CrawlerThread(i, this, database, max_size));
        service.shutdown();
        try {
          while(! service.awaitTermination(100, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
        	e.printStackTrace();
        } finally {
        	// close database and writer
        	database.close();
        	//closeWriter();
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
        // 1. Get all tag <a>
        Elements links = doc.select("a[href]");
        for(Element link: links) {
            String abs_url = link.absUrl("href");
            if(abs_url.equals(""))
                continue;
            abs_url = UrlNormalizer.getCanonicalUrl(null, abs_url);
            if(abs_url == null)
                continue;
            String encoded_url = SHA1.encodeString(abs_url);
            if(validUrl(abs_url, encoded_url)) {
            	database.storeUrl(encoded_url, abs_url, false);
                writeUrl(abs_url,  crawl_url.getDepth() + 1);
                links_set.add(abs_url);
            }
        }
        writeUrlLinks(url, links_set);
        // 2. Get all title and meta content tag
        String title = doc.title();
        if(title != null && ! title.equals(""))
            outputTitle(url, title.replaceAll("\t", ""));
        String meta_data = "";
        Elements meta_list = doc.select("meta[name=description]");
        for(Element meta: meta_list)
           meta_data += meta.attr("content") + "; ";
        meta_list = doc.select("meta[name=keywords]");
        for(Element meta: meta_list)
            meta_data += meta.attr("content") + "; ";
        if(! meta_data.equals(""))
            outputMetaTag(url, meta_data.replaceAll("\t", ""));
    }
    
    private synchronized void outputBody(String url, String body) throws IOException {
    	String output = url + "\t" + body + CRLF;
    	byte[] array = output.getBytes();
        body_writer.write(array);
    	//body_writer.print(output);
    }
    
    private synchronized void outputMetaTag(String url, String meta_data) throws IOException {
        String output = url + "\t" + meta_data + CRLF;
        byte[] array = output.getBytes();
        meta_writer.write(array);
        //meta_writer.print(output);
        //meta_writer.flush();
    }

    private synchronized void outputTitle(String url, String title) throws IOException {
        String output = url + "\t" + title + CRLF;
        byte[] array = output.getBytes();
        title_writer.write(array);
        //title_writer.print(output);
        //title_writer.flush();
        database.storeTitle(url, title);
    }
    

    private synchronized void writeUrl(String url, int depth) throws IOException {
        String output = url + "\t" + depth + CRLF;
        byte[] array = output.getBytes();
        file_writer.write(array);
        //output_writer.flush();
    }
    

    private synchronized void writeUrlLinks(String url, Set<String> links_set) throws IOException {
        /*
    	links_writer.print(url + "\t");
        for(String s : links_set) 
            links_writer.print(s + " ");
        links_writer.print(CRLF);
        */
    	String output = url + "\t";
    	links_writer.write(output.getBytes());
    	for(String s: links_set) {
    		output = s + " ";
    		links_writer.write(output.getBytes());
    	}
    	links_writer.write(CRLF.getBytes());
        //links_writer.flush();
    }

    private boolean validUrl(String abs_url, String encoded_url) {
        if(abs_url == null)
            return false;
        // if it is not english encode page
        if(abs_url.matches(NONENGPAGE))
        	return false;
        int length = abs_url.length();
        return length < MAXURLLENGTH && UrlNormalizer.schemaIsHttp(abs_url) && database.getUrl(encoded_url) == null && ! abs_url.matches(CGIPATTERN);
    }

    @Override
    public synchronized boolean isDone() {
      return frontierQueue.isEmpty() || count.get() >= max_num;
    }

    @Override
    public synchronized int getCount() {
      return count.get();
    }

    @Override
    public synchronized void incrementCount() {
      count.incrementAndGet();
    }

    @Override
    public synchronized LinkedBlockingQueue<CrawlUrl> getLinkedBlockingQueue(int number) {
        return frontierQueue.get(number);
    }

    @Override
    public synchronized boolean deleteLinkedBlockingQueue(int number) {
        LinkedBlockingQueue<CrawlUrl> delete_queue = frontierQueue.remove(number);
        return delete_queue != null;
    }

    @Override
    public synchronized boolean containsRobot(String robotUrl) {
        return visitedRobots.contains(robotUrl);
    }

    @Override
    public synchronized void addRobot(String robotUrl) {
        visitedRobots.add(robotUrl);
    }
    
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
