package general;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

public class DelimiterTokenizer {
	
	private StreamTokenizer st;
	
	public DelimiterTokenizer() {
	}
	
	public DelimiterTokenizer(String str) {
		this();
		setTokenizer(str);
	}
	
	public void setTokenizer(String str) {
		st = new StreamTokenizer(new StringReader(str));
		st.resetSyntax();
		st.whitespaceChars('\u0000', '\u0020');
		st.wordChars('a', 'z');
		st.wordChars('A', 'Z');
		st.wordChars('0', '9');
		st.wordChars('_', '_');
	}
	
	/**
	 * 
	 * @return the string of next token
	 * @throws IOException
	 */
	public String nextToken() throws IOException {
		if(st.ttype == StreamTokenizer.TT_WORD ) {
			return st.sval;
		} else if(st.ttype == StreamTokenizer.TT_NUMBER) {
			return String.valueOf(st.nval);
		}
		else {
			return "";
		}
	}
	
	/**
	 * 
	 * @return if the tokenizer have next token
	 * @throws IOException
	 */
	public boolean hasNext() throws IOException{
		st.nextToken();
    	return (st.ttype != StreamTokenizer.TT_EOF && st.ttype != StreamTokenizer.TT_EOL);
    }	
	
}
