import general.UrlNormalizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class test { 
	static String CRLF = "\r\n";

	public static void testPost() throws ClientProtocolException, IOException {
		for(int i = 0; i < 2; i++) {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost post = new HttpPost("http://localhost:8080/MVC/busy");
			/*
			BufferedReader brr = new BufferedReader(new FileReader("test"));
			String result = "";
			String tmp = null;
			while((tmp = brr.readLine()) != null) {
				result += tmp;
				System.out.println(result.length());
			}
			brr.close();
			System.out.println(result.length());
			StringEntity entity = new StringEntity(result, ContentType.DEFAULT_TEXT);
			*/
			
			File file = new File("test");
			//FileBody bin = new FileBody(file);
			FileEntity entity = new FileEntity(file, ContentType.DEFAULT_TEXT);
			/*
			HttpEntity reqEntity = MultipartEntityBuilder.create()
	                .addPart("bin", bin)
	                .build();
			/*
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("message", "value1"));
			formparams.add(new BasicNameValuePair("param2", "value2"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams);
			*/
			post.setEntity(entity);
			CloseableHttpResponse response = httpclient.execute(post);
			HttpEntity e = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(e.getContent()));
			String s = null;
			while((s= br.readLine()) != null)
				System.out.println("Content: " + s);
			httpclient.close();
		}
	}
	
	public static void test3() throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		String url="http://localhost:8080/MVC/busy";
		HttpPost httppost=new HttpPost(url);
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("message","2544"));
		httppost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
		HttpResponse response= HttpClients.createDefault().execute(httppost);

                //Header header = response.getFirstHeader("Content-Length");
		//String Length=header.getValue();
		if(response.getStatusLine().getStatusCode()==200){
			String result=EntityUtils.toString(response.getEntity());
			System.out.println(result);
		}
	}
	
	public static void testPost1() throws UnknownHostException, IOException {
		Socket s = new Socket("127.0.0.1", 8080);
		String message = "abde";
	    PrintStream ps = new PrintStream(s.getOutputStream());
	    String header = "POST /MVC/busy HTTP/1.1" + CRLF + "Host: 127.0.0.1" + CRLF
						+ "Content-Length: " + message.length()+ CRLF + CRLF;
	      // write to worker
	    ps.print(header);
	    ps.print(message);
	    ps.flush();
		s.close();
	}
	
	
	public static void test1() throws MalformedURLException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("/Users/ChenyangYu/Documents/Upenn/storage/level1/test"));
		PrintStream ps = new PrintStream(new FileOutputStream("/Users/ChenyangYu/Documents/Upenn/storage/level1/file"));
		String test = null;
		while((test = br.readLine()) != null) {
			String out = UrlNormalizer.getHostName(test) + "\t" + test;
			ps.println(out);
		}
		ps.flush();
		br.close();
		ps.close();
	}
	
	public static void deleteFolder(File file) {
		  if(file.isDirectory()){
	    		if(file.list().length==0){
	    		   file.delete();
	    		}
			    else{
			        File[] filess = file.listFiles();
			        for (File temp : filess) {
			        	if(file.getName().equals("level1") && temp.getName().equals("urlfile"));
			        	else
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
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		BasicConfigurator.configure();
		Logger.getLogger("org.apache.http").setLevel(org.apache.log4j.Level.OFF);
		//testPost();
		
		/*
		System.out.println(new File(".").getCanonicalPath());
		File f = new File("data");
		if(! f.exists()) {
			System.out.println("make directory");
			f.mkdir();
		}
		*/
		/*
		PrintStream ps = new PrintStream(new FileOutputStream("test"));
		for(int i = 0; i < 200000; i++) {
			ps.println("urlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurlurl");
		}
		*/
		//test1();
		deleteFolder(new File("/Users/ChenyangYu/Documents/Upenn/storage1"));
		File f = new File("/Users/ChenyangYu/Documents/Upenn/storage1/berkeleydb");
		f.mkdir();
	}

}
