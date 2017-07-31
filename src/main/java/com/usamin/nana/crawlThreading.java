package com.usamin.nana;

import com.usamin.nana.extractor.DomainCrawler;

public class crawlThreading implements Runnable{
	final private crawlTop top;
    final private String initialCrawl;
    final private int threadNumber;
	public crawlThreading(crawlTop top, String initialCrawl, int threadNumber){
		this.initialCrawl=initialCrawl;
		this.top=top;
		this.threadNumber=threadNumber;
	}
	public void run() {
		DomainCrawler dc = new DomainCrawler(initialCrawl);
		if(threadNumber!=0){ //we want to avoid crawling the starting url more then once
		dc.removeNextCrawlUrl();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		top.executeCrawl(dc);
	}
}
