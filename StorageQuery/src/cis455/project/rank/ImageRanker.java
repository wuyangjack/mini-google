package cis455.project.rank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cis455.project.search.SearchWorker;

public class ImageRanker {

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
	public static Map<Integer, List<DocumentInfo>> rankDocument(String[] results) {
		List<String> inputs = parseResult(results);
		// 1. create tree map to sort by match number
		Map<Integer, List<DocumentInfo>> result = new TreeMap<Integer, List<DocumentInfo>>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;
			}
		});
		// 2. combine all results
		for(String input : inputs) {
			String[] args = input.split("\t", 4);
			if(args.length == 4) {
				//QueryMaster.logger.info("Input: " + input);
				//QueryMaster.logger.info("Args: " + args[0] + "; " + args[1]);
				int num = Integer.parseInt(args[0]);
				//QueryMaster.logger.info("Title length: " + title.length);
				DocumentInfo doc_info = new DocumentInfo(args[1], "", 0,
						 0,  0,  0);
				if(! result.containsKey(num)) {
					result.put(num, new ArrayList<DocumentInfo>());
				}
				result.get(num).add(doc_info);
			}
		}
		return result;
	}
}
