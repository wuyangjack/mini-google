package cis455.project.storage;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StorageDumper {
	
	public static void main(String[] args) {
		dataPath = args[0];
		String dbDir = args[1];
		NODE_NUMBER = Integer.parseInt(args[2]);
		NODE_INDEX = Integer.parseInt(args[3]);
		dbName = args[4];
		dumpThreadNum = Integer.parseInt(args[5]);
		Storage.setEnvPath(dbDir, false);
		dump();
	}
	
	public static int NODE_INDEX = 1;
	public static int NODE_NUMBER = 5;
	public static String dbName = null;
	public static String  dataPath = null;
	public static int dumpThreadNum = 1;
	public static File poison = new File("poison");

	public static void dump(){
		File dir = new File(dataPath);
		File[] fileList = dir.listFiles();
		
		BlockingQueue<File> entryQ = new LinkedBlockingQueue<File>();
		
		try {
			for(int i = 0; i < fileList.length; i++){
				entryQ.put(fileList[i]);
			}
			for(int i = 0; i < StorageDumper.dumpThreadNum; i++){
				entryQ.put(StorageDumper.poison);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread[] threads = new Thread[StorageDumper.dumpThreadNum];
		
		Log.info("Start dumping into DB - " + dbName + " , thread number is " + fileList.length);
		
		for(int i = 0; i < threads.length; i++){
			threads[i] = new FileReaderThread(entryQ);
			threads[i].start();
		}
	}
}
