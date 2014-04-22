package storage;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import general.DateTransfer;
import java.io.File;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;


public class BerkerleyDB {
	
	public static final String HTMLDB = "html_db";
	public static final String ROBOTDB = "robot_db";
	public static final String URLDB = "url_db";
	public static final String CONTENTDB = "content_db";
	public static final String TITLEDB = "title_db";
	
	// singleton class
	private static BerkerleyDB database;
	private static String env_path;
	
	private Environment dbEnv;
	// hash url: url, content, date
	private Database htmlDB;
	private final ReentrantLock htmlLock = new ReentrantLock();
	// robot : time, disallow path
	private Database robotDB;
	//private ReentrantLock robotLock;
	// hash url : url (visited url)
	private Database urlDB;
	private final ReentrantLock urlLock = new ReentrantLock();
	// hash content : set hash urls
	private Database contentDB;
	private final ReentrantLock contentLock = new ReentrantLock();
	private Database titleDB;
	
	private BerkerleyDB() {
		initEnv();
		openDB();
	}
	
	public static BerkerleyDB getInstance() {
		if(database == null)
			database = new BerkerleyDB();
		return database;
	}
	
	public static boolean setEnvPath1(String env_path) {
		BerkerleyDB.env_path = env_path;
		return true;
	}
	
	public static void setEnvPath(String env_path) {
		BerkerleyDB.env_path = env_path;
	}
	
	public static String getEnvPath() {
		return env_path;
	}
	
	public void initEnv() {
		// create a configuration for DB environment
	      EnvironmentConfig envConf = new EnvironmentConfig();
	      // environment will be created if not exists
	      envConf.setAllowCreate(true);
	      envConf.setReadOnly(false);  
	      envConf.setTransactional(true); 
	  
	      // open/create the DB environment using config
	      dbEnv = new Environment(
	              new File(env_path), envConf);
	}
	
	public void openDB() {
		 try {
		      // create a configuration for DB
		      DatabaseConfig dbConf = new DatabaseConfig();
		      // db will be created if not exits
		      dbConf.setAllowCreate(true);
		      dbConf.setReadOnly(false);  
		      dbConf.setTransactional(true);
		      
		      // create/open DB using config
		     htmlDB = dbEnv.openDatabase(null, HTMLDB , dbConf);
		  	 robotDB = dbEnv.openDatabase(null, ROBOTDB, dbConf);
		  	 urlDB = dbEnv.openDatabase(null, URLDB, dbConf);
		  	 contentDB = dbEnv.openDatabase(null, CONTENTDB, dbConf);
		  	 titleDB = dbEnv.openDatabase(null, TITLEDB, dbConf);
		
		      
		 } catch (DatabaseException dbe) {
		      System.out.println("Error :" + dbe.getMessage());
		 }
	}
	
	public void closeEnv() {
		if (dbEnv != null) {  
		    dbEnv.sync();
		    dbEnv.cleanLog();
		    dbEnv.close();  
		    dbEnv = null;  
		}  
	}
	
	public void closeHtmlDB() {
		if (htmlDB != null) {  
			htmlDB.close();  
			htmlDB = null;  
		} 
	}

	public void closeRobotDB() {
		if(robotDB != null) {
			robotDB.close();
			robotDB = null;
		}
	}
	
	public void closeUrlDB() {
		if(urlDB != null) {
			urlDB.close();
			urlDB = null;
		}
	}
	
	public void closeContentDB() {
		if(contentDB != null) {
			contentDB.close();
			contentDB = null;
		}
	}
	
	
	public void close() {
		try {
			closeHtmlDB();
			closeRobotDB();
			closeUrlDB();
			closeContentDB();
			closeEnv();
		} catch(Exception e) {
			
		} finally {
			database = null;
		}
	}
	
