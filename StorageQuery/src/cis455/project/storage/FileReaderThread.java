package cis455.project.storage;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.sleepycat.je.Database;
import com.sleepycat.je.Environment;

import cis455.project.hash.SHA1Partition;

public class FileReaderThread extends Thread{
	
	private BlockingQueue<File> queue = null;
	Storage storage = Storage.getInstance(DumperDistributed.databaseName);
	
	public FileReaderThread(BlockingQueue<File> queue){
		this.queue = queue;
	}
	
	@Override
	public void run(){
		BufferedReader br = null;
		Environment env = storage.getEnvironment(DumperDistributed.databaseName);
		Database dat = storage.getDatabase(DumperDistributed.databaseName);
		if (env == null || dat == null) {
			System.err.println("null environment / database");
		}
		SHA1Partition.setRange(DumperDistributed.nodeCount);
		try {
			while(true){
				File file = this.queue.take();
				if(file.compareTo(DumperDistributed.poison) == 0){
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
						if (count % DumperDistributed.linesUpdateIncrement == 0) {
							DumperDistributed.updateStatus();
							count = 1;
						}
						String[] data = line.trim().split("\t", 2);
						key = data[0];
						value = data[1];
						//System.out.println(file.getPath() + ":");
						boolean write = false;
						if (DumperDistributed.nodeSingle) {
							write = true;
						} else {
							SHAkey = line.trim().split("\t", DumperDistributed.keyIndex + 2)[DumperDistributed.keyIndex];
							if(!SHAkey.equals("") && (SHA1Partition.getWorkerIndex(SHAkey) == DumperDistributed.nodeIndex)) {
								write = true;
							}
							System.out.print("SHA | Key | Outcome: " + SHAkey + " | " + key + " | ");
						}
						
						if(write){
							//System.out.println("Save to " + DumperDistributed.databaseName);
							if(!storage.bulk(env, dat, DumperDistributed.databaseName, key, value)){
								System.err.println("bug when putting into DB | key | value: " + DumperDistributed.databaseName + " | " + SHAkey + " | " + line);
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
			System.out.println("sync database: " + DumperDistributed.databaseName);
			storage.sync(DumperDistributed.databaseName);
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
