package cis455.project.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class QueryWorker{

	public static List<TFIDFEntry> getWordweight(String tableName, List<String> words) {
		List<TFIDFEntry> tf_entries = new ArrayList<TFIDFEntry>();
		TFIDFEntry entry1 = new TFIDFEntry("java", "url1", 2.45, "[]");
		tf_entries.add(entry1);
		entry1 = new TFIDFEntry("java", "url2", 1.45, "[]");
		tf_entries.add(entry1);
		entry1 = new TFIDFEntry("list", "url1", 0.45, "[]");
		tf_entries.add(entry1);
		return tf_entries;
	}

	public static Map<String, Double> getPagerank(Set<String> urls) {
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("url1", 1.0);
		map.put("url2", 1.0);
		return map;
	}

	public static Map<String, Double> getDocmodule(String tableName, Set<String> urls) {
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("url1", 1.0);
		map.put("url2", 1.0);
		return map;
	}

}