	/**
	 * Store the HtmlDocument
	 * @param key
	 * @param data
	 * @param date
	 * @param isHtml
	 * @return
	 */
	public boolean storeHtmlDocument(String hash_url, String url, String data, Date date) {
		if(htmlDB == null)
			return false;
		try { 
			htmlLock.lock();
			String sdate = DateTransfer.transferDate(date);
			HtmlDocument hdoc = new HtmlDocument(url, data, sdate);
			HtmlTupleBinding htb = new HtmlTupleBinding();
			
		    DatabaseEntry key_entry = new DatabaseEntry();
		    DatabaseEntry data_entry = new DatabaseEntry();
		    
		    // bind the string to entry
		    StringBinding.stringToEntry(hash_url, key_entry);
		    htb.objectToEntry(hdoc,  data_entry);
	
		    
		    OperationStatus status = htmlDB.put(null, key_entry, data_entry); 
		    if(status == OperationStatus.SUCCESS)
		    	return true;
		    else
		    	return false;
		} catch(Exception e) {
			return false;
		} finally {
			htmlLock.unlock();
		}
	}
	/**
	 * Get HtmlDocument by key(url), one time can only one store
	 * @param key
	 * @return
	 */
	public HtmlDocument getHtmlDocument(String hash_url) {
		if(htmlDB == null)
			return null;	
		try {
			htmlLock.lock();
		    DatabaseEntry key_entry = new DatabaseEntry();
		    DatabaseEntry data_entry = new DatabaseEntry();
		    HtmlTupleBinding htb = new HtmlTupleBinding();
		    StringBinding.stringToEntry(hash_url, key_entry);
		    OperationStatus status = htmlDB.get(null, key_entry, data_entry, LockMode.DEFAULT);
		    
		    if(status == OperationStatus.SUCCESS) {
		    	HtmlDocument hdoc = htb.entryToObject(data_entry);
		    	return hdoc;
		    }
		    return null;
		} catch(Exception e) {
			return null;
		} finally {
			htmlLock.unlock();
		}
	}
	
	/**
	 * Delete Url Document by key(url)
	 * @param key
	 * @return
	 */
	public boolean deleteHtmlDocument(String hash_url) {
		if(htmlDB == null)
			return false;
		
	    DatabaseEntry key_entry = new DatabaseEntry();
	    StringBinding.stringToEntry(hash_url.toString(), key_entry);
	    OperationStatus status = htmlDB.delete(null, key_entry);
	    if(status == OperationStatus.SUCCESS) {
	    	return true;
	    }
	    return false;
	}
	
	public boolean storeRobot(String host, long delay, String time, Set<String> disallows, Set<String> allows) {
		if(robotDB == null)
			return false;
		RobotText rt = new RobotText(delay, time, disallows, allows);
		RobotTupleBinding rtb = new RobotTupleBinding();
		
	    DatabaseEntry key_entry = new DatabaseEntry();
	    DatabaseEntry data_entry = new DatabaseEntry();
	    
	    // bind the string to entry
	    StringBinding.stringToEntry(host, key_entry);
	    rtb.objectToEntry(rt,  data_entry);
	    
	    OperationStatus status = robotDB.put(null, key_entry, data_entry); 
	    if(status == OperationStatus.SUCCESS)
	    	return true;
	    else
	    	return false;
	}
	
	public RobotText getRobot(String key) {
		if(robotDB == null)
			return null;
	    DatabaseEntry key_entry = new DatabaseEntry();
	    DatabaseEntry data_entry = new DatabaseEntry();
	    RobotTupleBinding xtb = new RobotTupleBinding();
	    StringBinding.stringToEntry(key, key_entry);
	    OperationStatus status = robotDB.get(null, key_entry, data_entry, LockMode.DEFAULT);
	    if(status == OperationStatus.SUCCESS) {
	    	RobotText xdoc = xtb.entryToObject(data_entry);
	    	return xdoc;
	    }
	    return null;
	}
	
	public boolean deleteRobot(String key) {
		if(robotDB == null)
			return false;
	    DatabaseEntry key_entry = new DatabaseEntry();
	    StringBinding.stringToEntry(key, key_entry);
	    OperationStatus status = robotDB.delete(null, key_entry);
	    if(status == OperationStatus.SUCCESS) {
	    	return true;
	    }
	    return false;
	}
	
