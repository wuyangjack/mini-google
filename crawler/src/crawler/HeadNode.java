package crawler;

public class HeadNode {
	//state 0: ok; state 1: not modified; state 2: others
	private int state;
	// content-type is html or not
	private boolean type;
	// content-length
	private int length;
	// redirect location
	private String location;
	
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
}
