package storage;

import java.util.Set;

public class RobotText {

	private int delay;
	private String crawl_time;
	private Set<String> disallow;
	
	public RobotText(int delay, String crawl_time, Set<String> disallow) {
		this.delay = delay;
		this.disallow = disallow;
		this.crawl_time = crawl_time;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public void setTime(String time) {
		this.crawl_time = time;
	}
	
	public void setDisallow(Set<String> disallow) {
		this.disallow = disallow;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public String getTime() {
		return crawl_time;
	}
	
	public Set<String> getDisallow() {
		return disallow;
	}
	
	public void print(String url) {
		System.out.println("Robot URL: " + url + "; Delay: " + delay);
		for(String s: disallow)
			System.out.print(s + ", ");
		System.out.println();
		
	}
	
}
