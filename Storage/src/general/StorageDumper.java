package general;

import java.io.File;

public class StorageDumper {
	
	public static void main(String[] args) {
		
		NODE_NUMBER = Integer.parseInt(args[1]);
		NODE_INDEX = Integer.parseInt(args[2]);
		String path = args[0];
		dump(path);
	}
	
	public static int NODE_INDEX = 1;
	public static int NODE_NUMBER = 5;
	public static String dbName = "";
	
	public static void dump(String path){
		if(path.contains("title")) dbName = "title";
		else if (path.contains("meta")) dbName = "meta";
		else if (path.contains("body")) dbName = "body";

		File dir = new File(path);
		File[] fileList = dir.listFiles();
		Thread[] threads = new FileReaderThread[fileList.length];
		for(int i = 0; i < fileList.length; i++){
//			threads[i] 
		}
		
	}

}
