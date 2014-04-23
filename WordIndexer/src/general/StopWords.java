package general;

import index.IndexDriver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class StopWords {

	HashSet<String> stopWordsList = null;

	public StopWords(){
		stopWordsList = new HashSet<String>();
//		try {
//			BufferedReader br = new BufferedReader(new FileReader("conf/StopWords.txt"));
//			String line = null;
//			while((line = br.readLine()) != null){
//				stopWordsList.add(line);
//			}
//			br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		

		try{
//			Path pt = new Path(IndexDriver.stopWords + "title.txt");
			Path pt = new Path(IndexDriver.stopWords + "StopWords.txt");
			FileSystem fs = pt.getFileSystem(new Configuration());
			//FileSystem fs = FileSystem.get(new Configuration());
			if (fs.exists(pt) && fs.isFile(pt)) {
				// Read total loss from the first line
				BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
				String line = null;
				while((line = br.readLine()) != null){
					stopWordsList.add(line);
				}
				br.close();
			}
		}catch(Exception e){
			System.err.println("Read test failure." );
			e.printStackTrace();
			System.exit(1);
		}


		
		
		
		
	}
	
	public boolean isStopWord(String word){
		return this.stopWordsList.contains(word);
	}
}
