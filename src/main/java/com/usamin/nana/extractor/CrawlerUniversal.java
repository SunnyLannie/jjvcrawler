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
   
   //removes final / from url if it is present
   public static final String trimUrl(String url){
	   if (url != null && url.length() > 0 && url.charAt(url.length()-1)=='/') {
		   url = url.substring(0, url.length()-1);
		    }
	   return url;
	   
   }

}
