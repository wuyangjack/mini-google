package cis455.project.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class Storage {
	// Singleton class
	private static Storage database = null;
	
	private static String envPath;
	private static boolean readOnly = false;
	private Environment dbEnv;
	private Hashtable<String, Database> tables = new Hashtable<String, Database>();
	private Hashtable<String, String> locks = new Hashtable<String, String>();

	
	private Storage() {
		File dir = new File(envPath);
		if (!dir.exists()) {
			dir.mkdir(); 
		}
		initEnv();
	}
	
	public static Storage getInstance() {
		if(database == null)
			database = new Storage();
		return database;
	}
	
	public static void setEnvPath(String env_path, boolean readOnly) {
		Storage.envPath = env_path;
		Storage.readOnly = readOnly;
	}
	
	public void initEnv() {
		// create a configuration for DB environment
		EnvironmentConfig envConf = new EnvironmentConfig();
		// environment will be created if not exists
		envConf.setAllowCreate(true);
		envConf.setReadOnly(readOnly);  
		envConf.setTransactional(true); 

		// open/create the DB environment using config
		dbEnv = new Environment(
				new File(envPath), envConf);
		
		//open TF-IDF DB
		this.initDatabase(StorageGlobal.tableFreqBody, readOnly);
		this.initDatabase(StorageGlobal.tableFreqMeta, readOnly);
		this.initDatabase(StorageGlobal.tableFreqTitle, readOnly);
		this.initDatabase(StorageGlobal.tablePageRank, readOnly);
	}
	
	private void initDatabase(String dbname, boolean readOnly) {
		// Config
		DatabaseConfig config = new DatabaseConfig(); 
		config.setTransactional(true);
		config.setReadOnly(readOnly);
		config.setSortedDuplicates(true);
		config.setAllowCreate(true);
		// Create
		Database dat = this.dbEnv.openDatabase(null, dbname, config);
		// Add
		this.tables.put(dbname, dat);
		this.locks.put(dbname, dbname);
	}
	
	public boolean put(String dbName, String key, String value){
		try{
			if (false == this.tables.containsKey(dbName)) {
				throw new Exception("table not existed");
			}
			if (false == this.locks.containsKey(dbName)) {
				throw new Exception("lock not existed");
			}
			Database database = this.tables.get(dbName);
			String lock =  this.locks.get(dbName);
			synchronized(lock){
				OperationStatus status = database.put(null, stringToEntry(key), stringToEntry(value));
				if (status.equals(OperationStatus.SUCCESS)){
					dbEnv.sync();
					return true;
				}
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public String[] get(String dbName, String key){
		try{
			if (false == this.tables.containsKey(dbName)) {
				throw new Exception("table not existed");
			}
			ArrayList<String> list = new ArrayList<String>();
			Database database = this.tables.get(dbName);
			Cursor cursor = null;
			DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));			
			DatabaseEntry theData = new DatabaseEntry();
			cursor = database.openCursor(null, null);
			OperationStatus retVal = cursor.getSearchKey(theKey, theData, LockMode.DEFAULT);			
			while (retVal == OperationStatus.SUCCESS) {
				list.add(new String(theData.getData(), "UTF-8"));
				retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
			}
			String[] ret = new String[list.size()];
			ret = list.toArray(ret);
			return ret;
		} catch(Exception e){
			e.printStackTrace();
			Log.error(e.getStackTrace().toString());
			// Return empty list if not found
			return new String[0];
		}
	}
	
	public void close() {
		Enumeration<Database> elements = tables.elements();
		while (elements.hasMoreElements()) {
			Database table = elements.nextElement();
			table.close();
		}
		dbEnv.close();
	}

	/**
	 * Convert a string to a database entry
	 * @param str
	 * @return
	 */
	private DatabaseEntry stringToEntry(String str){
		byte[] b = null;
		try
		{
			b = str.getBytes("UTF-8");
			return new DatabaseEntry(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new DatabaseEntry();
	}	
}
