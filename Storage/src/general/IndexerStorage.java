package general;

import java.io.File;
import java.util.ArrayList;
import org.json.JSONObject;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class IndexerStorage {

	
	public static final String TFIDFDB = "tf_idf";
	
	// singleton class
	private static IndexerStorage database = null;
	
	private static String env_path;
	private Environment dbEnv;
	
	private Database tf_idf_title;
	private Database tf_idf_meta;
	private Database tf_idf_body;
	
	private IndexerStorage() {
		initEnv();
	}
	
	public static IndexerStorage getInstance() {
		if(database == null)
			database = new IndexerStorage();
		return database;
	}
	
	public static void setEnvPath(String env_path) {
		IndexerStorage.env_path = env_path;
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

		//DB config for TF-IDF
		DatabaseConfig config = new DatabaseConfig(); 
		config.setTransactional(true);
		config.setSortedDuplicates(true);
		config.setAllowCreate(true);
		//open TF-IDF DB 
		this.tf_idf_title = this.dbEnv.openDatabase(null, "tf_idf_title", config);
		this.tf_idf_meta = this.dbEnv.openDatabase(null, "tf_idf_meta", config);
		this.tf_idf_body = this.dbEnv.openDatabase(null, "tf_idf_body", config);
	}
	
	
	/********************************************** TF-IDF DB Wrapper ************************/
	public boolean addTFIDF(String dbName, String word, String doc, double tf_idf, ArrayList<Integer> positions){
		try{
			Database database = null;
			if(dbName.equals("title")){
				database = this.tf_idf_title;
			}
			else if(dbName.equals("meta")){
				database = this.tf_idf_meta;
			}
			else if(dbName.equals("body")){
				database.equals(this.tf_idf_body);
			}
			else{
				return false;
			}
//						
			JSONObject value = new JSONObject();
			value.put("word", word);
			value.put("doc", doc);
			value.put("tfidf", tf_idf + "");
			value.put("positions", positions.toString());
			
			OperationStatus status = database.put(null, stringToEntry(word), stringToEntry(value.toString()));
			if (status.equals(OperationStatus.SUCCESS)){
				dbEnv.sync();
				return true;
			}
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public String getTFIDFString(String word){
		StringBuilder dataString = new StringBuilder();
		try{
			Cursor cursor = null;
			DatabaseEntry theKey = new DatabaseEntry(word.getBytes("UTF-8"));			
			DatabaseEntry theData = new DatabaseEntry();
			cursor = this.tf_idf_body.openCursor(null, null);
			OperationStatus retVal = cursor.getSearchKey(theKey, theData, LockMode.DEFAULT);
			while (retVal == OperationStatus.SUCCESS) {
				dataString.append(new String(theData.getData(), "UTF-8"));
				dataString.append(" >_<# ");	
				retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
			}
			return dataString.toString();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	

	public ArrayList<WordDocTFEntry> getTFIDF(String rawString){
		ArrayList<WordDocTFEntry> list = new ArrayList<WordDocTFEntry>();
		try{
			String[] array = rawString.split(" >_<# ");
			for(int i = 0; i < array.length; i++){
				if(!array[i].equals("")){
					JSONObject value = new JSONObject(array[i]);
					String word = value.getString("word").toString();
					String doc = value.get("doc").toString();
					String tfidf_String = value.get("tfidf").toString();
					double tfidf = Double.parseDouble(tfidf_String);
					String positions = value.get("positions").toString();
					list.add(new WordDocTFEntry(word, doc, tfidf, positions));
				}
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
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
	/********************************************** TF-IDF DB Wrapper ************************/
	
	
}
