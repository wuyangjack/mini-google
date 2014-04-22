package general;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class StopWords {
	
	HashSet<String> stopWordsList = null;

	public StopWords(){
		stopWordsList = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("conf/StopWords.txt"));
			String line = null;
			while((line = br.readLine()) != null){
				stopWordsList.add(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isStopWord(String word){
		return this.stopWordsList.contains(word);
	}
}
