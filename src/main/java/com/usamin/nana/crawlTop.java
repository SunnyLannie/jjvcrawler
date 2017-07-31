package com.usamin.nana;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.usamin.nana.extractor.CrawlerUniversal;
import com.usamin.nana.extractor.DomainCrawler;

//this implements the top level crawling logic
//TODO fifo queue
//TODO 
//TODO add a new class where each domain has it's own class

public class crawlTop extends CrawlerUniversal {
	Set<String> discovered = ConcurrentHashMap.newKeySet(); //thread safe set
//	HashSet<String> discovered;
	//HashSet<String> toCrawl;
	LinkedList<String> toCrawl;
	

	
   long maxIteration=200;
   CrawlMap map;
   int iteration=0;
   private Lock lock;
   private Lock toCrawlLock;

   final String url;
    
	 public crawlTop(String url) {
		 discovered = new HashSet<String>(); //maybe alreadyCrawled and excludeCrawl should be the same set since they serve the same purpose?
		 //but maybe not due to robots txt not being a static page
		 toCrawl = new LinkedList<String>();
			//DomainCrawler dc = new DomainCrawler(url);
			url=trimUrl(url); //remove extra '/'

			discovered.add(url);

			lock = new ReentrantLock();
			toCrawlLock=new ReentrantLock();
            this.url=url;
    }
	 
	 public void incrementIteration(){
			lock.lock();
		    iteration++;
			lock.unlock();
	 }
	 
	 public int getIteration(){
			lock.lock();
			int iter=iteration;
			lock.unlock();
			return iter;
	 }
	 
	 public void executeCrawl(DomainCrawler dc){
	        String next;
			do {
	    		for(String crawl:dc.crawl()){
	    			System.err.println("crawl is: "+crawl + " url is: "+url);
	    			if(areSameDomain(crawl,url)){
	    			toCrawlLock.lock();
	    			toCrawl.addFirst(crawl);
	    			toCrawlLock.unlock();
	    			}else{
		    			toCrawlLock.lock();
	    				toCrawl.addLast(crawl);
		    			toCrawlLock.unlock();
	    			}	
	    		}
	    		next=getNextCrawl(); //concurent modifcation exception
	    		if(next==null){
	    			break;
	    		}
	    		System.err.println("the value of next is: "+next);
	    		dc.addURL(next); //add to domain crawler queue to crawl
	    		discovered.add(next);//make note that we are going to crawl this
	    		incrementIteration();
	    		System.out.println("we found: " +discovered);
	        } while (getIteration()<maxIteration && areSameDomain(next, url)); // is here to stop infinite crawling and to make it crawl only 1 domain

			//System.err.println("crawltop exclude crawl: "+excludeCrawl);
	        System.out.println("we found: " +discovered);

		 
	 }
	 
	 
	 public void retrieveMap(CrawlMap cm) {
	    map = cm;
	 }
	 
	 //synchronized to stop different threads from crawling the same url
	 private String getNextCrawl(){
		// System.err.println("before "+toCrawl);
		toCrawlLock.lock();
		Iterator<String> iterator = toCrawl.iterator();
		 while (iterator.hasNext()) {
			 String nextCrawl=iterator.next();
			 iterator.remove();
			 if(discovered.contains(nextCrawl)){
			 }else{
				 System.err.println("after "+toCrawl); //modified exception
					toCrawlLock.unlock();

				 return nextCrawl;
			 }	 
		 }	 
		 //System.err.println("after "+toCrawl);
			toCrawlLock.unlock();

		 return null;
	 }
}
