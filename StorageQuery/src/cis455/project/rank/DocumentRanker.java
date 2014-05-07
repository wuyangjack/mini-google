package cis455.project.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import cis455.project.search.SearchWorker;
import cis455.project.storage.Storage;
import cis455.project.storage.StorageGlobal;

public class DocumentRanker {
	
	private static List<String> parseResult(String[] results) {
		List<String> inputs = new ArrayList<String>();
		// 1. for each result from worker
		for(String result : results) {
			if(result.length() > 0)
				// 1.1 split the reuslt by CRLF
				for(String s : result.split(SearchWorker.CRLF)) {
					inputs.add(s);
				}
		}
		return inputs;
	}

	// input: url + \t + score
	public static Map<Integer, List<DocumentInfo>> rankDocument(Storage database, String[] results) {
		List<String> inputs = parseResult(results);
		// 1. create tree map to sort by match number
		Map<Integer, List<DocumentInfo>> result = new TreeMap<Integer, List<DocumentInfo>>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;
			}
		});
		// 2. combine all results
		int num = -1;
		for(String input : inputs) {
			String[] args = input.split("\t", 5);
			// 2.1 if it is match number
			if(args.length == 1) {
				num = Integer.parseInt(args[0].trim());
			}
			// 2.2 if it is url + info
			else {
				//QueryMaster.logger.info("Input: " + input);
				//QueryMaster.logger.info("Args: " + args[0] + "; " + args[1]);
				String[] title = database.get(StorageGlobal.tableTitle, args[0]);
				//QueryMaster.logger.info("Title length: " + title.length);
				DocumentInfo doc_info = new DocumentInfo(args[0], title.length > 0 ? title[0] : "default", Double.parseDouble(args[1]),
						 Double.parseDouble(args[2]),  Double.parseDouble(args[3]),  Double.parseDouble(args[4]));
				if(! result.containsKey(num)) {
					result.put(num, new ArrayList<DocumentInfo>());
				}
				result.get(num).add(doc_info);
			}
		}
		for(Integer i : result.keySet()) {
			List<DocumentInfo> doc_infos = result.get(i);
			Collections.sort(doc_infos, new Comparator<DocumentInfo>() {
				public int compare(DocumentInfo arg0, DocumentInfo arg1) {
					if(arg0.getScore() > arg1.getScore())
						return -1;
					else if(arg0.getScore() < arg1.getScore())
						return 1;
					else
						return 0;
				}
			}); 
		}
		return result;
	}
}
