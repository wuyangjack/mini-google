package cis455.project;

public class StringDelimeter {
	public static String[] split(String value, String regex) {
		if(value.equals("")) return new String[0];
		else return value.split(regex);
	}
	
	public static String[] split(String value, String regex, int limit) {
		if(value.equals("")) return new String[0];
		else return value.split(regex, limit);
	}
}
