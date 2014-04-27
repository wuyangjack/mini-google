package storage;

import java.util.Set;

public class RobotText {

	private long delay;
	private String crawl_time;
	private Set<String> disallow;
	private Set<String> allow;
	
	public RobotText(long delay, String crawl_time, Set<String> disallow, Set<String> allow) {
		this.delay = delay;
		this.disallow = disallow;
		this.crawl_time = crawl_time;
		this.allow = allow;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public void setTime(String time) {
		this.crawl_time = time;
	}
	
	public void setDisallow(Set<String> disallow) {
		this.disallow = disallow;
	}
	
	public void setAllow(Set<String> allow) {
		this.allow = allow;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public String getTime() {
		return crawl_time;
	}
	
	public Set<String> getDisallow() {
		return disallow;
	}
	
	public Set<String> getAllow() {
		return allow;
	}
	
	public void print(String url) {
		System.out.println("Robot URL: " + url + "; Delay: " + delay);
		for(String s: disallow)
			System.out.print(s + ", ");
		System.out.println();
		
	}
	
}
