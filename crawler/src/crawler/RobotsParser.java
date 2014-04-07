package crawler;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import storage.BerkerleyDB;
import storage.DateTransfer;
import storage.RobotText;

public class RobotsParser {
	// have the database to store the robots
	private BerkerleyDB database;
	public static final String PATTERN = "user-agent:.*";
	public static final String PATTERN1 = "disallow:.*";
	public static final String PATTERN2 = "crawl-delay:.*";
	
	public RobotsParser() { }
	public RobotsParser(BerkerleyDB database) {
		this.database = database;
	}
	
	// crawler each time first to got the server
	public boolean downloadRobot(String rurl) throws Exception {
		WebClient wc = new WebClient();
		String url = "http://" + UrlNormalize.getHostName(rurl) + "/robots.txt";
		// if it doesn't modified, return true
		if(! isModified(url))
			return true;
		// Download robots.txt
		String tmp = wc.download(url);
		if(tmp.equals(""))
			return false;
		String[] response = tmp.split("\n");
		// init delay and time and set
		int delay = 0;
		String time = DateTransfer.transferDate(new Date());
		Set<String> set = new HashSet<String>();
		boolean flag = false;
		boolean iscis455 = false;
		// creat the robot
		for(String s: response) {
			String content;
			// if it is user agent
			if(s.toLowerCase().matches(PATTERN)) {
				// get the content
				content = getContent(s).toLowerCase();
				// if we found cis455crawler
				if(content.equals("cis455crawler")) {
					flag = true;
					// if it is first time, we clear the set
					if(! iscis455) {
						set = new HashSet<String>();
						delay = 0;
					}
					iscis455 = true;
				}
				// if it is * and we never met cis455crawler
				else if(content.equals("*") && ! iscis455)
					flag = true;
				else flag = false;
			}
			// if flag == true, it means we should follow this rule
			if(s.toLowerCase().matches(PATTERN1) && flag == true) {
				content = getContent(s);
				set.add(content);
			}
			else if(s.toLowerCase().matches(PATTERN2) && flag == true) {
				delay = Integer.parseInt(getContent(s));
			}
		}
		System.out.println("Store Url: " + url);
		return database.storeRobot(url, delay, time, set);
	}
	
	// update Robot time
	public synchronized boolean updateRobot(String url) throws Exception {	
		if(url == null)
			return false;
		RobotText rt = getRobot(url);
		if(rt == null)
			return true;
		url = "http://" + UrlNormalize.getHostName(url) + "/robots.txt";
		String time = DateTransfer.transferDate(new Date());
		return database.storeRobot(url, rt.getDelay(), time, rt.getDisallow());
	}
		
	// get robot from database by url
	public RobotText getRobot(String rurl) throws Exception {
		String url = "http://" + UrlNormalize.getHostName(rurl) + "/robots.txt";
		System.out.println(url);
		RobotText rt = database == null ? null : database.getRobot(url);
		return rt;
	}
	
	// if url is allowed in the robot Text
	public boolean allowUrl(RobotText rt, String url) throws MalformedURLException {
		// the server don't have robot
		if(rt == null)
			return true;
		// get the url path
		String path = UrlNormalize.getPath(url);
		for(String s : rt.getDisallow()) {
			if(path.startsWith(s))
				return false;
		}
		return true;
	}
	
	// return the time we need to wait for the delay (>0), or we are ready(<=0)
	public long timeAllow(RobotText rt) {
		// the server don't have robot
		if(rt == null)
			return 0;
		long current = new Date().getTime();
		long accessRobot = DateTransfer.transferString(rt.getTime()).getTime();
		int delay = rt.getDelay();
		return (delay*1000 - (current-accessRobot));
	}

	
	private String getContent(String s) {
		int index = s.indexOf(":");
		if(index >= s.length()) return s;
		else {
			return s.substring(index+1).trim();
		}
	}

	// test if modified since
	private boolean isModified(String url) {
		RobotText rt = database == null ? null : database.getRobot(url);
		if(rt == null)
			return true;
		// use head if-modified-since
		WebClient wc = new WebClient();
		HeadNode hn;
		hn = wc.inspectHeader(url, DateTransfer.getDate(rt.getTime()));
		// url is not right
		if(hn == null)
			return true;
		// Not modified
		else if(hn.getState() == 1)
			return false;
		else
		 return true;
	}
	
	public static void main(String[] args) throws Exception {
		BerkerleyDB db = new BerkerleyDB("/Users/ChenyangYu/Documents/Upenn/CIS555/berkerlydb");
		RobotsParser rp = new RobotsParser(db);
		System.out.println(rp.downloadRobot("http://crawltest.cis.upenn.edu/"));
	}
	
	
}
