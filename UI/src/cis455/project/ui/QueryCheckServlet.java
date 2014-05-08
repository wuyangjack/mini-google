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


import cis455.project.amazon.Item;
import cis455.project.amazon.ItemSearchTool;
import cis455.project.youtube.YouTubeThread;
import cis455.project.youtube.YoutubeItem;

import com.google.api.services.samples.youtube.cmdline.youtube_cmdline_search_sample.Search;

public class QueryCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init() throws ServletException {

	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("static-access")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Fetch parameters
		String query = request.getParameter("query");
		String page = request.getParameter("page");
		String amazon = request.getParameter("amazon");
		String youtube = request.getParameter("youtube");		
		
		if(page == null){  // right after search	
			query = UIWorker.filter(query);
			if(query == null){
				response.sendRedirect(UIGlobal.jspIndex);
				return;
			}
			
		    // Wikipedia
			String wikipediaUrl = UIWorker.wikipedia(query);
				   
		    // Amazon
			List<Item> amazonItems = null;
			if (amazon != null) {
				amazonItems = UIWorker.amazon(query);
			}
	
			// Youtube
			YoutubeItem youtubeItems = null;
			if (youtube != null) {
				youtubeItems = UIWorker.youtube(query);
			}
			
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
			request.setAttribute("wiki", wikipediaUrl);
			request.setAttribute("amazon_items", amazonItems);
			request.setAttribute("youtube_items", youtubeItems);
			request.setAttribute("page", "1");
			request.setAttribute("query", query);
			request.setAttribute("titles", titles);
			request.setAttribute("urls", urls);

			// Forward parameters to JSP
			RequestDispatcher view = request.getRequestDispatcher(UIGlobal.jspResult);
			view.forward(request, response);
		}
	}

}
