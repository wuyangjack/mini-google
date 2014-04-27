package crawler;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.nodes.Document;

import crawldatatype.*;

public interface CrawlerInterface {
	
	
	/**
	 * Proces the HTML document, extract the link, write the title and meta data to output
	 * @param doc
	 * @param crawl_url
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void processHtmlDocument(Document doc, CrawlUrl crawl_url) throws IOException, InterruptedException;
	
	/**
	 * Return the linked blockingqueue associated to the thread
	 * @param number
	 * @return
	 */
	public LinkedBlockingQueue<CrawlUrl> getLinkedBlockingQueue(int number);
	
	/**
	 * Delete the linkedBlockingqueue from frontier queue
	 * @param number
	 * @return
	 */
	public boolean deleteLinkedBlockingQueue(int number);
	
	/**
	 * Check if we have already download the roboturl
	 * @param robotUrl
	 * @return
	 */
	public boolean containsRobot(String robotUrl);
	
	/**
	 * Add robotUrl to robotUrl set
	 * @param robotUrl
	 */
	public void addRobot(String robotUrl);
	
	/**
	 * Return if the queue is empty or we got our max crawl number
	 * @return
	 */
	public boolean isDone();
	
	/**
	 * Get the number of page we current crawled
	 * @return
	 */
	public int getCount();
	
	/**
	 * Increase count by 1
	 * @return
	 */
	public void incrementCount();
	
	/**
	 * 
	 * @param abs_url
	 * @param encoded_url
	 * @param flag
	 * @return
	 */
	public boolean validUrl(String abs_url, String encoded_url, boolean flag);
	
	public void setFinishFlag();
	
}
