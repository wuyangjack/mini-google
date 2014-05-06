package cis455.project.search;

public class ScoreInfo {
	double score;
	double tfidf;
	double pagerank;
	
	public ScoreInfo(double score, double tfidf, double pagerank) {
		this.score = score;
		this.tfidf = tfidf;
		this.pagerank = pagerank;
	}
	
	public double getScore() { return score; }
	
	public double getTfIdf() { return tfidf; }
	
	public double getPagerank() { return pagerank; }
	
	public String toString() { return score + "\t" + tfidf + "\t" + pagerank; }
}
