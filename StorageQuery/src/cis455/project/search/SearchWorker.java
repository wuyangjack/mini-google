package cis455.project.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

import cis455.project.query.QueryWorker;
import cis455.project.rank.DocumentInfo;
import cis455.project.storage.StorageGlobal;
import cis455.project.storage.TFIDFEntry;


public class SearchWorker {

	public static final String CRLF = "\r\n";
	
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
		// word : occur number
		Map<String, Integer> words_map = new HashMap<String, Integer>();
		// word : tfidf
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
	
	private static Map<String, SearchInfo> getSearchInfo(String tableName, List<String> words) {
		//QueryWorker.logger.info("Begin Table: " + tableName);
		// url : words map + words position
		Map<String, SearchInfo> urlMap =  new HashMap<String, SearchInfo>();
		List<TFIDFEntry> tf_entries = QueryWorker.getWordweight(tableName, words);
		Iterator<TFIDFEntry> tf_iterator = tf_entries.iterator();
		while(tf_iterator.hasNext()) {
			TFIDFEntry entry = tf_iterator.next();
			SearchInfo info = null;
			// 1.1 for each word, add the weight and position to url map
			if(! urlMap.containsKey(entry.getUrl())) {
				info = new SearchInfo();
			}
			else {
				info = urlMap.get(entry.getUrl());
			}
			//QueryWorker.logger.info("Word: " + entry.getWord() + "; " + entry.getWeight());
			// 1.2 map add word : weight
			info.addWordweight(entry.getWord(), entry.getWeight());
			//QueryWorker.logger.info("Word: " + entry.getWord() + "; " + entry.getPositions().size());
			info.addWordPosition(entry.getWord(), entry.getPositions());
			// 1.3 last step, update this url
			urlMap.put(entry.getUrl(), info);
		}
		return urlMap;
	}
	
	private static Map<Integer, List<DocumentInfo>> getWeightMap(QueryInfo queryInfo, Map<String, SearchInfo> urlMap, Map<String, SearchInfo> metaMap) {
		// 1 init weight Map 
		// number match : url list
		Map<Integer, List<DocumentInfo>> weightMap = new HashMap<Integer, List<DocumentInfo>>();
		for(int i = 1; i <= queryInfo.getWordsWeights().size(); i++) {
			weightMap.put(i, new ArrayList<DocumentInfo>());
		}
		// 2 get the module and pageRank
		Map<String, Double> moduleMap = QueryWorker.getDocmodule(StorageGlobal.tableModTitle, urlMap.keySet());
		Map<String, Double> metaModuleMap = QueryWorker.getDocmodule(StorageGlobal.tableModMeta, urlMap.keySet());
		Map<String, Double> pageRankMap = QueryWorker.getPagerank(urlMap.keySet());
		//QueryWorker.logger.info("Module: " + moduleMap.size() + "; PageRank: " + pageRankMap.size());
		
		// 3 for each url in urlMap
		for(Entry<String, SearchInfo> entry: urlMap.entrySet()) {
			// 3.1  get all the words in the url
			Map<String, Double> wordsWeight = entry.getValue().getWordweights();
			SearchInfo metaInfo = metaMap.get(entry.getKey());
			double final_score = 0, vector_mul = 0, meta_mul = 0;
			//QueryWorker.logger.info("Doc: " + entry.getKey() + "; ");
			// 3.2 calculte the title score
			for(Entry<String, Double> weight_entry : wordsWeight.entrySet()) {
				vector_mul += weight_entry.getValue() * queryInfo.getWordsWeights().get(weight_entry.getKey());
				//QueryWorker.logger.info("Word: " + weight_entry.getKey() + "; two weight: " + weight_entry.getValue() + "; " + queryInfo.getWordsWeights().get(weight_entry.getKey()));
			}
			// 3.3 if the url have meta info calculate the meta score
			if(metaInfo != null) {
				for(Entry<String, Double> weight_entry : metaInfo.getWordweights().entrySet()) {
					meta_mul += weight_entry.getValue() * queryInfo.getWordsWeights().get(weight_entry.getKey());
				}
			}
			//QueryWorker.logger.info(entry.getKey() + "; " + entry.getValue());
			//QueryWorker.logger.info("1: " + vector_mul + "; 2: " + queryInfo.getModule() + "; 3: " + moduleMap.get(entry.getKey()) + "; 4: " + pageRankMap.get(entry.getKey()));
			// 3.4 if we have page rank, we get the page rank of this url
			Double pagerank = pageRankMap.get(entry.getKey());
			if (pagerank == null) {
				QueryWorker.logger.warn("pagerank not found: " + entry.getKey());
				pagerank = (double) (Math.log(1 + 1) / Math.log(SearchGlobal.scalePageRank));
			} else {
				pagerank = (double) (Math.log(1 + pagerank) / Math.log(SearchGlobal.scalePageRank));
			}
			// 3.5 we get the module
			Double title_module = moduleMap.containsKey(entry.getKey()) ? moduleMap.get(entry.getKey()) : 100;
			Double meta_module = metaModuleMap.containsKey(entry.getKey()) ? metaModuleMap.get(entry.getKey()) : 100;
			double title_tfidf = SearchGlobal.weightVectorTitle * vector_mul / (queryInfo.getModule() * title_module);
			double meta_tfidf = SearchGlobal.weightVectorMeta * meta_mul / (queryInfo.getModule() * meta_module);
			final_score = (title_tfidf + meta_tfidf) * pagerank;
			//QueryWorker.logger.info("Url: " + entry.getKey() + "; final score: " + final_score);
			// 3.6 update the weightMap
			weightMap.get(wordsWeight.size()).add(new DocumentInfo(entry.getKey(), "" , final_score, title_tfidf, meta_tfidf, pagerank));
		}
		return weightMap;
	}
	
	
	public static String search(List<String> words) {
		// 1. calcute tf of the query
		QueryWorker.logger.info("Begin Search");
		QueryInfo queryInfo = getQueryInfo(words);
		// 2. get all the words and their corresponds doc
		Map<String, SearchInfo> urlMap = getSearchInfo(StorageGlobal.tableFreqTitle, words);
		Map<String, SearchInfo> metaMap = getSearchInfo(StorageGlobal.tableFreqMeta, words);
		QueryWorker.logger.info("Url Map: " + urlMap.size());
		// the node don't have the matching doc
		if(urlMap.size() == 0)
			return "";
		// 3. calculate the weight
		Map<Integer, List<DocumentInfo>> weightMap = getWeightMap(queryInfo, urlMap, metaMap);
		QueryWorker.logger.info("WeightMap size: " + weightMap.size());
		// 4. Sort by values
		StringBuffer sb = new StringBuffer();
		for(int num : weightMap.keySet()) {
			List<DocumentInfo> results = weightMap.get(num);
			Collections.sort(results, new Comparator<DocumentInfo>() {
				@Override
				public int compare(DocumentInfo o1, DocumentInfo o2) {
					if(o1.getScore() > o2.getScore()) return -1;
					else if(o1.getScore() < o2.getScore()) return 1;
					else return 0;
				}
			});
			sb.append(num + CRLF);
			for(DocumentInfo di : results) {
				sb.append(di.toString() + CRLF);
			}
		}
		return sb.toString();
	}
}
