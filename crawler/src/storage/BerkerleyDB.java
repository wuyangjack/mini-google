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
import general.SHA1;
import java.io.File;
import java.util.Date;
import java.util.Set;


public class BerkerleyDB {
	
	public static final String HTMLDB = "html_db";
	public static final String ROBOTDB = "robot_db";
	public static final String URLDB = "url_db";
	
	private String env_path;
	private Environment dbEnv;
	// hash url: url, content, date
	private Database htmlDB;
	// robot : time, disallow path
	private Database robotDB;
	// hash url : url (some may have not been crawled)
	private Database urlDB;
	
	public BerkerleyDB(String env_path) {
		this.env_path = env_path;
		initEnv();
		openDB();
	}
	
	public void setEnvPath(String env_path) {
		this.env_path = env_path;
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
	
	
	public void close() {
		closeHtmlDB();
		closeRobotDB();
		closeUrlDB();
		closeEnv();
	}
	
	/**
	 * Store the HtmlDocument
	 * @param key
	 * @param data
	 * @param date
	 * @param isHtml
	 * @return
	 */
	public synchronized boolean storeHtmlDocument(String url, String data, Date date) {
		if(htmlDB == null)
			return false;
		String sdate = DateTransfer.transferDate(date);
		String hash_url = SHA1.encodeString(url);
		HtmlDocument hdoc = new HtmlDocument(url, data, sdate);
		HtmlTupleBinding xtb = new HtmlTupleBinding();
		
	    DatabaseEntry key_entry = new DatabaseEntry();
	    DatabaseEntry data_entry = new DatabaseEntry();
	    
	    // bind the string to entry
	    StringBinding.stringToEntry(hash_url, key_entry);
	    xtb.objectToEntry(hdoc,  data_entry);

	    
	    OperationStatus status = htmlDB.put(null, key_entry, data_entry); 
	    if(status == OperationStatus.SUCCESS)
	    	return true;
	    else
	    	return false;
	}
	/**
	 * Get HtmlDocument by key(url)
	 * @param key
	 * @return
	 */
	public HtmlDocument getHtmlDocument(String url) {
		if(htmlDB == null)
			return null;
		
		String hash_url = SHA1.encodeString(url);
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
	}
	
	/**
	 * Delete Url Document by key(url)
	 * @param key
	 * @return
	 */
	public synchronized boolean deleteHtmlDocument(String url) {
		if(htmlDB == null)
			return false;
		
		String hash_url = SHA1.encodeString(url);
	    DatabaseEntry key_entry = new DatabaseEntry();
	    StringBinding.stringToEntry(hash_url.toString(), key_entry);
	    OperationStatus status = htmlDB.delete(null, key_entry);
	    if(status == OperationStatus.SUCCESS) {
	    	return true;
	    }
	    return false;
	}
	
	public synchronized boolean storeRobot(String host, int delay, String time, Set<String> disallow) {
		if(robotDB == null)
			return false;
		RobotText rt = new RobotText(delay, time, disallow);
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
	
	public synchronized boolean storeUrl(String hash_key, String url) {
		if(urlDB == null)
			return false;

	    DatabaseEntry key_entry = new DatabaseEntry();
	    DatabaseEntry data_entry = new DatabaseEntry();
	    
	    // bind the string to entry
	    StringBinding.stringToEntry(hash_key, key_entry);
	    StringBinding.stringToEntry(url, data_entry);
	    
	    OperationStatus status = urlDB.put(null, key_entry, data_entry); 
	    if(status == OperationStatus.SUCCESS)
	    	return true;
	    else
	    	return false;
	}
	
	public String getUrl(String hash_key) {
		if(urlDB == null)
			return null;
	    DatabaseEntry key_entry = new DatabaseEntry();
	    DatabaseEntry data_entry = new DatabaseEntry();
	  
	    StringBinding.stringToEntry(hash_key, key_entry);
	    OperationStatus status = urlDB.get(null, key_entry, data_entry, LockMode.DEFAULT);
	    if(status == OperationStatus.SUCCESS) {
	    	String response = StringBinding.entryToString(data_entry);
	    	return response;
	    }
	    return null;
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
	
	public Cursor initChannelCursor() {
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

}
