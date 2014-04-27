package edu.upenn.cis455.mapreduce.worker;

public class Info {

	  public static final String WAITING = "waiting";
	  public static final String WAITING1 = "waiting1";
	  public static final String IDLE = "idle";
	  public static final String MAPPING = "mapping";
	  public static final String PUSHING = "pushing";
	  public static final String REDUCING = "reducing";
	  public static final String CRLF = "\r\n";
	  // url
	  public static final String WORKERSTATUS = "/CrawlerMaster/workerstatus";
	  public static final String WPUSHDATA = "/CrawlerWorker/pushdata";
	  public static final String RUNMAP = "/runmap";
	  public static final String RUNREDUCE = "/runreduce";
	  public static final String PUSHDATA = "/pushdata";
	  public static final String RUNPUSH = "/runpush";
	  public static final String TERMINATE = "/terminate";
	  //public static final String RUNPUSH
	  // local storage
	  public static final String MAPOUTPUT = "/mapoutput";
	  public static final String REDUCEINPUTFILE = "/reduceinput";
	  public static final String MAPOUTFILENAME = "/worker";
	  public static final String INPUTFILENAME = "/urlfile";
}
