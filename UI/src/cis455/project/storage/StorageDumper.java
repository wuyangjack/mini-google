package cis455.project.storage;

import java.io.File;

public class StorageDumper {
	
	public static void main(String[] args) {
		dataPath = args[0];
		String dbDir = args[1];
		NODE_NUMBER = Integer.parseInt(args[2]);
		NODE_INDEX = Integer.parseInt(args[3]);
		dbName = args[4];
		Storage.setEnvPath(dbDir, false);
		dump();
	}
	
	public static int NODE_INDEX = 1;
	public static int NODE_NUMBER = 5;
	public static String dbName = null;
	public static String  dataPath = null;;

	public static void dump(){
		File dir = new File(dataPath);
		File[] fileList = dir.listFiles();
		Thread[] threads = new Thread[fileList.length];
		
		Log.info("Start dumping into DB - " + dbName + " , thread number is " + fileList.length);
		
		for(int i = 0; i < fileList.length; i++){
			threads[i] = new FileReaderThread(fileList[i]);
			threads[i].start();
		}
	}
}
