package general;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {

	public static BigInteger encodeInt(String key) {
		String result = encodeString(key);
		BigInteger b_int = new BigInteger(result, 16);
		return b_int;
	}
	
	// use sha1 to encode the input
	public static String encodeString(String args) {
		try {
			// String replace all \s (space or ...)
			String input = args.replaceAll("\\s", "");
			MessageDigest mDigest = MessageDigest.getInstance("SHA1");
			byte[] result = mDigest.digest(input.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < result.length; i++) {
	            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}
