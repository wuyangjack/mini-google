package cis455.project;

import java.util.regex.Pattern;

/**
 * Value class that represents a webpage
 * @author Yang Wu
 *
 */
public class ValuePage {
	public static final String prefix = "L";
	public static final String delimeter = "|";
	public String[] outlinks = null;
	public double rank = 0;
	
	ValuePage() {}
	
	ValuePage(double rank, String[] outlinks) {
		this.rank = rank;
		this.outlinks = outlinks;
	}
	
	public static boolean is(String token) {
		String[] pair = token.split(Pattern.quote(delimeter), 2);
		if (pair.length < 2) return false;
		if (pair[0].equals(prefix)) return true;
		else return false;
	}
	
	public void deserialize(String token) throws Exception {
		String[] pair = token.split(Pattern.quote(delimeter), 3);
		if (pair.length < 2 || pair.length > 3) {
			throw new Exception("invalid page token length: " + pair.length);
		} else {
			rank = Double.parseDouble(pair[1]);
			if (pair.length == 2) outlinks = new String[0];
			else outlinks = pair[2].split(Pattern.quote(delimeter));
		}
	}
	
	public String serialize() {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append(delimeter);
		sb.append(String.valueOf(rank));
		for (String url : outlinks) {
			sb.append(delimeter);
			sb.append(url);
		}
		return sb.toString();
	}
}
