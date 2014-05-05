package cis455.project.storage;

public class test {

	public static void main(String[] args) {
		String dir = "/home/cis455/database";
		Storage.setEnvPath(dir, true);
		Storage storage = Storage.getInstance();
		String[] result = storage.get(StorageGlobal.tablePageRank, "http://www.upenn.edu/");
		System.out.println(result.length);
		System.out.println(result[0]);
		result = storage.get(StorageGlobal.tablePageRank, "http://www.google.com/");
		System.out.println(result.length);
	}

}
