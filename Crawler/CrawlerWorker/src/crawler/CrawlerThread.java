package crawler;

import edu.upenn.cis455.mapreduce.worker.LogClass;
import general.UrlNormalizer;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jsoup.nodes.Document;
import storage.BerkerleyDB;
import general.DateTransfer;
import hash.SHA1;
import storage.HtmlDocument;
import storage.RobotText;
import storage.UrlText;
import crawldatatype.CrawlData;
import crawldatatype.CrawlInfo;
import crawldatatype.CrawlUrl;
import crawldatatype.HeadNode;

public class CrawlerThread implements Runnable{
  
  // assgined number to this thread (this thread in response of the queue corresponds the number)
  private int number;
  private CrawlerInterface crawler;
  private BerkerleyDB database;
  private int max_size;
  // web client and document retriever to download
  private WebClient client;
  private DocumentRetriever document_retriever;
  // robotParser to validate the url
  RobotsParser robotParser;
  
  
  public CrawlerThread(int num, CrawlerInterface ci, BerkerleyDB database, int max_size) {
      this.number = num;
      this.crawler = ci;
      this.database = database;
      this.max_size = max_size;
      this.client = new WebClient();
      this.document_retriever = new DocumentRetriever();
      this.robotParser = new RobotsParser(database);
  }
  
  public void run() {
      while(! crawler.isDone()) {
          try {
              init();
          } catch (Exception e) {
              // have exception, maybe there are something still in the queue
              LogClass.info("Thread " + Thread.currentThread().getId() + " Thread Error");
              e.printStackTrace();
          }
      }
  }
  
  public void init(){
      try {
          // put the url into database
          while(! crawler.isDone()) {
              LogClass.info("Thread " + Thread.currentThread().getId() +" Begin /********************************/");
              LogClass.info("Thread " + Thread.currentThread().getId() +" Crawled Success Number: " + crawler.getCount() + "; Dequeu: " + number);
              // 1. Take item from the Queue and get the delay
              CrawlInfo crawl_info = dequeue(number);
              long delay = crawl_info.getDelay();
              CrawlUrl crawl_url = crawl_info.getCrawlUrl();
              // 2. Check delay and crawl_url (if crawl_url == null continue)
              if(delay > 0) {
                LogClass.info("Thread " + Thread.currentThread().getId() + " Waiting for Crawl-Delay: " + delay + " milliseconds");
                Thread.sleep(delay);
              }
              else if(delay == 0)
                Thread.sleep(500);
              // my queue is empty, don't have other to download
              if(crawl_url == null) {
                continue;
              } 
              //  3. head query the url (if visited or url is not valid, continue)
              String absolute_url = crawl_url.getUrl();
              String encoded_url = SHA1.encodeString(absolute_url);
              if(! crawler.validUrl(absolute_url, encoded_url, true))
            	  continue;
              // check header type and if modified or find new location
              HeadNode head_node = inspectUrl(absolute_url, encoded_url);
              if(head_node == null)
                continue;
              // 4. download the url document
              LogClass.info("Thread " + Thread.currentThread().getId() + " HeadNode status: " + head_node.getState() + "; Type valid: " + head_node.typeValid());
              Document doc = downloadUrl(head_node);
              if(doc == null)
                continue;
              else {
                crawl_url = new CrawlUrl(head_node.getAbsoluteUrl());
                crawler.processHtmlDocument(doc, crawl_url);
              }
          }
      } catch(Exception e) {
        e.printStackTrace();
      } finally {
        LogClass.info("End /*********************************/");
      }
  }

