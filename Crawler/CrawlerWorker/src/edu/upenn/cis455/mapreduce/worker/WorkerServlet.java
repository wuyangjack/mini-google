package edu.upenn.cis455.mapreduce.worker;

import hash.SHA1Partition;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import crawler.WebCrawler;

public class WorkerServlet extends HttpServlet {

	  static final long serialVersionUID = 455555002;
	  private static final int MAXQUEUE = 1000;
	  
	  // 1. Initial Paramter
	  private boolean start = false;
	  private ServletConfig config;
	  private WorkerInfo worker_info;
	  //private Timer timer = new Timer();
	  //private PeriodicSender p_sender;
	  private PeriodicSender1 p_sender;
	  // 2. store runmap, writer in context
	  private MapperStatus map_status;
	  private MapProcessor map_processor;
	  // 3. push data
	  private PushdataProcessor push_processor;
	  private AtomicInteger work_num = new AtomicInteger(0);
	  private AtomicInteger max_work_num = new AtomicInteger(Integer.MAX_VALUE);
	  // 4. store runreduce
	  private WebCrawler webCrawler;
	 
	  
	  public void init(ServletConfig config) {
	  	this.config = config;
	  	LogClass.init();
		try {
			LogClass.info("start up");
		  	// get the config init parameter
		    String storage = config.getInitParameter("storagedir");
		    String berkeleydb = config.getInitParameter("berkeleydir");
		    // 1. initial webcrawler
		    webCrawler = new WebCrawler(berkeleydb);
		    String master = config.getInitParameter("master");
		    // 1. Initial the value
		    // get its own port
		    int port = Integer.parseInt(config.getInitParameter("port"));
		    worker_info = new WorkerInfo(storage, master, port,
		    		Info.IDLE, 0, 0, 0);
		    // 2. Periodic sends the the master
		    p_sender = new PeriodicSender1(worker_info, webCrawler);
		    //timer.scheduleAtFixedRate(p_sender, 0, 10*1000);
		    Thread t = new Thread(p_sender);
		    t.start();
		    // initialized
		    start = true;
		} catch(Exception e) {
			LogClass.info("Load Error");
		}
	  }
	  
	  public void destroy() {
		  start = false;  
		  map_status = null;
	  }
	  
