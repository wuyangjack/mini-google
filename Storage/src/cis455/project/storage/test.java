package cis455.project.storage;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dir = "/home/cloudera/Database";
		Storage.setEnvPath(dir);
		Storage storage = Storage.getInstance();
		System.out.println(storage.get("title", "zentai").size());
		System.out.println(storage.get("title", "zentai"));
	}

}
