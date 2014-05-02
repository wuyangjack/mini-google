package cis455.project.rank;

public class DocumentInfo {

	private String url;
	private String title;
	private Double score;
	
	public DocumentInfo(String url, String title, double score) {
		this.url = url;
		this.title = title;
		this.score = score;
	}
	
	public String getUrl() { return url; }
	
	public String getTitle() { return title; }
	
	public double getScore() { return score; }
	
}