	public boolean storeUrl(String hash_key, String url, boolean visited) {
		if(urlDB == null)
			return false;
		try {
			urlLock.lock();
			UrlText ut = new UrlText(url, visited);
		    DatabaseEntry key_entry = new DatabaseEntry();
		    DatabaseEntry data_entry = new DatabaseEntry();
		    UrlTupleBinding utb = new UrlTupleBinding();
		    
		    // bind the string to entry
		    StringBinding.stringToEntry(hash_key, key_entry);
		    utb.objectToEntry(ut, data_entry);
		    
		    OperationStatus status = urlDB.put(null, key_entry, data_entry); 
		    if(status == OperationStatus.SUCCESS)
		    	return true;
		    else
		    	return false;
		} catch(Exception e) {
			return false;
		} finally {
			urlLock.unlock();
		}
	}
	
	public UrlText getUrl(String hash_key) {
		if(urlDB == null)
			return null;
		try {
			urlLock.lock();
		    DatabaseEntry key_entry = new DatabaseEntry();
		    DatabaseEntry data_entry = new DatabaseEntry();
		  
		    StringBinding.stringToEntry(hash_key, key_entry);
		    UrlTupleBinding utb = new UrlTupleBinding();
		    
		    OperationStatus status = urlDB.get(null, key_entry, data_entry, LockMode.DEFAULT);
		    if(status == OperationStatus.SUCCESS) {
		    	UrlText response = utb.entryToObject(data_entry);
		    	return response;
		    }
		    return null;
		} catch(Exception e) {
			return null;
		} finally {
			urlLock.unlock();
		}
	}
	
	public boolean deleteUrl(String hash_key) {
		if(urlDB == null)
			return false;
	    DatabaseEntry key_entry = new DatabaseEntry();
	    StringBinding.stringToEntry(hash_key, key_entry);
	    OperationStatus status = urlDB.delete(null, key_entry);
	    if(status == OperationStatus.SUCCESS) {
	    	return true;
	    }
	    return false;
	}
	
	public boolean storeContent(String hash_content, String hash_url) {
		if(contentDB == null)
			return false;
		Content content = null;
		try {
			contentLock.lock();
			content = getContent(hash_content);
			if(content == null)
				content = new Content(hash_url);
			else {
				if(content.getHashUrls().contains(hash_url))
					return false;
				content.addHashUrls(hash_url);
			}
			
			ContentTupleBinding ctb = new ContentTupleBinding();
		    DatabaseEntry key_entry = new DatabaseEntry();
		    DatabaseEntry data_entry = new DatabaseEntry();
		    
		    // bind the string to entry
		    StringBinding.stringToEntry(hash_content, key_entry);
		    ctb.objectToEntry(content, data_entry);
		    
		    OperationStatus status = contentDB.put(null, key_entry, data_entry); 
		    if(status == OperationStatus.SUCCESS)
		    	return true;
		    else
		    	return false;
		} catch(Exception e) {
			return false;
		} finally {
			contentLock.unlock();
		}
	}
	
	public Content getContent(String key) {
		if(contentDB == null)
			return null;
		try {
			contentLock.lock();
		    DatabaseEntry key_entry = new DatabaseEntry();
		    DatabaseEntry data_entry = new DatabaseEntry();
		    ContentTupleBinding ctb = new ContentTupleBinding();
		    StringBinding.stringToEntry(key, key_entry);
		    OperationStatus status = contentDB.get(null, key_entry, data_entry, LockMode.DEFAULT);
		    if(status == OperationStatus.SUCCESS) {
		    	Content content = ctb.entryToObject(data_entry);
		    	return content;
		    }
		    return null;
		} catch(Exception e) {
			return null;
		} finally {
			contentLock.unlock();
		}
	}
	
