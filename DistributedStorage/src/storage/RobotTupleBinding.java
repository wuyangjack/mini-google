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
		long delay = ti.readLong();
		String time = ti.readString();
		int i = ti.readInt();
		Set<String> disallow = new HashSet<String>(i);
		while(i > 0) {
			disallow.add(ti.readString());
			i--;
		}
		i = ti.readInt();
		Set<String> allow = new HashSet<String>(i);
		while(i > 0) {
			allow.add(ti.readString());
			i--;
		}
		rt = new RobotText(delay, time, disallow, allow);
		return rt;
	}

	@Override
	public void objectToEntry(RobotText rt, TupleOutput to) {
		to.writeLong(rt.getDelay());
		to.writeString(rt.getTime());
		Set<String> set = rt.getDisallow();
		to.writeInt(set.size());
		for(String s : set) 
			to.writeString(s);
		set = rt.getAllow();
		to.writeInt(set.size());
		for(String s : set) 
			to.writeString(s);
	}

	
}
