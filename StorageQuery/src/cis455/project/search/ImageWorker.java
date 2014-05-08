package cis455.project.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cis455.project.query.QueryWorker;
import cis455.project.storage.StorageGlobal;

public class ImageWorker {
	public static final String CRLF = "\r\n";
	
	private static Map<String, Integer> getSearchInfo(String tableName, List<String> words) {
		//QueryWorker.logger.info("Begin Table: " + tableName);
		// url : number
		Map<String, Integer> urlMap =  new HashMap<String, Integer>();
		List<String> url_entries = QueryWorker.getImageweight(tableName, words);
		Iterator<String> url_iterator = url_entries.iterator();
		while(url_iterator.hasNext()) {
			String url = url_iterator.next();
			Integer i = 0;
			// 1.1 for each word, add the weight and position to url map
			if(! urlMap.containsKey(url)) {
				i = 1;
			}
			else {
				int j = urlMap.get(url) + 1;
				i = j >= words.size() ? words.size() : j;
			}
			// 1.3 last step, update this url
			urlMap.put(url, i);
		}
		return urlMap;
	}

	private static Map<Integer, List<String>> getImageWeight(Map<String, Integer> urlMap) {
		Map<Integer, List<String> > weightMap = new HashMap<Integer, List<String> >();
		for(Entry<String, Integer> entry : urlMap.entrySet()) {
			String url = entry.getKey();
			Integer num = entry.getValue();
			if(! weightMap.containsKey(num)) {
				weightMap.put(num, new ArrayList<String>());	
			}
			weightMap.get(num).add(url);
		}
		return weightMap;
	}
	
	public static String search(List<String> words) {
		Map<String, Integer> urlMap = getSearchInfo(StorageGlobal.tableFreqImage, words);
		QueryWorker.logger.info("Url Map: " + urlMap.size());
		// the node don't have the matching doc
		if(urlMap.size() == 0)
			return "";
		Map<Integer, List<String>> weightMap = getImageWeight(urlMap);
		StringBuffer sb = new StringBuffer();
		for(int num : weightMap.keySet()) {
			List<String> results = weightMap.get(num);
			for(String url : results) {
				sb.append(num + "\t" + url + CRLF);
			}
		}
		return sb.toString();
	}
}
