package cis455.project.storage;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cis455.project.hash.SHA1Partition;

public class FileReaderThread extends Thread{
	
	private File file;
	Storage storage = Storage.getInstance();
	
	public FileReaderThread(File file){
		this.file = file;
	}
	
	@Override
	public void run(){
		BufferedReader br = null;
		try {
			SHA1Partition.setRange(StorageDumper.NODE_NUMBER);
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null){
				String[] data = line.split("\t", 2);
				if (data.length != 2) {
					System.out.println("Illegal");
				}
				System.out.println(file.getPath() + ":");
				System.out.print("\"" + data[0] + "\": ");
				if(!data[0].equals("") && (SHA1Partition.getWorkerIndex(data[0]) == StorageDumper.NODE_INDEX)){
					System.out.println("Save");
					if(!storage.put(StorageDumper.dbName, data[0], data[1])){
						Log.error("bug whern putting into DB + key + value: " + StorageDumper.dbName
								+ " + " + data[0]+ " + " + line);
						throw new Exception("DB Exception occured while putting index data!");
					}
				} else {
					System.out.println("Skip");
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
