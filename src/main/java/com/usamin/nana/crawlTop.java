package com.usamin.nana;

import java.util.HashSet;
import java.util.Iterator;

import com.usamin.nana.extractor.CrawlerUniversal;
import com.usamin.nana.extractor.DomainCrawler;

//this implements the top level crawling logic
//TODO fifo queue
//TODO 
//TODO add a new class where each domain has it's own class

public class crawlTop extends CrawlerUniversal {
	HashSet<String> alreadyCrawled;
	HashSet<String> toCrawl;
	HashSet<String> excludeCrawl;
    long maxIteration=200;
    
    
	 public crawlTop(String url) {
		 alreadyCrawled = new HashSet<String>(); //maybe alreadyCrawled and excludeCrawl should be the same set since they serve the same purpose?
		 //but maybe not due to robots txt not being a static page
		 excludeCrawl = new HashSet<String>();
		 toCrawl = new HashSet<String>();

		DomainCrawler dc = new DomainCrawler(url);
		alreadyCrawled.add(url);
		Iterator iter = dc.getCrawlExclusion().iterator();
		url=trimUrl(url); //remove extra '/'
		while (iter.hasNext()) {
		   excludeCrawl.add(url+iter.next());
		}
		System.err.println(excludeCrawl);
		int iteration=0;
		
        do {
    		for(String crawl:dc.crawl()){
    			toCrawl.add(crawl);
    		}
    		String next=getNextCrawl();
    		if(next==null){
    			break;
    		}
    		System.err.println("the value of next is: "+next);
    		dc.addURL(next); //add to domain crawler queue to crawl
    		alreadyCrawled.add(next);//make note that we are going to crawl this
    		iteration++;
            System.out.println("we found: " +alreadyCrawled);
        } while (iteration<maxIteration); //this is here to stop infinite crawling

		//System.err.println("crawltop exclude crawl: "+excludeCrawl);
        System.out.println("we found: " +alreadyCrawled);
	    }
	 private String getNextCrawl(){
		 System.err.println("before "+toCrawl);
		 Iterator<String> iterator = toCrawl.iterator();
		 while (iterator.hasNext()) {
			 String nextCrawl=iterator.next();
			 iterator.remove();
			 if(alreadyCrawled.contains(nextCrawl) || excludeCrawl.contains(nextCrawl)){
			 }else{
				 System.err.println("after "+toCrawl);
				 return nextCrawl;
			 }	 
		 }	 
		 System.err.println("after "+toCrawl);

		 return null;
	 }

}
