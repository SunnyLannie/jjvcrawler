package com.usamin.nana;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//This is main
/*** process that downloads and parse webpage for hyperlinks ***/

public class Jjvcrawler {
	final static int SUGGESTED_NUM_THREADS = 2;
	static int NUM_THREADS = SUGGESTED_NUM_THREADS;
	final static int MAX_THREADS = 10;
	//first command line arg is url to start crawling with
    //second command line arg is number of threads
	public static void main(String[] args) throws URISyntaxException {
		System.out.println("ran");

		// obtain number of threads
		if (args.length > 0) {
			try {
				NUM_THREADS = Integer.parseInt(args[0]);
			} catch (Exception e) {
				NUM_THREADS = SUGGESTED_NUM_THREADS;
			}
		}
		// avoid issues where the user wants millions of threads
		if (NUM_THREADS > MAX_THREADS) {
			NUM_THREADS = MAX_THREADS;
		}

		String test = "https://en.wikipedia.org/wiki/Cassiopeia_(constellation)";
		test = "https://disorderlylabs.github.io/";
		String reddit = "https://www.reddit.com/";
		String facebook = "https://www.facebook.com";
		// test="https://www.equestriadaily.com";
		String test2 = "https://www.macrumors.com/";
		// test="https://www.equestriadaily.com";
	//	 test= "https://sites.google.com/site/daviddeyellwatercolor/";
		

		/*** output file name ***/

		String filename = "unwrap.nana";

		crawlTop top = new crawlTop(test); // crawl top is where the actual
											// crawling takes place 
		final long startTime = System.currentTimeMillis();

		// for final refactor look at which files are not actually used
		ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		// submit jobs to be executing by the pool
		final String testHere = test;
		// pass the i into crawlThreading, if it isn't 0. don't crawl testHere
		for (int i = 0; i < NUM_THREADS; i++) {
			Runnable worker = new crawlThreading(top, testHere, i);
			threadPool.execute(worker);// calling execute method of
										// ExecutorService
		}
		// once you've submitted your last job to the service it should be shut
		// down
		threadPool.shutdown();
		// wait for the threads to finish
		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		final long endTime = System.currentTimeMillis();

		System.out.println("Total execution time: " + (endTime - startTime));

	}
	/*
	 * crawlThreading thread= new crawlThreading(top, test); crawlThreading
	 * thread1= new crawlThreading(top, test);
	 * 
	 * thread.start(); thread1.start();
	 * 
	 * 
	 * try{thread.join(); }catch(Exception e){ System.err.println(e); }
	 * 
	 * try{thread1.join(); }catch(Exception e){ System.err.println(e); }
	 */
	// System.out.println(dc.crawl());

	/*
	 * DomainCrawler seedDomain = new DomainCrawler(test); map.put("hi",
	 * seedDomain); LinkedList<String> links = seedDomain.crawl(); Set<String> s
	 * = new LinkedHashSet<>(links);
	 * 
	 * System.out.println(s); seedDomain.addURL(links.get(2));
	 * System.out.println(seedDomain.crawl());
	 */

}