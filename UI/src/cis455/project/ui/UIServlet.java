package cis455.project.ui;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import cis455.project.amazon.Item;
import cis455.project.youtube.YoutubeItem;

public class UIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init() throws ServletException {
		UIWorker.logger.info("UI servlet initialized");
	}
	
	
	
	private void search(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// Fetch parameters
		String query = request.getParameter(UIGlobal.paraQuery);
		String page = request.getParameter(UIGlobal.paraPage);
		String amazon = request.getParameter(UIGlobal.paraAmazon);
		String youtube = request.getParameter(UIGlobal.paraYoutube);
		String wiki = request.getParameter(UIGlobal.paraWiki);
		
		// Query
		query = UIWorker.filter(query);
		if(query == null){
			response.sendRedirect(UIGlobal.jspIndex);
			return;
		}
		
	    // Wikipedia
		String wikipediaUrl = null;
		if (wiki != null) {
			wikipediaUrl = UIWorker.wikipedia(query);
		}

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
		SearchResult result = new SearchResult(message, page, query, amazonItems, youtubeItems, wikipediaUrl);
		request.setAttribute(UIGlobal.attrSearchResult, result);
		
		// Forward parameters to JSP
		RequestDispatcher view = request.getRequestDispatcher(UIGlobal.jspResult);
		view.forward(request, response);
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.search(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.search(request, response);
	}

}
