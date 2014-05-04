package cis455.project.storage;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StorageDumper {
	
	public static void main(String[] args) {
		System.out.println("dumper arguments: ");
		for (String arg : args) {
			System.out.print(" " + arg);
		}
		System.out.println();
		sourceFile = args[0];
		String databaseDirectory = args[1];
		nodeCount = Integer.parseInt(args[2]);
		nodeIndex = Integer.parseInt(args[3]);
		databaseName = args[4];
		threadCount = Integer.parseInt(args[5]);
		keyIndex = Integer.parseInt(args[6]);
		Storage.setEnvPath(databaseDirectory, false);
		dump();
	}
	
	public static int nodeIndex = -1;
	public static int nodeCount = -1;
	public static int keyIndex = -1;
	public static String databaseName = null;
	public static String  sourceFile = null;
	public static int threadCount = -1;
	public static File poison = new File("poison");
	private static int linesWritten = 0;
	public static final int linesUpdateIncrement = 100;
	
	public static synchronized void updateStatus() {
		linesWritten += linesUpdateIncrement;
		System.out.println("line written: " + linesWritten);
	}
	
	public static void dump(){
		File dir = new File(sourceFile);
		File[] fileList = dir.listFiles();
		
		BlockingQueue<File> entryQ = new LinkedBlockingQueue<File>();
		
		try {
			for(int i = 0; i < fileList.length; i++){
				entryQ.put(fileList[i]);
			}
			for(int i = 0; i < StorageDumper.threadCount; i++){
				entryQ.put(StorageDumper.poison);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Thread[] threads = new Thread[StorageDumper.threadCount];
		
		Log.info("Start dumping into DB - " + databaseName + " , thread number is " + threads.length);
		
		for(int i = 0; i < threads.length; i++){
			threads[i] = new FileReaderThread(entryQ);
			threads[i].start();
		}
	}
}