	  public void doGet(HttpServletRequest request, HttpServletResponse response) 
	       throws java.io.IOException {
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter(); 
	    try {
	    	String servletPath = request.getServletPath();
			LogClass.info("Servlet Path: " + servletPath + "; " + servletPath.equalsIgnoreCase("/runcrawler"));
			if(servletPath.equals("/runcrawler")) {
				out.println("<html><head><title>Worker</title></head>");
			    out.println("<body><p>run crawler</p></body></html>");
			    out.flush();
			    response.flushBuffer();
				//runWebCrawler();
			}
	    	// first time to create this worker
	    	if(! start) {
	    		// get the config init parameter
			    String storage = config.getInitParameter("storagedir");
			    String master = config.getInitParameter("master");
			    // 1. Initial the value
			    // get its own port
			    int port = Integer.parseInt(config.getInitParameter("port"));
			    worker_info = new WorkerInfo(storage, master, port,
			    		Info.IDLE, 0, 0, 0);
			    // 2. Periodic sends the the master
			    p_sender = new PeriodicSender1(worker_info, webCrawler);
			    //timer.scheduleAtFixedRate(p_sender, 0, 10*1000);
			    Thread t = new Thread(p_sender);
			    t.start();
			    // initialized
			    start = true;
			    // 3. return success page
			    out.println("<html><head><title>Worker</title></head>");
			    out.println("<body><p>Hi, I am the worker!</p>" +
			    		"<p>Initialize, every 10 seconds send message to " + master + "</p></body></html>");
			    out.flush();
			    response.flushBuffer();
	    	} 
	    	else {
	    		out.println("<html><head><title>Worker</title></head>");
			    out.println("<body><p>Hi, I am the worker!</p>" +
			    		"<p>Already Initialize, every 10 seconds send message to " + worker_info.getMasterIpPort() + "</p>");
			    out.println("<p> Status: " + worker_info.getStatus() + "</p>");
			    out.println("<p> keysRead: " + worker_info.getKeysRead() + "</p>");
			    out.println("<p> keysWritten: " + worker_info.getKeysWritten() + "</p>");
			    out.println("<p> Count: " + webCrawler.getCount() + "</p>");
			    out.println("</body></html>");
			    out.flush();
			    response.flushBuffer();
	    	}
	    } catch(Exception e) {
	    	// 4. return error page
	    	out.println("<html><head><title>Worker</title></head>");
		    out.println("<body><p>Hi, I am the worker!</p>" +
		    		"<p>Error</p></body></html>");
		    out.flush();
		    response.flushBuffer();
	    }
	 }
	  
	  
	  public void doPost(HttpServletRequest request, HttpServletResponse response) 
		       throws java.io.IOException {
		  // Get the servletPath
		  String servletPath = request.getServletPath();
		  // every time send back ok
		  sendBackOk(response);
		  LogClass.info("Servlet Path: " + servletPath + "; ");
		  try {
			  //lock.lock();
			  if(start && servletPath.equalsIgnoreCase(Info.RUNMAP)) {
				  worker_info.setStatus(Info.MAPPING);
				  processRunmap(request, response);
				  //p_sender.sendNow();
				  // first receive pushdata, then runmap
				  //if(work_num.get() == max_work_num.get()) {
					 //worker_info.setStatus(Info.WAITING);
				  //}
			  }
			  else if(start && servletPath.equalsIgnoreCase(Info.RUNREDUCE)) {
				  worker_info.setStatus(Info.REDUCING);
				  processRunreduce(request, response);
				  //p_sender.sendNow();
			  }
			  else if(start && servletPath.equalsIgnoreCase(Info.RUNPUSH)) {
				  worker_info.setStatus(Info.PUSHING);
				  pushData();
			  }
			  else if(start && servletPath.equalsIgnoreCase(Info.PUSHDATA)) {
				  // 1. init the process push data instance
				  String folder = worker_info.getStorage() + map_status.getInputDirectory() + 
						  map_status.getIteration();
				  LogClass.info("Reduce inpu folder: " + folder);
				  push_processor = PushdataProcessor.instanceOf(folder);
				  // 2. process
				  push_processor.processPushdata(request);
				  // 3. when receive all message send from workers
				  if(work_num.incrementAndGet() == max_work_num.get()) {
					  worker_info.setStatus(Info.WAITING1);
					  push_processor.close();
					  //p_sender.sendNow();
				  }
				  LogClass.info("Receive work_num: " + work_num.get());
			  } 
			  else if(start && servletPath.equalsIgnoreCase(Info.TERMINATE)) {
				  // stop
				  webCrawler.setFinishFlag();
			  }
		  } catch(Exception e) {
			  
		  } finally {
			  //lock.unlock();
		  }
	  }

	  private void processRunmap(HttpServletRequest request,
				HttpServletResponse response) throws IOException {
		  LogClass.info("Worker Start Run Map");
		  // 1. init status to mapping
		  String inputDir = request.getParameter("inputdir");
		  int iteration = Integer.parseInt(request.getParameter("iteration"));
		  int numThreads = Integer.parseInt(request.getParameter("numThreads"));
		  int numWorkers = Integer.parseInt(request.getParameter("numWorkers"));
		  SHA1Partition.setRange(numWorkers);
		  LogClass.info("Run Map param: " + inputDir + "; " + iteration + "; " + numThreads + "; " + numWorkers);
		  Map<String, String> worker_map = new HashMap<String, String>();
		  for(int i = 0; i < numWorkers; i++) {
			  String name = "worker" + i;
			  worker_map.put(name, request.getParameter(name));
			  LogClass.info("Worker name: " + name + ";" + request.getParameter(name));
		  }
		  map_status = new MapperStatus(inputDir, iteration, numThreads, numWorkers, worker_map);
		  // 2. set the worker_num to 0 and max_worker_num to the total worker (to identify when to finish map)
		  work_num = new AtomicInteger(0);
		  max_work_num = new AtomicInteger(numWorkers);
		  LogClass.info("Worker Number is: " + max_work_num.get());
		  // 3. run map processor
		  map_processor = new MapProcessor(worker_info, map_status);
		  map_processor.process();
	  }
	  
