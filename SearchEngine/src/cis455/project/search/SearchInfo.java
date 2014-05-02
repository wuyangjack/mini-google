package cis455.project.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchInfo {
	
	private Map<String, Double> wordWeights;
	private Map<String, List<Integer> > wordPositions;

	public SearchInfo() {
		wordWeights = new HashMap<String, Double>();
		wordPositions = new HashMap<String, List<Integer> >();
	}
	
	public void addWordweight(String word, double weight) {
		if(wordWeights == null) {
			wordWeights = new HashMap<String, Double>();
		}
		wordWeights.put(word, weight);
	}
	
	
	public void addWordPosition(String word, List<Integer> positions) {
		if(wordPositions == null) {
			wordPositions = new HashMap<String, List<Integer>>();
		}
		List<Integer> list = new ArrayList<Integer>(positions.size());
		Collections.copy(list, positions);
		wordPositions.put(word, list);
	}
	
	public Map<String, Double> getWordweights() {
		return wordWeights;
	}
	
	public Map<String, List<Integer>> getWordPositions() {
		return wordPositions;
	}
	
}
