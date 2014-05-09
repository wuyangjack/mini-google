package cis455.project.search;

import java.util.HashSet;
import java.util.Set;

public class SearchGlobal {
	public static final double scalePageRank = 10;
	public static final double defaultPageRank = 1;
	public static final double defaultVectorMeta = 0;
	public static final double weightVectorMeta = 0.5;
	public static final double weightVectorTitle = 1;
	
	public static final Set<String> blackList; 
	static {
		blackList = new HashSet<String>();
		blackList.add("about.about.com");
	}
}
