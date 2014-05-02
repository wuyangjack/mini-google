package cis455.project.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Test {

	private static String CRLF = "\r\n";
	public static void test1() {
		List<String> words = new ArrayList<String>();
		words.add("java");
		words.add("list");
		
	}
	
	public static void test2() {
		List<String> words = new ArrayList<String>();
		words.add("java");
		words.add("list");
		// 1. calcute tf of the query
		Map<String, Integer> words_map = new HashMap<String, Integer>();
		Map<String, Double> words_weights = new HashMap<String, Double>();
		int max_freq = 0;
		for(String word : words) {
			int num = 0;
			if(! words_map.containsKey(word)) {
				num = 1;
			}
			else {
				num = words_map.get(word) + 1;
			}
			words_map.put(word, num);
			max_freq = Math.max(max_freq, num);
		}
		// 1.1 get the the tf of each word
		double module = 0;
		for(String word: words_map.keySet()) {
			double tf = 0.5 + 0.5 * (words_map.get(word) / max_freq);
			module += tf * tf;
			words_weights.put(word, tf);
			System.out.println(word + ": " + tf);
		}
		// 1.2 get the module
		module = Math.sqrt(module);
		System.out.println(module);
		
		Map<String, SearchInfo> urlMap = new HashMap<String, SearchInfo>();
		List<TFIDFEntry> tf_entries = new ArrayList<TFIDFEntry>();
		TFIDFEntry entry1 = new TFIDFEntry("java", "url1", 2.45, "[]");
		tf_entries.add(entry1);
		entry1 = new TFIDFEntry("java", "url2", 1.45, "[]");
		tf_entries.add(entry1);
		entry1 = new TFIDFEntry("list", "url1", 0.45, "[]");
		tf_entries.add(entry1);
		
		Iterator<TFIDFEntry> tf_iterator = tf_entries.iterator();
		while(tf_iterator.hasNext()) {
			TFIDFEntry entry = tf_iterator.next();
			SearchInfo info = null;
			// for each word, add the weight and position to url map
			if(! urlMap.containsKey(entry.getUrl())) {
				info = new SearchInfo();
			}
			else {
				info = urlMap.get(entry.getUrl());
			}
			info.addWordweight(entry.getWord(), entry.getWeight());
			info.addWordPosition(entry.getWord(), entry.getPositions());
			// last step, update this url
			urlMap.put(entry.getUrl(), info);
		}
		for(String s : urlMap.keySet()) {
			System.out.print("Url: " + s + "; ");
			for(String ss : urlMap.get(s).getWordweights().keySet()) {
				System.out.print("Word: " + ss + " Wegith: " + urlMap.get(s).getWordweights().get(ss) + "; ");
			}
			System.out.println();
		}
		
		for(Entry<String, SearchInfo> entry: urlMap.entrySet()) {
			// 4.1 for each word in the query
			Map<String, Double> wordsWeight = entry.getValue().getWordweights();
			double final_score = 0, vector_mul = 0;
			for(Entry<String, Double> weight_entry : wordsWeight.entrySet()) {
				vector_mul += weight_entry.getValue() * words_weights.get(weight_entry.getKey());
			}
			final_score = vector_mul;
			System.out.println(entry.getKey() + ": " + final_score);
		}
	}
	
	public static void test4() {
		
	}
	
	
	public static void test5() {
		Map<String, Double> weightMap = new HashMap<String, Double>();
		weightMap.put("url1", 0.45);
		weightMap.put("url2", 0.55);
		weightMap.put("url3", 0.25);
		weightMap.put("url4", 0.75);
		List<Entry<String, Double> > results = new ArrayList<Entry<String, Double>>(weightMap.entrySet());
		Collections.sort(results, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				if(o1.getValue() < o2.getValue())
					return 1;
				else if(o1.getValue() > o2.getValue())
					return -1;
				else return 0;
			} 
		}); 
		StringBuffer sb = new StringBuffer();
		for(Entry<String, Double> entry : results) {
			sb.append(entry.getKey() + "\t" + entry.getValue() + CRLF);
		}
		System.out.println(sb.toString());
	}
	
	public static void main(String[] args) {
		test2();
		test5();
	}
}
