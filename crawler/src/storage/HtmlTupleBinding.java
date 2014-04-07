package storage;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class HtmlTupleBinding extends TupleBinding<HtmlDocument>{

	@Override
	public HtmlDocument entryToObject(TupleInput ti) {
		HtmlDocument hdoc = new HtmlDocument();
		hdoc.setUrl(ti.readString());
		hdoc.setHtmlData(ti.readString());
		hdoc.setDate(ti.readString());
		return hdoc;
	}

	@Override
	public void objectToEntry(HtmlDocument hdoc, TupleOutput to) {
		to.writeString(hdoc.getUrl());
		to.writeString(hdoc.getHtmlData());
		to.writeString(hdoc.getDate());
	}
	
	
}