   /**
   * Dequeue from the frontier queue (1. find the url we have not visited and 2. it valid for robot.txt)
   */
  public CrawlInfo dequeue(int num) throws Exception {
      // store the best delay and the mapping host name
      CrawlUrl best_crawl_url = null;
      long best_delay = Long.MAX_VALUE;
      
      // 1. Get the queue
      LinkedBlockingQueue<CrawlUrl> queue = crawler.getLinkedBlockingQueue(number);
      // if the queue is empty
      if(queue == null || queue.isEmpty()) {
          // remove this entry
          crawler.deleteLinkedBlockingQueue(number);
          return new CrawlInfo(null, 5000); 
      }
      while(true) {
          // 2. Check if we seen the url or the url already got the depth
          // get the crawl url from queue
          best_crawl_url = queue.poll(5, TimeUnit.SECONDS);
          if(best_crawl_url == null) {
            // means the queue is empty
            LogClass.info("Thread " + Thread.currentThread().getId() + "get url is null");
            break;
          }
          // get url
          String url = best_crawl_url.getUrl();
          String hostname = UrlNormalizer.getHostName(url);
          String encoded_url = SHA1.encodeString(url);
          // Already Seen the URL or already got the depth, this maybe the case we add the same url into queue
          UrlText ut = database.getUrl(encoded_url);
          if( (ut != null && ut.isVisited())) {
              // take the url from queue and set crawl_url = null
              if(queue == null || queue.isEmpty())
                  crawler.deleteLinkedBlockingQueue(number);
              continue;
          }
          
          //3. Check if this url compatible the robotText
          // get robot text of this url
          String robotUrl = "http://" + hostname + "/robots.txt";
          RobotText rt = robotParser.getRobot(robotUrl);
          if(rt == null && ! crawler.containsRobot(robotUrl)) {
              robotParser.downloadRobot(robotUrl);
              rt = robotParser.getRobot(robotUrl);
          }  
          LogClass.info("Url: " + url + "; Robot Url: " + robotUrl + " is null: " + (rt == null));
          //4. the server don't have robots.txt
          if(rt == null) {
              best_delay = 0;
              crawler.addRobot(robotUrl);
              break;
          }
          //5. the server have robots.txt
          else {
              // don't allow this url
              if(! robotParser.allowUrl(rt, url)) {
                  LogClass.info("Allow Url: " + url);
                  continue;
              }
              // the first time got the robot.txt, we ignore the delay return delay;
              if(! crawler.containsRobot(robotUrl)) {
                  // the first time got the robot.txt, we ignore the delay
                  crawler.addRobot(robotUrl);
                  best_delay = 0;
                  LogClass.info("add robot url: " + robotUrl);
                  break;
              }
              //delay = delay < 0 ? robotParser.timeAllow(rt) : Math.min(delay, robotParser.timeAllow(rt));
              // delay is bigger than robotParser.timeAllow
              long timeallow = robotParser.timeAllow(rt);
              best_delay = timeallow <= 0 ? 0 : timeallow;
              break;
          }
      }
      // we don't have anything to process
      if(best_delay == Long.MAX_VALUE)
          return new CrawlInfo(null, 5000);
      // update the time we crawl this host
      robotParser.updateRobot(best_crawl_url.getUrl(), best_delay > 0 ? best_delay : 0);
      LogClass.info("Best Url: " + best_crawl_url.getUrl() + "; Best delay: " + best_delay);
      return new CrawlInfo(best_crawl_url, best_delay);
  }
  
  /**
   * Head to the url, to see if it is http 200 ok or 304 not modified
   * @param absolute_url
   * @param encoded_url
   * @return
   */
  private HeadNode inspectUrl(String absolute_url, String encoded_url) {
      // 0. This must be absolute url, as we normalize it when extract the link
      HeadNode head_node = checkHeader(absolute_url, encoded_url);
      database.storeUrl(encoded_url, absolute_url, true);
      int i = 3;
      while(head_node != null && head_node.getLocation() != null && i > 0) {
          String redirect_url = head_node.getLocation();
          // 1. get absolute redirected url, and add the new location to VisitedUrl set
          absolute_url = UrlNormalizer.getCanonicalUrl(absolute_url, redirect_url);
          if(absolute_url == null)
              return null;
          encoded_url = SHA1.encodeString(absolute_url);
          // 2. if the new redirect is not http or we visited the url, return null
          if(! crawler.validUrl(absolute_url, encoded_url, true))
        	  return null;
          // 3. else we add the new redirect url and check again
          else {
              // Query the redirect location again, and add this as visited
              head_node = checkHeader(absolute_url, encoded_url);
              database.storeUrl(encoded_url, absolute_url, true);
              i--;
          }
      }
      // 4. set the absolute url to download
      if(head_node != null) {
          head_node.setAbsoluteUrl(absolute_url);
          head_node.setEncodeUrl(encoded_url);
          return head_node;
      }
      else return head_node;
  }
  
