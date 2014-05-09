package cis455.project.rank;

public class DocumentInfo {

	private String url;
	private String title;
	private Double score;
	private Double title_tfidf;
	private Double meta_tfidf;
	private Double pagerank;
	
	public DocumentInfo(String url, String title, double score) {
		this.url = url;
		this.title = title;
		this.score = score;
	}
	
	public DocumentInfo(String url, String title, double score, double title_tfidf, double meta_tfidf, double pagerank) {
		this.url = url;
		this.title = title;
		this.score = score;
		this.title_tfidf = title_tfidf;
		this.meta_tfidf = meta_tfidf;
		this.pagerank = pagerank;
	}
	
	public String getUrl() { return url; }
	
	public String getTitle() { return title; }
	
	public double getScore() { return score; }
	
	public double getTitleTfIdf() { return title_tfidf; }
	
	public double getMetaTfIdf() { return meta_tfidf; }
	
	public double getPagerank() { return pagerank; }
	
	public String toString() {
		if(title == null || title.length() == 0) {
			return url + "\t" + score + "\t" + title_tfidf + "\t" + meta_tfidf + "\t" + pagerank;
		}
		else {
			return url + "\t" + title + "\t" + score + "\t" + title_tfidf + "\t" + meta_tfidf + "\t" + pagerank;
		}
	}

}
