package general;

public class WordPreprocessor {
	
	private static StopWords stopWords = new StopWords();
	private static PorterStemmer stemmer = new PorterStemmer();
	
	/**
	 * To lowercase, excluding stop words, word stemming
	 * @param rawWord
	 * @return Return the processed word; null if it is a stop word
	 */
	public static String preprocess(String rawWord){
		String word = rawWord.toLowerCase();
		if(stopWords.isStopWord(word)){
			return null;
		}
		return stemmer.stem(word);
	}

}
