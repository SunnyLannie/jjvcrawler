package com.usamin.nana;

import java.net.URI;
import java.net.URISyntaxException;

public class util {
	public String removeSpace(String url) {
		   return url.replaceAll(" ", "%20");
		}
		
		public String getHost(String url) {
		  url=removeSpace(url);
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
