package cis455.project.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import cis455.project.amazon.ItemSearchTool;
import cis455.project.youtube.YouTubeThread;
import cis455.project.youtube.YoutubeItem;

import com.google.api.services.samples.youtube.cmdline.youtube_cmdline_search_sample.Search;

//import cis455.project.query.QueryMaster;
//import cis455.project.storage.StorageGlobal;

/**
 * Servlet implementation class QueryCheckServlet
 */
public class QueryCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init() throws ServletException {

	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Fetch parameters
		String query = request.getParameter("query");
		String page = request.getParameter("page");
		String amazon = request.getParameter("amazon");
		String youtube = request.getParameter("youtube");		
		
		if(page == null){  // right after search
			if(query == null){
				response.sendRedirect(UIGlobal.jspIndex);
				return;      // if null then return to main page
			}
			query = query.trim();
			if(query.length() == 0){
				response.sendRedirect(UIGlobal.jspIndex);
				return;
			}
		
			// put away all the symbols
			query = query.replaceAll("\\W+", " ");
			query = query.toLowerCase();
			
			query = query.trim();
			if(query.length() == 0){
				response.sendRedirect(UIGlobal.jspIndex);
				return;
			}
			
		    String[] query_split_list = query.split("\\s"); 
		    
		    // get wiki search page
		    String wiki_string = query_split_list[0];
		    for(int i = 1; i < query_split_list.length; i++){
		    	wiki_string = wiki_string + "+" + query_split_list[i];
		    }
		    wiki_string = "https://www.wikipedia.org/search-redirect.php?family=wikipedia&search=" + wiki_string + "&language=en&go=++%E2%86%92++&go=Go";
			
		    /*
		    YoutubeItem youtube_result = new YoutubeItem();
		    Thread youtubeQuery= null;
		    if(youtube != null){
		    	youtubeQuery = new YouTubeThread(query);
		    	youtubeQuery.start();
		    }   
		    */
		    
		    // amazon API 
		    ItemSearchTool amazon_tool = new ItemSearchTool();
		    if(amazon != null)
		    	amazon_tool.fetch(query);
		    
		    // Search request
			
			ArrayList<String> title_results = new ArrayList<String>();
			ArrayList<String> meta_results = new ArrayList<String>();
			ArrayList<String> body_results = new ArrayList<String>();
			ArrayList<String> pagerank_results = new ArrayList<String>();
//			for(int i = 0; i < query_split_list.length; i++){
//				title_results.add(QueryMaster.get(StorageGlobal.tableFreqTitle, query_split_list[i]));
//				meta_results.add(QueryMaster.get(StorageGlobal.tableFreqMeta, query_split_list[i]));
//				body_results.add(QueryMaster.get(StorageGlobal.tableFreqBody, query_split_list[i]));
//			}
			
			// Youtube API
			Search youtube_tool = new Search();
			YoutubeItem youtube_result = new YoutubeItem();
			if(youtube != null){
				youtube_result.parse(youtube_tool.search(query));
			}
			/*
			if(youtube != null){
				try {
					youtubeQuery.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			*/
			
			// Search
			String message = UIWorker.search(query);
			SearchResult result = new SearchResult(message);
			result.setPage(1);
			String[] titles = result.getPageTitles();
			String[] urls = result.getPageUrls();
			UIWorker.logger.info("XXX" + result.count + "XXX");
			for (int i = 0;  i < urls.length; i ++) {
				UIWorker.logger.info("Title: " + titles[i] + "; URL: " + urls[i]);
			}
			
			// store information in sessions
			// HttpSession session = request.getSession();
			request.setAttribute("wiki", wiki_string);
			request.setAttribute("amazon_items", amazon_tool.getItems());
			request.setAttribute("youtube_items", youtube_result);
			request.setAttribute("page", "1");
			request.setAttribute("query", query);
			request.setAttribute("titles", titles);
			request.setAttribute("urls", urls);


			// forward req and resp to JSP
			RequestDispatcher view = request.getRequestDispatcher(UIGlobal.jspResult);
			view.forward(request, response);
		}
		else{ // if after pressing prev or next
			int page_num = Integer.parseInt(page);
			RequestDispatcher view = request.getRequestDispatcher(UIGlobal.jspResult);
			view.forward(request, response);
		}
		
	}

}