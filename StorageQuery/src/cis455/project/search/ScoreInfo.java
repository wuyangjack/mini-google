package cis455.project.search;

public class ScoreInfo {
	double score;
	double title_tfidf;
	double meta_tfidf;
	double pagerank;
	
	public ScoreInfo(double score, double title_tfidf, double meta_tfidf, double pagerank) {
		this.score = score;
		this.title_tfidf = title_tfidf;
		this.meta_tfidf = meta_tfidf;
		this.pagerank = pagerank;
	}
	
	public double getScore() { return score; }
	
	public double getTitleTfIdf() { return title_tfidf; }
	
	public double getMetaTfIdf() { return meta_tfidf; }

	public double getPagerank() { return pagerank; }
	
	public String toString() { return score + "\t" + title_tfidf + "\t" + meta_tfidf + "\t" + pagerank;}
}
