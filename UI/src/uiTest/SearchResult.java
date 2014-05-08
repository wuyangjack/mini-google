package uiTest;

public class SearchResult {
	public String[] titles = null;
	public String[] urls = null;
	public Integer count = null;
	
	SearchResult(String result) {
		String[] lines = result.split(UIGlobal.CRLF);
		count = lines.length;
		titles = new String[count];
		urls = new String[count];
		for (int i = 0; i < count; i ++ ) {
			String[] tokens = lines[i].split(UIGlobal.delimiterUI, 2);
			titles[i] = tokens[0];
			urls[i] = tokens[1];
		}
	}
}