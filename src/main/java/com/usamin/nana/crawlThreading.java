package com.usamin.nana;

import com.usamin.nana.extractor.DomainCrawler;

public class crawlThreading extends Thread{
	final private crawlTop top;
    final private String initialCrawl;
	public crawlThreading(crawlTop top, String initialCrawl){
		this.initialCrawl=initialCrawl;
		this.top=top;
	}
	public void run() {
		DomainCrawler dc = new DomainCrawler(initialCrawl);
		top.executeCrawl(dc);
	}
}
