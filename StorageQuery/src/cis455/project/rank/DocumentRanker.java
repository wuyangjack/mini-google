package cis455.project.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cis455.project.storage.Storage;
import cis455.project.storage.StorageGlobal;

public class DocumentRanker {
	
	private static List<String> parseResult(String[] results) {
		List<String> inputs = new ArrayList<String>();
		for(String result : results) {
			for(String s : result.split("\r\n")) {
				inputs.add(s);
			}
		}
		return inputs;
	}
	// input: url + \t + score
	public static List<DocumentInfo> rankDocument(Storage database, String[] results) {
		List<String> inputs = parseResult(results);
		List<DocumentInfo> result = new ArrayList<DocumentInfo>();
		for(String input : inputs) {
			String[] args = input.split("\t", 2);
			String[] title = database.get(StorageGlobal.tableTitle, args[0]);
			DocumentInfo doc_info = new DocumentInfo(args[0], title.length > 0 ? title[0] : "", Double.parseDouble(args[1]));
			result.add(doc_info);
		}
		Collections.sort(result, new Comparator<DocumentInfo>() {

			public int compare(DocumentInfo arg0, DocumentInfo arg1) {
				if(arg0.getScore() > arg1.getScore())
					return -1;
				else if(arg0.getScore() < arg1.getScore())
					return 1;
				else
					return 0;
			}
		}); 
		return result;
	}
}
