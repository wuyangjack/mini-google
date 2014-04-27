package cis455.project.query;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class WorkerServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Simple Servlet</TITLE></HEAD><BODY>");
		out.println("<P>Hello!This is cool!</P>");
		out.println("</BODY></HTML>");		
	}
}