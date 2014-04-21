package storage;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class UrlTupleBinding extends TupleBinding<UrlText>{

	@Override
	public UrlText entryToObject(TupleInput ti) {
		String url = ti.readString();
		boolean visited = ti.readBoolean();
		return new UrlText(url, visited);
	}

	@Override
	public void objectToEntry(UrlText ut, TupleOutput uo) {
		uo.writeString(ut.getUrl());
		uo.writeBoolean(ut.isVisited());
	}
}
