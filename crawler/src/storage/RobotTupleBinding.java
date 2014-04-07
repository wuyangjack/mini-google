package storage;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class RobotTupleBinding extends TupleBinding<RobotText>{

	@Override
	public RobotText entryToObject(TupleInput ti) {
		RobotText rt;
		int delay = ti.readInt();
		String time = ti.readString();
		int i = ti.readInt();
		Set<String> xpaths = new HashSet<String>(i);
		while(i > 0) {
			xpaths.add(ti.readString());
			i--;
		}
		rt = new RobotText(delay, time, xpaths);
		return rt;
	}

	@Override
	public void objectToEntry(RobotText rt, TupleOutput to) {
		to.writeInt(rt.getDelay());
		to.writeString(rt.getTime());
		Set<String> set = rt.getDisallow();
		to.writeInt(set.size());
		for(String s : set) 
			to.writeString(s);
	}

	
}
