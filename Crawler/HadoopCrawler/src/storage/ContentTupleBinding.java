package storage;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class ContentTupleBinding extends TupleBinding<Content>{

	@Override
	public Content entryToObject(TupleInput ti) {
		Content content;
		int i = ti.readInt();
		Set<String> hash_urls = new HashSet<String>(i);
		while(i > 0) {
			hash_urls.add(ti.readString());
			i--;
		}
		content = new Content(hash_urls);
		return content;
	}

	@Override
	public void objectToEntry(Content rt, TupleOutput to) {
		Set<String> set = rt.getHashUrls();
		to.writeInt(set.size());
		for(String s : set) 
			to.writeString(s);
	}

	
}
