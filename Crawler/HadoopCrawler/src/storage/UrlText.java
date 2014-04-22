package storage;

public class UrlText {

	private String url;
	private boolean visited;
	
	public UrlText(String url, boolean visited) {
		this.url = url;
		this.visited = visited;
	}
	
	public String getUrl() { return url; }
	
	public boolean isVisited() { return visited; }
	
	public void setUrl(String url) { this.url = url; }
	
	public void setVisited(boolean visited) { this.visited = visited; }
}