	public boolean deleteContent(String key) {
		if(contentDB == null)
			return false;
	    DatabaseEntry key_entry = new DatabaseEntry();
	    StringBinding.stringToEntry(key, key_entry);
	    OperationStatus status = contentDB.delete(null, key_entry);
	    if(status == OperationStatus.SUCCESS) {
	    	return true;
	    }
	    return false;
	}
	
	public boolean storeTitle(String url, String title) {
		if(titleDB == null)
			return false;
		
	    DatabaseEntry key_entry = new DatabaseEntry();
	    DatabaseEntry data_entry = new DatabaseEntry();
	    
	    // bind the string to entry
	    StringBinding.stringToEntry(url, key_entry);
	    StringBinding.stringToEntry(title, data_entry);
	    
	    OperationStatus status = titleDB.put(null, key_entry, data_entry); 
	    if(status == OperationStatus.SUCCESS)
	    	return true;
	    else
	    	return false;
	}
	
	public String getTitle(String url) {
		if(titleDB == null)
			return null;
		
	    DatabaseEntry key_entry = new DatabaseEntry();
	    DatabaseEntry data_entry = new DatabaseEntry();
	    
	    // bind the string to entry
	    StringBinding.stringToEntry(url, key_entry);
	    
	    OperationStatus status = titleDB.get(null, key_entry, data_entry, LockMode.DEFAULT); 
	    if(status == OperationStatus.SUCCESS)
	    	return StringBinding.entryToString(data_entry);
	    else
	    	return null;
	}
	
	public Cursor initTitleCursor() {
		if(dbEnv == null || titleDB == null)
			return null;
		Cursor myCursor = titleDB.openCursor(null, null);
		return myCursor;
	}
	
	public String getNextTitle(Cursor cursor) {
		if(cursor == null)
			return null;
		DatabaseEntry key_entry = new DatabaseEntry();
	    DatabaseEntry data_entry = new DatabaseEntry();
	 
	    if(cursor.getNext(key_entry, data_entry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
	    	String url = StringBinding.entryToString(key_entry);
	    	String title = StringBinding.entryToString(data_entry);
	    	return url + "\t" + title + "\r\n";
	    }
	    return null;
	}
	
	public void closeTitleCursor(Cursor cursor) {
		if(cursor != null)
			cursor.close();
	}
	
	public Cursor initHtmlDocumentCursor() {
		if(dbEnv == null || htmlDB == null)
			return null;
		Cursor myCursor = htmlDB.openCursor(null, null);
		return myCursor;
	}
	
	public HtmlDocument getNext(Cursor cursor) {
		if(cursor == null)
			return null;
		DatabaseEntry key_entry = new DatabaseEntry();
	    DatabaseEntry data_entry = new DatabaseEntry();
	    HtmlTupleBinding htb = new HtmlTupleBinding();
	    if(cursor.getNext(key_entry, data_entry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
	    	HtmlDocument hdoc = htb.entryToObject(data_entry);
	    	return hdoc;
	    }
	    return null;
	}
	
	public void closeCursor(Cursor cursor) {
		if(cursor != null)
			cursor.close();
	}
	
	public static void emptyUrlAndContent(String envpath) {
		EnvironmentConfig envConf = new EnvironmentConfig();
        // environment will be created if not exists
        envConf.setAllowCreate(true);
        envConf.setReadOnly(false);  
        envConf.setTransactional(true); 
  
        // open/create the DB environment using config
        Environment db_env = new Environment(
              new File(envpath), envConf);
        long num_url = db_env.truncateDatabase(null, URLDB, true);
		long num_content = db_env.truncateDatabase(null, CONTENTDB, true);
		long num_title = db_env.truncateDatabase(null, TITLEDB, true);
		System.out.println(num_url);
		System.out.println(num_content);
		System.out.println(num_title);
		db_env.close();
	}
	
	public static void main(String[] args) {
		emptyUrlAndContent("/home/cloudera/Documents/berkerleydb");
	}
	
}
