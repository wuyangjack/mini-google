package storage;

import java.util.HashSet;
import java.util.Set;

public class Content {

	private Set<String> hash_urls;
	
	public Content(Set<String> hash_urls) {
		this.hash_urls = hash_urls;
	}
	
	public Content(String hash_url) {
		if(this.hash_urls == null)
			this.hash_urls = new HashSet<String>();
		this.hash_urls.add(hash_url);
	}
	
	public Set<String> getHashUrls() { return hash_urls; }
	
	public void addHashUrls(String hash_url) { 
		if(hash_urls == null)
			hash_urls = new HashSet<String>();
		hash_urls.add(hash_url);	
	}
	
	public void setHashUrls(Set<String> hash_urls) { 
		hash_urls = new HashSet<String>();
		this.hash_urls.addAll(hash_urls);
	}
}
