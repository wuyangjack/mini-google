package cis455.project.ui;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<String, SearchResult> sessions = new HashMap<String, SearchResult>();
	private static SecureRandom sessionGenerator = new SecureRandom();

	@Override
	public void init() throws ServletException {
		UIWorker.logger.info("UI servlet initialized");
		UIWorker.logger.info("initalized sessions");
		
	}
	
	private void search(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// Get sessionID
		String sessionID = request.getParameter(UIGlobal.paraSessionID);
		SearchResult result = null;
		
		if (sessionID == null) {
			// Fetch parameters
			String query = request.getParameter(UIGlobal.paraQuery);
			String page = request.getParameter(UIGlobal.paraPage);
			String amazon = request.getParameter(UIGlobal.paraAmazon);
			String youtube = request.getParameter(UIGlobal.paraYoutube);
			String wiki = request.getParameter(UIGlobal.paraWiki);
			
			// Session 
			sessionID = new BigInteger(130, sessionGenerator).toString(32);
			UIWorker.logger.info("new session: " + sessionID);

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
			result = new SearchResult(sessionID, message, page, query, amazonItems, youtubeItems, wikipediaUrl);
			
			// Save to session
			this.sessions.put(sessionID, result);
		} else {
			UIWorker.logger.info("fetch session: " + sessionID);
			result = this.sessions.get(sessionID);
			String page = request.getParameter(UIGlobal.paraPage);
			UIWorker.logger.info("change to page: " + page);
			result.setPage(page);
		}
		
		// Forward parameters to JSP
		request.setAttribute(UIGlobal.attrSearchResult, result);
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
