package cis455.project.ui;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import cis455.project.amazon.Item;
import cis455.project.youtube.YoutubeItem;

public class UIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String, SearchResult> sessions = null;
	private static SecureRandom sessionGenerator = null;
	private static SpellChecker spellChecker = null;
	
	@Override
	public void init() throws ServletException {
		UIWorker.logger.info("UI servlet initialized");
		UIWorker.logger.info("initalized sessions");
		sessions = new HashMap<String, SearchResult>();
		sessionGenerator = new SecureRandom();
		ServletConfig config = getServletConfig();
		String pathDict = config.getInitParameter(UIGlobal.initPathDict);
		UIWorker.logger.info("initalize dictionary: " + pathDict);
		spellChecker = new SpellChecker(pathDict);
	}
	
	private void search(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, InterruptedException, ExecutionException, TimeoutException {
		// Start
		Long timeStart = System.currentTimeMillis();
		UIWorker.logger.info("request: " + request.getPathInfo());
		
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
			String spellcheck = request.getParameter(UIGlobal.paraSpellCheck);
			String mode = request.getParameter(UIGlobal.paraCatagory);
			
			
			// Session 
			sessionID = new BigInteger(130, sessionGenerator).toString(32);
			UIWorker.logger.info("new session: " + sessionID);

			// Query
			query = UIWorker.filter(query);
			if(query == null){
				UIWorker.logger.error("no query");
				response.sendRedirect(UIGlobal.urlError());
				return;
			}
			
			// Spell check
			String queryCheck = null;
			if (spellcheck != null) {
				queryCheck = spellChecker.correct(query);
			}
			
			// Amazon
			FutureTask<List<Item>> amazon_thread = null;
			if (amazon != null) {
				amazon_thread = new FutureTask<List<Item>>(new AmazonThread(query));
				new Thread(amazon_thread).start();
			}
			
		    // Wikipedia
			FutureTask<String> wiki_thread = null;
			if (wiki != null) {
				wiki_thread = new FutureTask<String>(new WikiThread(query));
				new Thread(wiki_thread).start();
			}

			// Youtube
			FutureTask<YoutubeItem> youtube_thread = null;
			if (youtube != null) {
				youtube_thread = new FutureTask<YoutubeItem>(new YoutubeThread(query));
				new Thread(youtube_thread).start();
			}

			FutureTask<String> title_thread = null;
			if (mode == null) {
				UIWorker.logger.error("unknown mode");
				response.sendRedirect(UIGlobal.urlError());
				return;
			}
			if (mode.equals(UIGlobal.modeSearchWeb)) {
				title_thread = new FutureTask<String>(new MessageThread(query, UIGlobal.urlMasterSearchWeb));
			} else if (mode.equals(UIGlobal.modeSearchImage)) {
				title_thread = new FutureTask<String>(new MessageThread(query, UIGlobal.urlMasterSearchImage));
			} else {
				UIWorker.logger.error("unknown mode");
				response.sendRedirect(UIGlobal.urlError());
				return;
			}
			new Thread(title_thread).start();
			
			// Search
			String message = title_thread.get(5, TimeUnit.SECONDS);
			if (mode.equals(UIGlobal.modeSearchImage)) {
				UIWorker.logger.error(message + "XXXXXXXX");
				return;
			}
			String wikipediaUrl = wiki_thread != null ? wiki_thread.get(5, TimeUnit.SECONDS) : null;
			YoutubeItem youtubeItems = youtube_thread != null ? youtube_thread.get(5, TimeUnit.SECONDS) : null;
			List<Item> amazonItems = amazon_thread != null ? amazon_thread.get(5, TimeUnit.SECONDS) : null;
			
			result = new SearchResult(mode, sessionID, message, page, query, queryCheck, amazonItems, youtubeItems, wikipediaUrl);
			
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
		Long timeEnd = System.currentTimeMillis();
		double timeDuration = (timeEnd - timeStart) / 1000.00;
		DecimalFormat df = new DecimalFormat("#.##");
		String time = df.format(timeDuration);
		request.setAttribute(UIGlobal.attrTime, time);
		request.setAttribute(UIGlobal.attrSearchResult, result);
		RequestDispatcher view = request.getRequestDispatcher(UIGlobal.jspResult);
		UIWorker.logger.info("enter forward");
		view.forward(request, response);
	}
	

	static class WikiThread implements Callable<String> {

		private String query;

		public WikiThread(String query) {
			this.query = query;
		}

		@Override
		public String call() throws Exception {
			return UIWorker.wikipedia(query);
		}
	} 

	static class MessageThread implements Callable<String> {

		private String query;
		private String urlSearch;

		public MessageThread(String query, String urlSearch) {
			this.query = query;
			this.urlSearch = urlSearch;
		}

		@Override
		public String call() throws Exception {
			return UIWorker.search(query, urlSearch);
		}
	} 

	static class AmazonThread implements Callable<List<Item>> {

		private String query;

		public AmazonThread(String query) {
			this.query = query;
		}

		@Override
		public List<Item> call() throws Exception {
			return UIWorker.amazon(query);
		}
	} 

	static class YoutubeThread implements Callable<YoutubeItem> {

		private String query;

		public YoutubeThread(String query) {
			this.query = query;
		}

		@Override
		public YoutubeItem call() throws Exception {
			return UIWorker.youtube(query);
		}
	} 
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//try {
			try {
				this.search(request, response);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				UIWorker.logger.error("error in search", e);
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				UIWorker.logger.error("error in search", e);
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				UIWorker.logger.error("error in search", e);
				e.printStackTrace();
			}
		//} catch (Exception e) {
		//	UIWorker.logger.error("error in search", e);
		//					response.sendRedirect(UIGlobal.urlError());
		//	return;
		//}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//try {
			try {
				this.search(request, response);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				UIWorker.logger.error("error in search", e);
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				UIWorker.logger.error("error in search", e);
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				UIWorker.logger.error("error in search", e);
				e.printStackTrace();
			}
		//}  catch (Exception e) {
		//	UIWorker.logger.error("error in search", e);
		//					response.sendRedirect(UIGlobal.urlError());
		//	return;
		//}
	}

}
