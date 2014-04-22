package crawldatatype;

public class HeadNode {
	//state 0: ok; state 1: not modified; state 2: others
	private int state;
	// content-type is html or not
	private boolean type;
	// content-length
	private int length;
	// query location
	private String location;
	// Absolute Url
	private String absolute_url;
	// encoded url
	private String encoded_url;
	
	public HeadNode(int f1, boolean f2, int i, String l) {
		state = f1;
		type = f2;
		length = i;
		location = l;
	}
	
	public int getState() {
		return state;
	}
	
	public boolean typeValid() {
		return type;
	}
	
	public int getLength() {
		return length;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setAbsoluteUrl(String url) {
		System.out.println(url);
		this.absolute_url = url; 
	}
	
	public String getAbsoluteUrl() {
		return absolute_url;
	}
	
	public void setEncodeUrl(String url) { 
		this.encoded_url = url; 
	}
	
	public String getEncodeUrl() {
		return encoded_url;
	}
}
