package cis455.project.rank;

public class DocumentInfo {

	private String url;
	private String title;
	private Double score;
	private Double tfidf;
	private Double pagerank;
	
	public DocumentInfo(String url, String title, double score) {
		this.url = url;
		this.title = title;
		this.score = score;
	}
	
	public DocumentInfo(String url, String title, double score, double tfidf, double pagerank) {
		this.url = url;
		this.title = title;
		this.score = score;
		this.tfidf = tfidf;
		this.pagerank = pagerank;
	}
	
	public String getUrl() { return url; }
	
	public String getTitle() { return title; }
	
	public double getScore() { return score; }
	
	public double getTfIdf() { return tfidf; }
	
	public double getPagerank() { return pagerank; }
}
