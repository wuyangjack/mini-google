package youtubeAPI;

public class YoutubeItem {

	public int item_Num = 0;
	public String[] url = new String[3];
	public String[] embed_url = new String[3];
    public String[] title = new String[3];
    public String[] img = new String[3];
    
    public void parse(String whole){
    	if(whole.length() == 0)
    		return;
    	whole = whole.substring(5);
    	String[] split = whole.split("-@@@-");
    	item_Num = split.length / 3;
    	for(int i = 0; i < (split.length / 3); i++){
    		url[i] = "https://www.youtube.com/watch?v=" + split[i*3];
    		embed_url[i] = "http://www.youtube.com/embed/" + split[i*3];
    		title[i] = split[i*3 + 1];
    		img[i] = split[i*3 + 2];
    	}
    }
}
