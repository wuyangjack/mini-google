package cis455.project.query;
import javax.servlet.*;
import javax.servlet.http.*;


import java.io.*;
import java.net.URLDecoder;

public class WorkerServlet extends HttpServlet {
	
	private static final long serialVersionUID = -1424404172694008578L;

	@Override
	public void init() throws ServletException {
		ServletConfig config = getServletConfig();
		String pathDatabase = config.getInitParameter(QueryGlobal.initPathDatabase);
		QueryWorker.initialize(pathDatabase);
	}
	
	private static final String header = "<HTML><HEAD><TITLE>Worker Servlet</TITLE></HEAD><BODY>";
	private static final String footer = "</BODY></HTML>";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		// Read mode
		String mode = null;
		if(false == request.getParameterMap().containsKey(QueryGlobal.paraMode)) {
			out.println(header);
			out.println("<P>Unknown mode!</P>");
			out.println(footer);
		} else {
			mode = request.getParameter(QueryGlobal.paraMode);
		}
		
		if (mode.equals(QueryGlobal.modeSearch)) {
			if (false == request.getParameterMap().containsKey(QueryGlobal.paraSearch)) {
				QueryWorker.logger.error("no query string");
			} else {
				String query = request.getParameter(QueryGlobal.paraSearch);
				query = URLDecoder.decode(query, "UTF-8");
				String result = QueryWorker.search(query);
				out.println(result);
			}
		} else if (mode.equals(QueryGlobal.modeGet)) {
			if (request.getParameterMap().containsKey(QueryGlobal.paraTable)
					&& request.getParameterMap().containsKey(QueryGlobal.paraKey)) {
				String table = request.getParameter(QueryGlobal.paraTable);
				String key = request.getParameter(QueryGlobal.paraKey);
				QueryWorker.logger.info("get table | key: " + table + " | " + key);
				String result = QueryWorker.get(table, key);
				out.println("<P>" + result + "</P>");
			} else {
				QueryWorker.logger.error("no table / no key");
			}
		} else {
			QueryWorker.logger.error("unknown mode");
		}
	}

	@Override
	public void destroy() {
		QueryWorker.close();
	}
}