package storage;


public class DB_Directory {

	private static DB_Directory itself;
	
	
//	public static String xsl = "http://localhost:8080/cis555.xsl";
	public static String xsl = "http://localhost:8080/HW2/rss/warandpeace.xsl";
	public static String testDB = "/home/cis455/workspace/db/test";
	public static String fileDB = "/home/cis455/workspace/db/file";
	public static String userDB = "/home/cis455/workspace/db/user";
	public static String channelDB = "/home/cis455/workspace/db/channel";
	public static String fixedchannelDB = "/home/cis455/workspace/HW2/berkleydb/fixedchannel";
	
	
	public static DB_Directory getInstance() {
        if (itself == null) {
            itself = new DB_Directory();
        }
        return itself;
    }
	
}
