package cis455.project.query;
import javax.servlet.*;
import javax.servlet.http.*;

import cis455.project.rank.DocumentInfo;

import java.io.*;
import java.util.List;

public class MasterServlet extends HttpServlet {
	private static final long serialVersionUID = 1257986842825393951L;

	@Override
	public void init() throws ServletException {
		String[] servers = new String[]{
				"ec2-50-16-82-61.compute-1.amazonaws.com:8080" , 
				"ec2-54-221-151-217.compute-1.amazonaws.com:8080",
				"ec2-54-237-211-134.compute-1.amazonaws.com:8080",
				"ec2-54-82-187-46.compute-1.amazonaws.com:8080"
		};
		ServletConfig config = getServletConfig();
		String pathDatabase = config.getInitParameter(QueryGlobal.initPathDatabase);
		QueryMaster.initialize(servers, pathDatabase);
	}
	
	private static final String header = "<HTML><HEAD><TITLE>Master Servlet</TITLE></HEAD><BODY>";
	private static final String footer = "</BODY></HTML>";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(header);
		// Read mode
		String mode = null;
		if(false == request.getParameterMap().containsKey(QueryGlobal.paraMode)) {
			out.println("<P>Unknown mode!</P>");
		} else {
			mode = request.getParameter(QueryGlobal.paraMode);
		}
		if (mode.equals(QueryGlobal.modeSearch)) {
			String query = request.getParameter(QueryGlobal.paraQuery);
			List<DocumentInfo> results = QueryMaster.search(query);
			out.println("<P>Results: " + results.size() + "</P>");
			for(DocumentInfo di : results) {
				out.println("<p> Url: " + di.getUrl() + "; Title: " + di.getTitle() + "; " + di.getScore() + "; " + di.getTfIdf() + "; " + di.getPagerank() + "</p>");
			}
		} else if (mode.equals(QueryGlobal.modeGet)) {
			if (request.getParameterMap().containsKey(QueryGlobal.paraTable)
					&& request.getParameterMap().containsKey(QueryGlobal.paraKey)) {
				String table = request.getParameter(QueryGlobal.paraTable);
				String key = request.getParameter(QueryGlobal.paraKey);
				String[] results = QueryMaster.get(table, key);
				out.println("<P>Results: " + results.length + "</P>");
				for (String result : results) {
					out.println("<P>" + result + "</P>");
				}
			} else {
				QueryMaster.logger.error("no table / no key");
			}
		} else {
			QueryMaster.logger.error("unknown mode");
		}
		
		out.println(footer);
	}
}