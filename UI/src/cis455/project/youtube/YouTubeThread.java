package cis455.project.youtube;

import com.google.api.services.samples.youtube.cmdline.youtube_cmdline_search_sample.Search;

public class YouTubeThread extends Thread{
	
	private String query = null;
	
	public YouTubeThread(String query){
		this.query = query;
	}

	@Override
	public void run(){
		YoutubeItem youtube_result = new YoutubeItem();
		youtube_result.parse(Search.search(query));
	}

}
