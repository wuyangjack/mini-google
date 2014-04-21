package cis455.project;

import java.util.regex.Pattern;

/**
 * Value class that represents rank contribution
 * @author Yang Wu
 *
 */
public class ValueRank {
	public static final String prefix = "R";
	public static final String delimeter = "|";
	public double rank;
	
	ValueRank() {}
	
	ValueRank(double rank) {
		this.rank = rank;
	}
	
	public static boolean is(String token) {
		String[] pair = token.split(Pattern.quote(delimeter), 2);
		if (pair.length != 2) return false;
		if (pair[0].equals(prefix)) return true;
		else return false;
	}
	
	public void deserialize(String token) throws Exception {
		String[] pair = token.split(Pattern.quote(delimeter), 2);
		if (pair.length != 2) {
			throw new Exception("invalid format");
		} else {
			rank = Double.parseDouble(pair[1]);
		}
	}

	public String serialize() {
		return prefix + delimeter + String.valueOf(rank);
	}
}
