package edu.upenn.cis455.mapreduce.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import edu.upenn.cis455.mapreduce.job.MapContext;
import edu.upenn.cis455.mapreduce.job.UrlJob;


public class MapProcessor {
	
	// 1. worker_info store self field, mapper_status store map status
	private WorkerInfo worker_info;
	private MapperStatus map_status;
	// 2. Job to do the map
	private UrlJob job;
	private MapContext context;
	private BufferedReader inputReader;
	private File inputFile;
	private String map_output;
	
	
	public MapProcessor(WorkerInfo worker_info, MapperStatus map_status) {
		this.worker_info = worker_info;
		this.map_status = map_status;
		this.job = new UrlJob();
		map_output = worker_info.getStorage() + map_status.getInputDirectory() + 
				  map_status.getIteration() + Info.MAPOUTPUT;
	}

	public void process() throws FileNotFoundException {
		  try {
			  // 1. Open the input directory
			  LogClass.info("1. Open Input directory");
			  // storage/level1/mapinput/urlfile
			  String map_input = worker_info.getStorage() + map_status.getInputDirectory() + 
					  map_status.getIteration() + Info.INPUTFILENAME;
			  inputFile = new File(map_input);
			  LogClass.info("Input: " + inputFile.getAbsolutePath());
			  inputReader = new BufferedReader(new FileReader(inputFile));
			  // 2. Create mapout folder and worker files
			  File local_dir = new File(map_output);
			  if(local_dir.exists())
				  deleteFolder(local_dir);
			  local_dir.mkdir();
			  for(int i = 0; i < map_status.getNumWorkers(); i++) {
				  File f = new File(map_output + Info.MAPOUTFILENAME + i);
				  LogClass.info("Map create file: " + map_output + Info.MAPOUTFILENAME + i);
				  f.createNewFile();
			  }
			  // 3. Create multiple thread
			  LogClass.info("3. Do Run Map");
			  context = new MapContext(worker_info, map_status);
			  ExecutorService service = Executors.newFixedThreadPool(map_status.getNumMappers());
			  // initial the thread
			  for(int i = 0; i < map_status.getNumMappers(); i++)
				  service.execute(new MapThread());
			  service.shutdown();
			  while(! service.awaitTermination(10, TimeUnit.SECONDS));
			  worker_info.setStatus(Info.WAITING);
			  LogClass.info("Finish Runmap; Status: " + worker_info.getStatus());
			  //pushData();
		  } catch(Exception e) {
			  LogClass.info("Exception");
			  e.printStackTrace(new PrintStream(new FileOutputStream("errorlog")));
			  worker_info.setStatus(Info.IDLE);
		  } finally {
			  //if(service != null)
				  //service.shutdown();
		  }  	
	 }
  
	  private class MapThread implements Runnable{
			
			public void run() {
				String pair;
				while((pair = readNext()) != null) {
					// increase one keysRead
					worker_info.incrementKeysRead();
					String[] args = pair.split("\t");
					job.map(args[0], args[1], context);
					worker_info.incrementKeysWritten();
				}
			}
	  }
  
  public synchronized String readNext() {
	  try {
		String result = inputReader.readLine();
		if(result == null)
			return null;
		// we read to end of a file
		while(result.equals("")) {
			result = inputReader.readLine();
		}
		LogClass.info("Read Map: " + result);
			return result;
	    } catch (IOException e) {
			e.printStackTrace();
			return null;
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
}
