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
		UIWorker.logger.info("received matches: " + count);
		for (int i = 0; i < count; i ++ ) {
			UIWorker.logger.info(lines[i]);
			String[] tokens = lines[i].split(UIGlobal.delimiterUI);
			UIWorker.logger.info("tokens: " + tokens.length);
			titles[i] = tokens[0];
			urls[i] = tokens[1];
		}
	}
}
