package cis455.project.search;

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
		wordPositions.put(word, positions);
	}
	
	public Map<String, Double> getWordweights() {
		return wordWeights;
	}
	
	public Map<String, List<Integer>> getWordPositions() {
		return wordPositions;
	}
	
}