	  private void pushData() throws IOException {
		  String map_output = worker_info.getStorage() + map_status.getInputDirectory() + 
				  map_status.getIteration() + Info.MAPOUTPUT;
		  Map<String, String> workerMap = map_status.getWorkers();
		  LogClass.info("Begin Push data; Worker Size: " + workerMap.size());
		  for(String worker_name : workerMap.keySet() ) {
			  LogClass.info("Push data to: " + worker_name + "; Ip: " + workerMap.get(worker_name));
			  CloseableHttpClient httpclient = HttpClients.createDefault();
			  String url = "http://" + workerMap.get(worker_name) + Info.WPUSHDATA;
			  HttpPost post = new HttpPost(url);
			  File file = new File(map_output + "/" + worker_name);
			  FileEntity entity = new FileEntity(file, ContentType.DEFAULT_TEXT);
			  post.setEntity(entity);
			  httpclient.execute(post);
			  httpclient.close();
		  }
	  }

	  private void processRunreduce(HttpServletRequest request,
			HttpServletResponse response) throws IOException, InterruptedException {
		  LogClass.info("Begin Reduce");
		  runWebCrawler();
		  worker_info.setStatus(Info.IDLE);
	  }
	  
	  public void runWebCrawler() throws IOException, InterruptedException {
		  String inputDir = map_status.getInputDirectory();
		  int iter = map_status.getIteration() + 1;
		  // 1. create dir level1
		  String outputDir = worker_info.getStorage() + inputDir + iter;
		  File out_dir = new File(outputDir);
		  if(out_dir.exists()) {
			  deleteFolder(out_dir);
		  }
		  out_dir.mkdir();
		  String folder = worker_info.getStorage() + inputDir + 
				  map_status.getIteration() + Info.REDUCEINPUTFILE;
		  LogClass.info("Crawler Read folder: " + folder);
		  LogClass.info("Crawler Write folder: " + outputDir);
		  // 2. init crawler
		  SHA1Partition.setSection(webCrawler.getThreadNum());
		  webCrawler.initWriter(worker_info.getStorage() + inputDir + map_status.getIteration());
		  webCrawler.init(outputDir + Info.INPUTFILENAME);
		  BufferedReader url_reader = new BufferedReader(new FileReader(folder));
		  int read_count = 0;
		  String line = null;
		  while( (line = url_reader.readLine()) != null) {
				String[] args = line.split("\t", 2);
				LogClass.info("Read Url: " + args[1]);
				if(args.length == 2) {
					webCrawler.initQueue(args[0], args[1]);
					read_count++;
				}
				// 2. if we got MAXQUEUE, run crawler
				if(read_count == MAXQUEUE) {
					webCrawler.run();
					read_count = 0;
					// need to reinit the queue and writer
					webCrawler.initWriter(worker_info.getStorage());
					webCrawler.init(outputDir + Info.INPUTFILENAME);
				}
		  }
		  if(read_count != 0) {
			 webCrawler.run();
			 read_count = 0;
	      }
	  }
	  
	  private void deleteFolder(File file) {
		  if(file.isDirectory()){
	    		if(file.list().length==0){
	    		   file.delete();
	    		}
			    else{
			        File[] filess = file.listFiles();
			        for (File temp : filess) {
			        	 deleteFolder(temp);
			        }
			        if(file.list().length==0){
			           	 file.delete();
			        }
			     }
		    }
		   else{
	    		file.delete();
	    	}
	  }
	  
	  private void sendBackOk(HttpServletResponse response) throws IOException {
		  response.setContentType("text/plain");
		  PrintWriter pw = response.getWriter();
		  pw.println("receive");
		  pw.flush();
		  response.flushBuffer();
	  }
	  
	  
}
  
