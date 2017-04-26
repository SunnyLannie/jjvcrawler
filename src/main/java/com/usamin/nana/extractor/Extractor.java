package com.usamin.nana.extractor;

import java.util.PriorityQueue;

public class Extractor {

   PriorityQueue<String> urls; // priority of links found in this domain
   
   public Extractor() {
      urls = new PriorityQueue<String>();
   }
   

}
