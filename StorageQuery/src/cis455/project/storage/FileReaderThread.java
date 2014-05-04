package cis455.project.storage;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import cis455.project.hash.SHA1Partition;

public class FileReaderThread extends Thread{
	
	private BlockingQueue<File> queue = null;
	Storage storage = Storage.getInstance(StorageDumper.databaseName);
	
	public FileReaderThread(BlockingQueue<File> queue){
		this.queue = queue;
	}
	
	@Override
	public void run(){
		BufferedReader br = null;
		SHA1Partition.setRange(StorageDumper.nodeCount);
		try {
			while(true){
				File file = this.queue.take();
				if(file.compareTo(StorageDumper.poison) == 0){
					break;
				}
				br = new BufferedReader(new FileReader(file));
				String line = null;
				String key = null;
				String value = null;
				String SHAkey = null;
				int count = 1;
				
				while((line = br.readLine()) != null){
					try {
						count ++;
						if (count % StorageDumper.linesUpdateIncrement == 0) {
							StorageDumper.updateStatus();
							count = 1;
						}
						String[] data = line.trim().split("\t", 2);
						key = data[0];
						value = data[1];
						SHAkey = line.trim().split("\t", StorageDumper.keyIndex + 2)[StorageDumper.keyIndex];
						System.out.println(file.getPath() + ":");
						System.out.print("SHA | Key | Outcome: " + SHAkey + " | " + key + " | ");
						if(!SHAkey.equals("") && (SHA1Partition.getWorkerIndex(SHAkey) == StorageDumper.nodeIndex)){
							System.out.println("Save to " + StorageDumper.databaseName);
							if(!storage.put(StorageDumper.databaseName, key, value)){
								System.err.println("bug when putting into DB | key | value: " + StorageDumper.databaseName + " | " + SHAkey + " | " + line);
								throw new Exception("error writing index");
							}
						} else {
							System.out.println("Skip");
						}
					} catch (Exception e) {
						System.err.println("error dumping line: " + line);
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			System.err.println("error dumping index");
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException e) {
				System.err.println("error closing reader");
				e.printStackTrace();
			}
		}
	}
	
}
