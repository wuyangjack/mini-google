package cis455.project.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;


public class SearchWorker {

	private static final String CRLF = "\r\n";
	
	public SearchWorker() {
	}
	
	static class QueryInfo {
		Map<String, Double> words_weights;
		double module;
		
		QueryInfo(Map<String, Double> words_weights, double module) {
			this.words_weights = words_weights;
			this.module = module;
		}
		
		Map<String, Double> getWordsWeights() {
			return words_weights;
		}
		
		double getModule() {
			return module;
		}
	}
	
	private static QueryInfo getQueryInfo(List<String> words) {
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
		}
		// 1.2 get the module
		module = Math.sqrt(module);
		QueryInfo queryInfo = new QueryInfo(words_weights, module);
		return queryInfo;
	}
	
	private static Map<String, SearchInfo> getSearchInfo(List<String> words) {
		Map<String, SearchInfo> urlMap =  new HashMap<String, SearchInfo>();
		List<TFIDFEntry> tf_entries = QueryWorker.getWordweight("title", words);
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
		return urlMap;
	}
	
	private static Map<String, Double> getWeightMap(QueryInfo queryInfo, Map<String, SearchInfo> urlMap) {
		Map<String, Double> weightMap = new HashMap<String, Double>();
		Map<String, Double> moduleMap = QueryWorker.getDocmodule("title", urlMap.keySet());
		Map<String, Double> pageRankMap = QueryWorker.getPagerank(urlMap.keySet());
		for(Entry<String, SearchInfo> entry: urlMap.entrySet()) {
			// 4.1 for each word in the query
			Map<String, Double> wordsWeight = entry.getValue().getWordweights();
			double final_score = 0, vector_mul = 0;
			System.out.print("Doc: " + entry.getKey() + "; ");
			for(Entry<String, Double> weight_entry : wordsWeight.entrySet()) {
				vector_mul += weight_entry.getValue() * queryInfo.getWordsWeights().get(weight_entry.getKey());
				System.out.println("Word: " + weight_entry.getKey() + "; two weight: " + weight_entry.getValue() + "; " + queryInfo.getWordsWeights().get(weight_entry.getKey()));
			}
			final_score = (vector_mul / (queryInfo.getModule() * moduleMap.get(entry.getKey()))) * pageRankMap.get(entry.getKey());
			weightMap.put(entry.getKey(), final_score);
		}
		return weightMap;
	}
	public static String search(List<String> words) {
		// 1. calcute tf of the query
		QueryInfo queryInfo = getQueryInfo(words);
		// 2. get all the words and their corresponds doc
		Map<String, SearchInfo> urlMap = getSearchInfo(words);
		// 3. calculate the weight
		Map<String, Double> weightMap = getWeightMap(queryInfo, urlMap);
		// 4. Sort by values
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
		return sb.toString();
	}
	
	public static void main(String[] args) {
		// test1
		System.out.println("Test1");
		List<String> words = new ArrayList<String>();
		words.add("java");
		words.add("list");
		QueryInfo queryInfo = getQueryInfo(words);
		for(String s : queryInfo.getWordsWeights().keySet()) {
			System.out.println("Word: " + s + "; Weight: " + queryInfo.getWordsWeights().get(s));
		}
		// test2
		System.out.println("Test2");
		Map<String, SearchInfo> urlMap = getSearchInfo(words);
		for(String s : urlMap.keySet()) {
			System.out.print("Url: " + s + "; ");
			for(String ss : urlMap.get(s).getWordweights().keySet()) {
				System.out.print("Word: " + ss + " Weight: " + urlMap.get(s).getWordweights().get(ss) + "; ");
			}
			System.out.println();
		}
		// test3
		System.out.println("Test3");
		Map<String, Double> weightMap = getWeightMap(queryInfo, urlMap);
		for(Entry<String, Double> entry: weightMap.entrySet()) {
			System.out.println("Document: " + entry.getKey() + "; Score: " + entry.getValue());
		}
	}
}
