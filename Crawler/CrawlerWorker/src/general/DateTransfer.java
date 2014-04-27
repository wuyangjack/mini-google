package general;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTransfer {

	private static final String p = "yyyy-MM-dd HH:mm:ss";
	private static final String p1 = "EEE, dd MMM yyyy HH:mm:ss z";
	
	/**
	 * Transfer Date to String pattern 1999-10-12T10:12:12
	 * @param date
	 * @return
	 */
	public static String transferDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(p);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date).replace(" ", "T");
	}
	
	/**
	 * Transfer String pattern 1999-10-12T10:12:12 to Date
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date transferString(String date) {
		date = date.replace("T", " ");
		SimpleDateFormat dateFormat = new SimpleDateFormat(p);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Transfer the Database store pattern string 2014-03-22T09:03:54 to normalize pattern EEE, dd MMM yyyy HH:mm:ss z;
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getDate(String date) {
		SimpleDateFormat dateFormat;
		dateFormat = new SimpleDateFormat(p1); 
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date d = transferString(date);
		return d != null ? dateFormat.format(d) : "";
	}
	
	
	public static void main(String[] args) throws ParseException {
		//String date = "2014-03-22T09:03:54";
		//System.out.println(getDate(date));
		String time = transferDate(new Date());
		System.out.println(time);
		System.out.println(transferString(time));
		String date = getDate(time);
		System.out.println(date);
	}
	
	
	
}
