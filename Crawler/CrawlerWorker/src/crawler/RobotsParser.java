package crawler;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import crawldatatype.HeadNode;

import storage.BerkerleyDB;
import general.DateTransfer;
import storage.RobotText;
import general.UrlNormalizer;

public class RobotsParser {
  // have the database to store the robots
  private BerkerleyDB database;
  public static final String PATTERN = "user-agent:.*";
  public static final String PATTERN1 = "disallow:.*";
  public static final String PATTERN2 = "allow:.*";
  public static final String PATTERN3 = "crawl-delay:.*";
  
  public RobotsParser() { }
  public RobotsParser(BerkerleyDB database) {
      this.database = database;
  }
  
  // crawler each time first to got the server
  public boolean downloadRobot(String rurl) throws Exception {
      // 1. Get webclient and get roboturl
      WebClient wc = new WebClient();
      String url = getRobotUrl(rurl);
      // init delay and time and set
      int delay = 0;
      String time = DateTransfer.transferDate(new Date());
      Set<String> disallow = new HashSet<String>();
      Set<String> allow = new HashSet<String>();
      // 2. if it doesn't modified, return true
      if(! isModified(url))
        return true;
      // 3. Download robots.txt
      String robot_content = wc.download(url);
      if(robot_content.equals("")) {
          return database.storeRobot(url, delay, time, disallow, allow);
      }
      String[] response = robot_content.split("\n");
      // if we add into the disallow or allow set
      boolean flag = false;
      // if we have meet the user-agent cis455
      boolean iscis455 = false;
      // creat the robot
      for(String s: response) {
        s = s.trim();
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
                    disallow = new HashSet<String>();
                    allow = new HashSet<String>();
                    delay = 0;
                }
                iscis455 = true;
            }
            // if it is * and we never met cis455crawler
            else if(content.equals("*") && ! iscis455)
                flag = true;
            else flag = false;
            continue;
        }
        // if flag == true, it means we should follow this rule
        if(s.toLowerCase().matches(PATTERN1) && flag == true) {
            content = getContent(s);
            if(! content.equals(""))
            	disallow.add(getPattern(content));
        }
        else if(s.toLowerCase().matches(PATTERN2) && flag == true) {
            content = getContent(s);
            if(! content.equals(""))
            	allow.add(getPattern(content));
        }
        else if(s.toLowerCase().matches(PATTERN3) && flag == true) {
            delay = Integer.parseInt(getContent(s));
        }
      }
      System.out.println("Store Url: " + url);
      return database.storeRobot(url, delay, time, disallow, allow);
  }
  
  // update Robot time
  public boolean updateRobot(String rurl, long delay) throws Exception {  
    if(rurl == null)
        return false;
    RobotText rt = getRobot(rurl);
    if(rt == null)
        return true;
    String url = getRobotUrl(rurl);
    String time = DateTransfer.transferDate(new Date(System.currentTimeMillis() + delay));
    return database.storeRobot(url, rt.getDelay(), time, rt.getDisallow(), rt.getAllow());
  }
    
  // get robot from database by url
  public RobotText getRobot(String rurl) throws Exception {
      String url = getRobotUrl(rurl);
      RobotText rt = database == null ? null : database.getRobot(url);
      return rt;
  }
  
  private String getRobotUrl(String url) throws MalformedURLException {
      return "http://" + UrlNormalizer.getHostName(url) + "/robots.txt";
  }
  
  // if url is allowed in the robot Text
  public boolean allowUrl(RobotText rt, String url) throws MalformedURLException {
      // don't have robots.txt
      if(rt == null)
          return true;
      // get the url path
      String path = UrlNormalizer.getFile(url).toLowerCase();
      for(String s : rt.getAllow()) {
          if(path.startsWith(s) || path.matches(s))
              return true;
      }
      
      for(String s : rt.getDisallow()) {
          if(path.startsWith(s) || path.matches(s))
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
      long delay = rt.getDelay();
      return (delay*1000 - (current-accessRobot));
  }

  
  private String getContent(String s) {
      int index = s.indexOf(":");
      if(index >= s.length()) return "";
      else {
          return s.substring(index+1).trim();
      }
  }
  
  private String getPattern(String content) {
      StringTokenizer st = new StringTokenizer(content,".*?", true);
      StringBuffer sb = new StringBuffer();
      sb.append("^");
      while(st.hasMoreTokens()) {
          String s = st.nextToken();
          if(s.equals("?") || s.equals(".")) {
              sb.append("\\");
              sb.append(s);
          }
          else if(s.equals("*")) {
              sb.append(".*");
          }
          else 
              sb.append(s);
      }
      sb.append(".*");
      return sb.toString();
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
    BerkerleyDB.setEnvPath("/home/cloudera/Documents/testdb");
    BerkerleyDB db = BerkerleyDB.getInstance();
    RobotsParser rp = new RobotsParser(db);
    System.out.println(rp.downloadRobot("http://www.upenn.edu"));
    RobotText rt = rp.getRobot("http://www.upenn.edu");
    //System.out.println("Allow: " + rp.allowUrl(rt, "http://crawltest.cis.upenn.edu/marie/private/"));
    Set<String> set = rt.getDisallow();
    System.out.println("Set size: " + set.size());
    String url = "/us/corporate/customers/customersearch/index.html?";
    for(String s : set) {
      System.out.println(s);
      System.out.println(url.matches(s));
    }
    System.out.println(rp.allowUrl(rt, url));
    db.close();
  }
}
