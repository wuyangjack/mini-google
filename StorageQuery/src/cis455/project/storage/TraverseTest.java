package cis455.project.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TraverseTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TraverseTest().getTest("Result-From-Get");

	}
	
	public void getTest(String fileName){
		String dir = "/home/cloudera/Database";
		Storage.setEnvPath(dir, false);
		Storage storage = Storage.getInstance();
		try {
			BufferedReader br = new BufferedReader(new FileReader("/home/cloudera/out/1"));
			String line;
			while((line = br.readLine()) != null){
				String key = line.trim().split(" ")[0];
				key = key.substring(4);
				System.out.println(key);
				String[] re = storage.get(StorageGlobal.tablePageRank, key);
				for(int i = 0; i < re.length; i++){
					fileWriter(fileName, key + "\t" +re[i]);
					System.out.println(re[i]);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void fileWriter(String fileName, String data){
    	try{ 
    		File file =new File("/home/cloudera/out/" + fileName);
 
    		//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}
 
    		//true = append file
    		FileWriter fileWritter = new FileWriter("/home/cloudera/out/" + file.getName(),true);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(data);
    	        bufferWritter.write("\n");
    	        bufferWritter.close();
 
    	}catch(IOException e){
    		e.printStackTrace();
    	}
	}
}
