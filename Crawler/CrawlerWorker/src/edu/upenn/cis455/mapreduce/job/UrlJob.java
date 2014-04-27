package edu.upenn.cis455.mapreduce.job;

import edu.upenn.cis455.mapreduce.Context;
import edu.upenn.cis455.mapreduce.Job;

public class UrlJob implements Job{

	  /**
	   * key: hostname		value: url
	   */
	  public void map(String key, String value, Context context)
	  {
		  context.write(key, value);
	  }
	  
	  /**
	   * key: hostname 	values: url
	   */
	  public void reduce(String key, String[] values, Context context)
	  {
		  for(String value : values)
			  context.write(key, value);	  
	  }
	  
}
