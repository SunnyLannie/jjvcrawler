package com.usamin.nana.extractor;

import java.net.URI;
import java.net.URISyntaxException;
//commonly used functions are placed in this file to allow code reuse

public class CrawlerUniversal {

   public CrawlerUniversal() {
      
   }
   
   public static final String removeSpace(String url) {
      return url.replaceAll(" ", "%20");
   }
   
   public static final String getHost(String url) {
      url = removeSpace(url);
      URI uri = null;
      try {
         uri = new URI(url);
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }
      String hostname = uri.getHost();
      return hostname;
   }

}
