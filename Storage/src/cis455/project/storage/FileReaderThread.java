package cis455.project.storage;

import hash.SHA1Partition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileReaderThread extends Thread{
	
	private File file;
	Storage storage = Storage.getInstance();
	
	public FileReaderThread(File file){
		this.file = file;
	}
	
	@Override
	public void run(){
		BufferedReader br;
		try {
			SHA1Partition.setRange(StorageDumper.NODE_NUMBER);
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null){
				String[] data = line.split("\t");
				if(!data[0].equals("") && (SHA1Partition.getWorkerIndex(data[0]) == StorageDumper.NODE_INDEX)){
					System.out.println("\"" + data[0] + "\"" );
					if(!storage.put(StorageDumper.dbName, data[0], line)){
						Log.error("bug whern putting into DB + key + value: " + StorageDumper.dbName
								+ " + " + data[0]+ " + " + line);
						throw new Exception("DB Exception occured while putting index data!");
					}
				}
			}	
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getStackTrace().toString());
		}
	}
	
}
