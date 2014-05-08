package cis455.project.ui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SpellChecker {

	public HashMap<String, Integer> word_freq;
	
	public SpellChecker(String train_filename){
		word_freq = new HashMap<String, Integer>();
		init_Dictionary(train_filename);
	}
	
	public void init_Dictionary(String filename){
		try {
			FileReader reader = new FileReader(filename);
			StringBuilder sb = new StringBuilder();
			int data = reader.read();
			while(data != -1){
				sb.append((char)data);
				data = reader.read();
			}
			
			// Process the text to be separate words and lowercased, symbol removed.
			String content = sb.toString();
			content = content.toLowerCase();
			content = content.replaceAll("\\W", " ");
			content = content.replaceAll("\\s+", " ");
//			System.out.println(content);
			String[] all_words = content.split(" ");
			
			// count the frequency
			for(int i = 0; i < all_words.length; i++){
				if(word_freq.containsKey(all_words[i])){
					int temp = word_freq.get(all_words[i]).intValue();
					word_freq.put(all_words[i], temp + 1);
					
				}
				else{
					word_freq.put(all_words[i], 1);
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return;
	}
	
	public String check(String word){
		if(word == null)
			return "";
		if(word.trim().length() == 0)
			return "";
		if(word_freq.containsKey(word))
			return word;
		HashSet<String> mutate_words = generate_Mutates(word);
		HashMap<Integer, String> possible_results = new HashMap<Integer, String>();
		int best = 0;
		ArrayList<String> best_string = new ArrayList<String>();
		for(String each : mutate_words){
			if(word_freq.containsKey(each)){
				if(word_freq.get(each).intValue() == best){ // if even record, add to list
					best_string.add(each);
				}
				else if(word_freq.get(each).intValue() > best){ // if new best, clear the old candidates
					best = word_freq.get(each).intValue();
					best_string.clear();
					best_string.add(each);
				}
			}
		}
		if(best > 0){
			return best_string.get((((int)(Math.random() * best_string.size())) % best_string.size()));
		}
		
		//if not succeeded, check for distance of at most 2!
		for(String first_each : mutate_words){
			for(String second_each : generate_Mutates(first_each)){
				if(word_freq.containsKey(second_each)){
					if(word_freq.get(second_each).intValue() == best){ // if even record, add to list
						best_string.add(second_each);
					}
					else if(word_freq.get(second_each).intValue() > best){ // if new best, clear the old candidates
						best = word_freq.get(second_each).intValue();
						best_string.clear();
						best_string.add(second_each);
					}
				}
			}
		}
		
		if(best > 0){
			return best_string.get((((int)(Math.random() * best_string.size())) % best_string.size()));
		}
		return "";
	}
	
	public HashSet<String> generate_Mutates(String word){ // generates possible strings that has distance of 1 from original word
		HashSet<String> mutate_words = new HashSet<String>();
		for(int i = 0; i < word.length(); i++){	// overhit a character
			mutate_words.add(word.substring(0, i) + word.substring(i + 1)); 
		}
//		System.out.println("*********************");
		for(int i = 0; i < word.length() - 1; i++){ // swap two consecutive characters
			mutate_words.add(word.substring(0, i) + word.substring(i + 1, i + 2) + word.substring(i, i + 1) + word.substring(i + 2));
		}                                     
//		System.out.println("*********************");
		for(int i = 0; i < word.length(); i++){
			for(int j = 97; j < 122; j++){
				mutate_words.add(word.substring(0, i) + String.valueOf((char) j) + word.substring(i + 1));
				mutate_words.add(word.substring(0, i) + String.valueOf((char) j) + word.substring(i));
			}
		}
//		System.out.println("*********************");
		for(int j = 97; j < 122; j++){
			mutate_words.add(word.substring(0, word.length()) + String.valueOf((char) j));
//			System.out.println(word.substring(0, word.length()) + String.valueOf((char) j));
		}
		return mutate_words;
		
	}
	
	public String correct(String query) {
		String ret = "";
		String[] query_split_list = query.split("\\s"); 
	    for(int i = 0; i < query_split_list.length; i++){
	    	ret += this.check(query_split_list[i]);
	    }
	    if (query.equals(ret)) return null;
	    else return ret;
	}
	
//	public static void main(String args[]){
//		MySpellChecker msc = new MySpellChecker("/Users/sitong/Downloads/big.txt");
//		System.out.println("dictionary size: " + msc.word_freq.size());
//		System.out.println(msc.check("aple"));
//		
//	}
	
	
}
