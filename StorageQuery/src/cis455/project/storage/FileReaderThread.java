package cis455.project.storage;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import cis455.project.hash.SHA1Partition;

public class FileReaderThread extends Thread{
	
	private BlockingQueue<File> queue = null;
	Storage storage = Storage.getInstance();
	
	public FileReaderThread(BlockingQueue<File> queue){
		this.queue = queue;
	}
	
	@Override
	public void run(){
		BufferedReader br = null;
		SHA1Partition.setRange(StorageDumper.NODE_NUMBER);
		try {
			while(true){
				File file = this.queue.take();
				if(file.compareTo(StorageDumper.poison) == 0){
					break;
				}
				br = new BufferedReader(new FileReader(file));
				String line = null;
				String key = "";
				String value = "";
				String SHAkey = "";

				while((line = br.readLine()) != null){

					String[] data = line.trim().split("\t", 2);
					key = data[0];
					value = data[1];
					if (data.length != 2) {
						System.out.println("Illegal");
					}
					if(StorageDumper.dbName.equals("tablePageRank")){
						SHAkey = key;
					}
					else{
						//SHA key = docId
						SHAkey = value.split("\t")[0];
					}


					System.out.println(file.getPath() + ":");
					System.out.print("\"" + data[0] + "\": ");
					if(!SHAkey.equals("") && (SHA1Partition.getWorkerIndex(SHAkey) == StorageDumper.NODE_INDEX)){

						System.out.println("Save to " + StorageDumper.dbName);
						if(!storage.put(StorageDumper.dbName, key, value)){
							Log.error("bug whern putting into DB + key + value: " + StorageDumper.dbName
									+ " + " + data[0]+ " + " + line);
							throw new Exception("DB Exception occured while putting index data!");
						}
					} else {
						System.out.println("Skip");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getStackTrace().toString());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				Log.error(e.getStackTrace().toString());
			}
		}
	}
	
}
