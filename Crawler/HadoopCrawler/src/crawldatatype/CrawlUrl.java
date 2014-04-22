package crawldatatype;

public class CrawlUrl {
	
		private String url;
		private int depth;
		
		public CrawlUrl(String url, int depth) {
			this.url = url;
			this.depth = depth;
		}
		
		public String getUrl() { return url; }
		public int getDepth() { return depth; }
}
