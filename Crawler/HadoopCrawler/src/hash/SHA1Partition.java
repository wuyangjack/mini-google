package hash;

import java.math.BigInteger;

public class SHA1Partition {
	

	private static BigInteger max_value = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
	private static BigInteger range;
	private static BigInteger section;
	private static int worker_num;
	private static int thread_num;


	/**
	 * Set the worker range of SHA1
	 * @param worker_num
	 * @return
	 */
	public static void setRange(int worker_num) {
		BigInteger workerNum = new BigInteger(String.valueOf(worker_num));
		range = max_value.divide(workerNum);
		SHA1Partition.worker_num = worker_num;
	}

	/**
	 * Set the thread range of SHA1
	 * @param thread_num
	 * @return
	 */
	public static void setSection(int thread_num) {
		BigInteger threadNum = new BigInteger(String.valueOf(thread_num));
		section = range.divide(threadNum);
		SHA1Partition.thread_num = thread_num;
	}

	/**
	 * Get the right thread number to process the url
	 * @param section	the range of one thread in one worker (example 0 ~ 00fffffffffffffffffffffff), each worker have 16 thread
	 * @param range		the range of one worker (example 0 ~ 0fffffffffffffffffffffffffffffff), have 16 worker
	 * @param encode
	 * @return
	 */
	public static int getThreadIndex(String key) {
		BigInteger encode = SHA1.encodeInt(key);
		// get the section_value < range
		BigInteger section_value = encode.mod(range);
		BigInteger threadNum = section_value.divide(section);
		return threadNum.intValue() >= thread_num ? thread_num - 1 : threadNum.intValue();
	}
	
	/**
	 * Get the right worker number given the worker range, example (0 ~ 2ffffffffffffff) and the url encode
	 * @param range
	 * @param encode
	 * @return
	 */
	public static int getWorkerIndex(String key) {
		BigInteger encode = SHA1.encodeInt(key);
		BigInteger workerNum = encode.divide(range);
		return workerNum.intValue() >= worker_num ? worker_num - 1 : workerNum.intValue();
	}
	
	public static void main(String[] args) {
		System.out.println("MAX_VALUE: " + max_value.toString(16) + "; " + max_value.toString(16).length());
		setRange(8);
		System.out.println("Range: " + range.toString(16) + "; " + range.toString(16).length());
		setSection(8);
		System.out.println("Section: " + section.toString(16) + "; " + section.toString(16).length());
		// test1
		System.out.println("Upenn: " + SHA1.encodeString("www.upenn.edu"));
		int worker_num2 = getWorkerIndex("www.upenn.edu");
		System.out.println("worker_num: " + worker_num2);
		int thread_num2 = getThreadIndex("www.upenn.edu");
		System.out.println("thread_num: " + thread_num2);
		// test2
		System.out.println("Oracle: " + SHA1.encodeString("www.oracle.com"));
		worker_num2 = getWorkerIndex("www.oracle.com");
		System.out.println("worker_num: " + worker_num2);
		thread_num2 = getThreadIndex("www.oracle.com");
		System.out.println("thread_num: " + thread_num2);
		// test3 
		System.out.println("Max Test");
		BigInteger encode = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF6", 16);
		BigInteger workerNum = encode.divide(range);
		System.out.println("Worker_num: " + (workerNum.intValue() >= worker_num ? worker_num - 1 : workerNum.intValue()));
		// get the section_value < range
		BigInteger section_value = encode.mod(range);
		System.out.println("Encode value: " + section_value.toString(16));
		BigInteger thread_num1 = section_value.divide(section);
		System.out.println(thread_num1.intValue() >= thread_num ? thread_num - 1 : thread_num1.intValue());
	}

}
