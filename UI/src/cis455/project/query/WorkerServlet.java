package cis455.project.query;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

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
		String table = request.getParameter(QueryGlobal.paraTable);
		String key = request.getParameter(QueryGlobal.paraKey);
		String result = QueryWorker.get(table, key);
		out.println("<P>Size is " + result + "</P>");
		out.println("</BODY></HTML>");
	}

	@Override
	public void destroy() {
		QueryWorker.close();
	}
}