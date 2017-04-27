package com.usamin.nana;

import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import com.usamin.nana.extractor.DomainCrawler;

public class CrawlMap extends HashMap<String, DomainCrawler>{

   PriorityBlockingQueue<DomainCrawler> frontier;
   public CrawlMap() {
      super();
      frontier = new PriorityBlockingQueue<DomainCrawler>();
   }
   
   public CrawlMap(String seed) {
      super();
      DomainCrawler crawler = new DomainCrawler(seed);
      String hostname = crawler.getHostname();
      put(hostname, crawler);
   }
   
   
   
   
   

}
