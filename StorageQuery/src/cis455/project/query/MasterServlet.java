package cis455.project.query;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class MasterServlet extends HttpServlet {
	private static final long serialVersionUID = 1257986842825393951L;

	@Override
	public void init() throws ServletException {
		String[] servers = new String[]{"127.0.0.1:8080", "127.0.0.1:8080"};
		QueryMaster.initialize(servers);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Master Servlet</TITLE></HEAD><BODY>");
		String table = request.getParameter("table");
		String key = request.getParameter("key");
		String result = QueryMaster.get(table, key);
		out.println("<P>" + result + "</P>");
		out.println("</BODY></HTML>");
	}
}