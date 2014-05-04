package cis455.project.query;
import javax.servlet.*;
import javax.servlet.http.*;

import StopAndStemmer.WordPreprocessor;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class WorkerServlet extends HttpServlet {
	
	private static final long serialVersionUID = -1424404172694008578L;

	@Override
	public void init() throws ServletException {
		ServletConfig config = getServletConfig();
		String pathDatabase = config.getInitParameter(QueryGlobal.initWorkerDatabase);
		QueryWorker.initialize(pathDatabase);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Worker Servlet</TITLE></HEAD><BODY>");
		String query = request.getParameter(QueryGlobal.paraSearch);
		query = URLDecoder.decode(query, "UTF-8");
		List<String> words = new ArrayList<String>();
		for(String word : query.split("\\s")) {
			String w = WordPreprocessor.preprocess(word);
			if(w != null)
				words.add(w);
		}
		out.println("<P>" + query + "</P>");
		/*
		String table = request.getParameter(QueryGlobal.paraTable);
		String key = request.getParameter(QueryGlobal.paraKey);
		if (table == null || key == null) {
			out.println("<P>Use table/key for argument.</P>");
		} else {
			String result = QueryWorker.get(table, key);
			out.println("<P>" + result + "</P>");
		}
		*/
		out.println("</BODY></HTML>");
	}

	@Override
	public void destroy() {
		QueryWorker.close();
	}
}