package cis455.project.query;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class MasterServlet extends HttpServlet {
	private static final long serialVersionUID = 1257986842825393951L;

	@Override
	public void init() throws ServletException {
		String[] servers = new String[]{
				"ec2-50-16-82-61.compute-1.amazonaws.com:8080", 
				"ec2-54-221-151-217.compute-1.amazonaws.com:8080",
				"ec2-54-237-211-134.compute-1.amazonaws.com:8080",
				"ec2-54-82-187-46.compute-1.amazonaws.com:8080"
		};
		QueryMaster.initialize(servers);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Master Servlet</TITLE></HEAD><BODY>");
		String key = request.getParameter("query");
		String[] result = QueryMaster.search(key);
		out.println("<P>" + result.length + "</P>");
		out.println("</BODY></HTML>");
	}
}