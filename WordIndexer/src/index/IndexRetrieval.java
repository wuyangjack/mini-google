package index;

import hash.SHA1Partition;

import java.util.ArrayList;

import storage.BerkeleyDB;
import storage.WordDocTFEntry;

public class IndexRetrieval {

	public static ArrayList<WordDocTFEntry> retrieve(String word){
		int workerIndex = SHA1Partition.getWorkerIndex(word);
		//TODO connect other workers according to the worker index, through servlets
		BerkeleyDB db = IndexDriver.db;
		return db.getTFIDF(word);
	}

}
