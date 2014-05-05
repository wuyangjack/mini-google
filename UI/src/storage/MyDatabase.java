package storage;

import java.io.File;


import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;


public class MyDatabase {

	public EnvironmentConfig env_config;
	public DatabaseConfig db_config;
	public Environment environment;
	public Database DB;
	
	public MyDatabase(String directory){
		env_config = new EnvironmentConfig();
		db_config = new DatabaseConfig();
		env_config.setAllowCreate(true);
		env_config.setTransactional(true);
		db_config.setTransactional(true);
        db_config.setAllowCreate(true);
//        db_config.setSortedDuplicates(true);
        
        File dir_file = new File(directory);
        if(!dir_file.exists()){
        	dir_file.mkdir();
        }
        
		environment = new Environment(new File(directory), env_config);
		DB = environment.openDatabase(null, "DB", db_config);
		
		
		
	}
	
	public void put(String key, DatabaseEntry valueEntry){
		DatabaseEntry keyEntry = new DatabaseEntry();
		
		StringBinding.stringToEntry(key, keyEntry);
		DB.put(null, keyEntry, valueEntry);
	}
	
	
	public void put(String key, String value){
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry valueEntry = new DatabaseEntry();
		
		StringBinding.stringToEntry(key, keyEntry);
		StringBinding.stringToEntry(value, valueEntry);
		DB.put(null, keyEntry, valueEntry);
	}
	
	public DatabaseEntry get(String key){
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry valueEntry = new DatabaseEntry();
		
		StringBinding.stringToEntry(key, keyEntry);
		if(DB.get(null, keyEntry, valueEntry, null) == OperationStatus.SUCCESS){
			return valueEntry;
		}
		return null;
	}
	
	public boolean containsKey(String key){
		return (get(key) != null);
	}
	
	public void delete(String key){
		DatabaseEntry keyEntry = new DatabaseEntry();
		StringBinding.stringToEntry(key, keyEntry);
		DB.delete(null, keyEntry);
	}
	
	public void close(){
		DB.close();
		environment.close();
	}
	
	
//	public static void main(String args[]){
//		MyDataBase db = new MyDataBase("/home/cis455/workspace/HW2/berkleydb");
//		db.put("iskey", "isvalue");
//	}

	
}
