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

	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Fetch parameters
		String query = request.getParameter(UIGlobal.paraQuery);
		String page = request.getParameter(UIGlobal.paraPage);
		String amazon = request.getParameter(UIGlobal.paraAmazon);
		String youtube = request.getParameter(UIGlobal.paraYoutube);
		
		request.setAttribute(UIGlobal.attrPage, page);
		request.setAttribute(UIGlobal.attrQuery, query);
		
		query = UIWorker.filter(query);
		if(query == null){
			response.sendRedirect(UIGlobal.jspIndex);
			return;
		}
		
	    // Wikipedia
		String wikipediaUrl = UIWorker.wikipedia(query);
		request.setAttribute(UIGlobal.attrWikiUrl, wikipediaUrl);

	    // Amazon
		List<Item> amazonItems = null;
		if (amazon != null) {
			amazonItems = UIWorker.amazon(query);
		}
		request.setAttribute(UIGlobal.attrAmazonItems, amazonItems);

		// Youtube
		YoutubeItem youtubeItems = null;
		if (youtube != null) {
			youtubeItems = UIWorker.youtube(query);
		}
		request.setAttribute(UIGlobal.attrYoutubeItems, youtubeItems);

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
		request.setAttribute(UIGlobal.attrTitles, titles);
		request.setAttribute(UIGlobal.attrUrls, urls);
		
		// store information in sessions
		// HttpSession session = request.getSession();

		// Forward parameters to JSP
		RequestDispatcher view = request.getRequestDispatcher(UIGlobal.jspResult);
		view.forward(request, response);
	}

}
