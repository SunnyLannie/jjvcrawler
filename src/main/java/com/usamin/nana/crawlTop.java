package com.usamin.nana;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.usamin.nana.extractor.CrawlerUniversal;
import com.usamin.nana.extractor.DomainCrawler;

//this implements the top level crawling logic

public class crawlTop extends CrawlerUniversal {
	Set<String> discovered = ConcurrentHashMap.newKeySet(); // thread safe set
	LinkedList<String> toCrawl;

	int iteration = 0;
	private Lock lock;
	private Lock toCrawlLock;

	final String url;
	public crawlTop(String url) {
		this.url = url;
		discovered = new HashSet<String>(); //contains url that already have been crawled or are about to be
		toCrawl = new LinkedList<String>(); //url that will be crawled shortly
		url = trimUrl(url); // remove extra '/'
		discovered.add(url); //populate discovered with first url to be crawled
		lock = new ReentrantLock();
		toCrawlLock = new ReentrantLock();
	}

	public void incrementIteration() {
		lock.lock();
		iteration++;
		lock.unlock();
	}

	public int getIteration() {
		lock.lock();
		int iter = iteration;
		lock.unlock();
		return iter;
	}

	public void executeCrawl(DomainCrawler dc) {
		String next;
		do {
			while (dc.getNextCrawlUrl() == null) {
				next = getNextCrawl();
				if (next == null) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				dc.addURL(next);
			}

			for (String crawl : dc.crawl()) {
				debugPrint("crawl is: " + crawl + " url is: " + url);
				if (areSameDomain(crawl, url)) {
					toCrawlLock.lock();
					toCrawl.addFirst(crawl);
					toCrawlLock.unlock();
				} else {
					toCrawlLock.lock();
					toCrawl.addLast(crawl);
					toCrawlLock.unlock();
				}
			}
			next = getNextCrawl();
			if (next == null) {
				break;
			}
			debugPrint("the value of next is: " + next);
			dc.addURL(next); // add to domain crawler queue to crawl
			discovered.add(next);// make note to other threads that this is
									// going to be crawled
			incrementIteration();
			debugPrint("we found: " + discovered);
		} while (getIteration() < getNumberIterCrawl() && areSameDomain(next, url)); // is
																				// here
																				// to
																				// stop
																				// infinite
																				// crawling
																				// and
																				// to
																				// make
																				// it
																				// crawl
																				// only
																				// 1
																				// domain

		//debugPrint("crawltop exclude crawl: "+excludeCrawl);
		//System.out.println("we found: " + discovered);
		//System.out.println("the number of items we found is: " + discovered.size());
	}
	
	public LinkedHashSet<String> getFoundUrl(){
		toCrawlLock.lock();
		System.out.println(toCrawl);
		LinkedHashSet<String> toCrawlNoDup =  new LinkedHashSet<String>(toCrawl);
		toCrawlLock.unlock();
		return toCrawlNoDup;
	}
	
	public Set<String> getCrawledUrl(){
		return discovered;
	}



	// synchronized to stop different threads from crawling the same url
	private String getNextCrawl() {
		// debugPrint("before "+toCrawl);
		toCrawlLock.lock();
		Iterator<String> iterator = toCrawl.iterator();
		while (iterator.hasNext()) {
			String nextCrawl = iterator.next();
			iterator.remove();
			if (discovered.contains(nextCrawl)) {
			} else {
				debugPrint("after " + toCrawl); // modified exception
				toCrawlLock.unlock();

				return nextCrawl;
			}
		}
		// debugPrint("after "+toCrawl);
		toCrawlLock.unlock();

		return null;
	}
}
