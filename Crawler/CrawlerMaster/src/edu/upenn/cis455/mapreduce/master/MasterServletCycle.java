package edu.upenn.cis455.mapreduce.master;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.http.client.ClientProtocolException;

public class MasterServletCycle extends HttpServlet {

  static final long serialVersionUID = 455555001;
  private static final String WORKERSTATUS = "/workerstatus";
  private static final String STATUS = "/status";
  private static final String ERROR = "/error";
  private static final String RUNWORK = "/runwork";
  private static final String IDLE = "idle";
  private static final String MAPPING = "mapping";
  private static final String WAITING = "waiting";
  private static final String WAITING1 = "waiting1";
  //private static final long EPOCH = 30000;
  
  // Store the worker status from /workerstatus; IP: status
  private static Map<String, Status> statusMap = new Hashtable<String, Status>();
  // Store the job: job status For each job
  private static JobStatus jobStatus = null;
  private static int max_iter_num = 0;
  // Give the key for process statusMap
  //private static ReentrantLock lock = new ReentrantLock();
  
  public void init() {
	  LogClass.init();
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
       throws java.io.IOException {
	    String servletPath = request.getServletPath();
	    LogClass.info("Receive from Worker: " + servletPath);
	    // each worker is done
	    if(servletPath.equalsIgnoreCase(WORKERSTATUS)) {
	    	// Update statusMap and check if job is done
	    	processWorkerStatus(request);
	    }
	    // See each worker status
	    else if(servletPath.equalsIgnoreCase(STATUS)) {
	    	// Show status map and form
	    	showStatus(request, response);
	    }
	    else if(servletPath.equalsIgnoreCase(ERROR)) {
	    	String error = request.getParameter("err");
	    	response.setContentType("text/html");
	        PrintWriter out = response.getWriter();
		    out.println("<html><head><title>Master</title></head>");
		    out.println("<body>" + error == null ? "Error" : error +"</body></html>");
	    }
	    else {
	    	response.setContentType("text/html");
	        PrintWriter out = response.getWriter();
		    out.println("<html><head><title>Master</title></head>");
		    out.println("<body>Hi, I am the master!</body></html>");
	    }
  }
  
  
  // Form Submit, do RunWork
  public void doPost(HttpServletRequest request, HttpServletResponse response) 
  	throws java.io.IOException {
	  String servletPath = request.getServletPath();
	  
	  if(servletPath.equalsIgnoreCase(RUNWORK))
		  try {
			  // lock to get the statusMap's idle worker, jobMap (also need to go through the worker map)
			  String inputDir = request.getParameter("inputdir");
			  int mapNum = Integer.parseInt(request.getParameter("mapnum"));
			  int redNum = Integer.parseInt(request.getParameter("rednum"));
			  max_iter_num = Integer.parseInt(request.getParameter("iternum"));
			  Map<String, Boolean> map = new Hashtable<String, Boolean>();
			  // go through statusMap to see which one is active
			  Iterator<Entry<String, Status>> iterator = statusMap.entrySet().iterator();
			  while(iterator.hasNext()) {
				  Entry<String, Status> entry = iterator.next();
				  String ip = entry.getKey();
				  Status status = entry.getValue();
				  if(status.getStatus().equalsIgnoreCase(IDLE)) {
					  map.put(ip, false);
					  status.setStatus(MAPPING);
					  LogClass.info("Worker(IDLE) in map: " + ip);
				  }
			  }
			  // Remember the job(for reducer), send run map
			  jobStatus = new JobStatus(inputDir, mapNum, redNum, map, 1);
			  System.out.println("Post Run Map");
			  // Post Run Map
			  postRunMap();
			  //postRunReduce();
			  LogClass.info("Master call Worker /runmap" + map.size() + ";");
			  // go back to status
			  response.sendRedirect("status");
		  } catch(Exception e) {
			  request.setAttribute("err", "Invalid Parameter");
			  RequestDispatcher rd = request.getRequestDispatcher("error");
			  try {
				rd.forward(request, response);
			} catch (ServletException e1) {
				e1.printStackTrace();
			}
		  } finally {
		  }
	  else {
		  response.sendRedirect("status");
	  }
  }

  private void showStatus(HttpServletRequest request,
		HttpServletResponse response) {
	try {
		// aquire the lock to show worker status, workerMap
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        // 1 Output worker status table
		out.println("<HTML><HEAD><TITLE>Status Servlet</TITLE></HEAD><style>" +
				"table,th,td{border:1px solid black;border-collapse:collapse; padding:10px;}</style><BODY>");
		// Output Table
		out.println("<table><tr bgcolor=\"#9acd32\"><th>IP:Port</th><th>Status</th>" +
				"<th>count</th><th>KeysRead</th><th>KeysWritten</th><th>Update Time</tr>");
		LogClass.info("Show status; Status Map size: " + statusMap.size());
		// get status map
		Iterator<Entry<String, Status>> iterator = statusMap.entrySet().iterator();
		while(iterator.hasNext()) { 
			Entry<String, Status> entry = iterator.next();
			String ip = entry.getKey();
			Status status = entry.getValue();
			long updateTime = status.getTime();
			if(true) {//currentTime - updateTime <= EPOCH) {
				out.println("<tr><td>" + ip + "</td><td>" + status.getStatus() + "</td><td>" +
						 status.getCount() + "</td><td>" + status.getKeysRead() + "</td><td>" +
						 status.getKeysWritten() + "</td><td>"  + 
						 new Date(updateTime) + "</td></tr>");
			}
			//it is not active
		}
		out.println("</table>");
		// 2. Output form to submit job
		out.println("<hr/><form method=\"POST\" action=\"runwork\">");
		out.println("Input Directory:<input style=\"width: 500px;\" type=\"text\" name=\"inputdir\"></br></br>");
		out.println("Number Threads For Mapper:<input style=\"width: 500px;\" type=\"text\" name=\"mapnum\"></br></br>");
		out.println("Number Threads For Reducer:<input style=\"width: 500px;\" type=\"text\" name=\"rednum\"></br></br>");
		out.println("Max Iteration Number:<input style=\"width: 500px;\" type=\"text\" name=\"iternum\"></br></br>");
		out.println("<input type=\"submit\" name=\"submit\" value=\"submit\">");
		
	} catch(Exception e) {
		e.printStackTrace();
	} finally {
	}
  }
  

  private void processWorkerStatus(HttpServletRequest request) {
	  try {
		  // aquire the lock, use workerMap and statusMap
		  // 1. get parameter (ip, status, count, keysRead, keysWritten)
		  String port = request.getParameter("port");
		  // get the client ip
		  String ip = request.getRemoteAddr() + ":" + port;
		  String status = request.getParameter("status");
		  LogClass.info("Worker: " + ip + "; Status: " + status + "; equals waiting1: " + status.equalsIgnoreCase(WAITING1));
		  long count = Long.parseLong(request.getParameter("count"));
		  long keysRead;
		  long keysWritten = Long.parseLong(request.getParameter("keysWritten"));
		  // Depend on worker is idle or not
		  if(status.equalsIgnoreCase(IDLE)) {
			  keysRead = 0;
		  }
		  else {
			  keysRead = Long.parseLong(request.getParameter("keysRead"));
			  // get the jobstatus by the job key(not idle, we need to process)
			  
		  }
		  // 2. Updata status Map
		  long time = System.currentTimeMillis();
		  Status value = new Status(status, count, keysRead, keysWritten, time);
		  if(statusMap == null) {
			  statusMap = new Hashtable<String, Status>();
		  }
		  statusMap.put(ip, value);
		  LogClass.info("Status Map size: " + statusMap.size());
		  
		  
		  // 3. update jobStatus
		  if(jobStatus != null && jobStatus.getStatus() == 0 && status.equalsIgnoreCase(WAITING)) {
			  // get workers map
			  Map<String, Boolean> map = jobStatus.getWorkers();
			  if(map != null && map.containsKey(ip)) {
				  boolean flag = map.get(ip);
				  // we have one worker finishes
				  if(flag == false)  {
					  map.put(ip, true);
					  int leftNum = jobStatus.reduceNumWorkers();
					  LogClass.info("LeftWorkers to finish map: " + leftNum);
					  if(leftNum == 0) {
						  resetJobStatus();
						  postRunPush();
						  LogClass.info("Master call Worker /runpush");
					  }
				  }
			  }
		  }
		  else if(jobStatus != null && jobStatus.getStatus() == 1 && status.equalsIgnoreCase(WAITING1)) {
			  LogClass.info("Master process waiting1");
			  Map<String, Boolean> map = jobStatus.getWorkers();
			  if(map != null && map.containsKey(ip)) {
				  boolean flag = map.get(ip);
				  // we have one worker finishes
				  if(flag == false)  {
					  map.put(ip, true);
					  int leftNum = jobStatus.reduceNumWorkers();
					  LogClass.info("Waiting1 LeftWorkers to finish map: " + leftNum + "; " + (leftNum == 0));
					  if(leftNum == 0) {
						  resetJobStatus();
						  postRunReduce();
						  LogClass.info("Master call Worker /runreduce");
					  }
				  }
			  }
		  }
		  else if(jobStatus != null && jobStatus.getStatus() == 2 && status.equalsIgnoreCase(IDLE)) {
			  LogClass.info("Master process idle");
			  Map<String, Boolean> map = jobStatus.getWorkers();
			  if(map != null && map.containsKey(ip)) {
				  boolean flag = map.get(ip);
				  // we have one worker finishes
				  if(flag == false)  {
					  map.put(ip, true);
					  int leftNum = jobStatus.reduceNumWorkers();
					  LogClass.info("Waiting1 LeftWorkers to finish map: " + leftNum + "; " + (leftNum == 0));
					  if(leftNum == 0) {
						  resetJobStatus();
						  jobStatus.incrementIterativeNum();
						  if(jobStatus.getIterativeNum() <= max_iter_num)
							  postRunMap();
						  LogClass.info("Master call Worker /runreduce");
					  }
				  }
			  }
		  }
	  } catch (Exception e) {
		  e.printStackTrace();
	  } finally {
	  }
  }
  
  private void resetJobStatus() {
	  Map<String, Boolean> map = new Hashtable<String, Boolean>();
	  for(String ip_port : jobStatus.getWorkers().keySet()) {
		  map.put(ip_port, false);
	  }
	  jobStatus.setWorkers(map);
	  jobStatus.setNumWorkers(map.size());
	  jobStatus.increaseStatus();
  }
  
  private void postRunReduce() {
	  MasterClient client = new MasterClient();
	  try {
		client.sendReducePost(jobStatus);
	  } catch (ClientProtocolException e) {
		e.printStackTrace();
	  } catch (IOException e) {
		e.printStackTrace();
	  }
  }
  
  private void postRunPush() {
	  MasterClient client = new MasterClient();
	  try {
		client.sendPushPost(jobStatus);
	  } catch (ClientProtocolException e) {
		e.printStackTrace();
	  } catch (IOException e) {
		e.printStackTrace();
	  }
  }
  
  private void postRunMap() {
		MasterClient client = new MasterClient();
		try {
		  client.sendMapPost(jobStatus);
		} catch (ClientProtocolException e) {
		  e.printStackTrace();
		} catch (IOException e) {
		  e.printStackTrace();
		}
  }
  
  public void sendTerminate() {
	  MasterClient client = new MasterClient();
	  try {
		  client.sendTerminate(jobStatus);
		} catch (ClientProtocolException e) {
		  e.printStackTrace();
		} catch (IOException e) {
		  e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
  }
 
}
  