  /**
   * Check the Header Node
   * @param url
   * @param encoded_url
   * @return
   */
  private HeadNode checkHeader(String url, String encoded_url) {
      // Get HtmlDocument 
      LogClass.info("Thread " + Thread.currentThread().getId() + " Check URL: " + url);
      HtmlDocument html_data = database.getHtmlDocument(encoded_url); 
      // Query Header
      if(html_data == null)
          // we don't have the record in database
          return client.inspectHeader(url, null);
      // return the HeadNode
      else {
          String dateString = DateTransfer.getDate(html_data.getDate());
          return client.inspectHeader(url, dateString);
      }
  }

  private Document downloadUrl(HeadNode head_node) {
	  try {
	      String absolute_url = head_node.getAbsoluteUrl();
	      String encoded_url = head_node.getEncodeUrl();
	      // Case 1. if not modified
	      if(head_node.getState() == 1) {
	          LogClass.info("Thread " + Thread.currentThread().getId() + " " + absolute_url + ": Not Modified");
	          // 1. Get html_data html_data can't be null as it is not modified
	          HtmlDocument html_data = database.getHtmlDocument(encoded_url);
	          String content = html_data.getHtmlData();
	          String encoded_content = SHA1.encodeString(content);
	          // 2. if the content is not visited
	          Document doc = null;
	          if(database.getContent(encoded_content) == null) {
	              database.storeContent(encoded_content, encoded_url);
	          } 
	          else {
	              LogClass.info("Thread " + Thread.currentThread().getId() + " " + absolute_url + ": Parsing");
	              //doc = Jsoup.parse(content);
	              //crawler.incrementCount();
	          }
	          // 3. update information in database, return null(we alread visited)
	          database.storeHtmlDocument(encoded_url, absolute_url , content, new Date());
	          return doc;
	      }
	      // Case 2. response is ok
	      else if(head_node.getState() == 0){
	        // 1. type is not html, we ignore
	        if(! head_node.typeValid() || (int)(head_node.getLength() / Math.pow(10, 6)) > max_size) {
	          return null;
	        }
	        else {
	          // 2. retrieve document
	          CrawlData crawl_data = document_retriever.retrieveData(absolute_url);
	          if(crawl_data == null)
	              return null;
	          String content = crawl_data.getResponse();
	          Document doc = crawl_data.getDocument();
	          String encoded_content = SHA1.encodeString(content);
	          // 3. if we have not seen the content
	          if(database.getContent(encoded_content) == null) {
	              database.storeContent(encoded_content, encoded_url);
	              LogClass.info("Thread " + Thread.currentThread().getId() + " " + absolute_url + ": Downloading");
	              // 4. store in the database, maybe different url store same content
	              database.storeHtmlDocument(encoded_url, absolute_url, content, new Date());
	              return doc;
	          }
	          else {
	            LogClass.info("Thread " + Thread.currentThread().getId() + " visited the content");
	            return null;
	          }
	        }
	      }
	      else {
	        return null;
	      }
	  } catch (Exception e) {
		  LogClass.info("Thread " + Thread.currentThread().getId() + " Error");
		  e.printStackTrace();
		  return null;
	  } 
	  
  } 
}
