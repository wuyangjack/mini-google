package cis455.project.storage;

import java.io.File;

public class StorageDumper {
	
	public static void main(String[] args) {
		String dbDir = args[1];
		NODE_NUMBER = Integer.parseInt(args[2]);
		NODE_INDEX = Integer.parseInt(args[3]);
		String dataPath = args[0];
		
		Storage.setEnvPath(dbDir);
		dump(dataPath);
		
	}
	
	public static int NODE_INDEX = 1;
	public static int NODE_NUMBER = 5;
	public static String dbName = "";
	
	public static void dump(String path){
		if(path.contains("title")) dbName = "title";
		else if (path.contains("meta")) dbName = "meta";
		else if (path.contains("body")) dbName = "body";
		
		System.out.println(dbName);

		File dir = new File(path);
		File[] fileList = dir.listFiles();
		Thread[] threads = new Thread[fileList.length];
		
		System.out.println(fileList.length);
		
		for(int i = 0; i < fileList.length; i++){
			threads[i] = new FileReaderThread(fileList[i]);
			threads[i].start();
		}
	}

}
