package amazonAPI;

public class Item {

	private String title = "";
	private String img = "";
	private String price = "";
	private String url = "";
	
	public Item(String title, String img, String price, String url){
		this.title = title;
		this.img = img;
		this.price = price;
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
