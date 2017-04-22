package com.usamin.nana;

import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;

/*** process that downloads and parse webpage for hyperlinks ***/

public class Jjvcrawler {

	public static void main(String[] args) {

		/*** Send GET request ***/
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// String test =
		// "https://en.wikipedia.org/wiki/Cassiopeia_(constellation)";
		String test = "https://disorderlylabs.github.io/";
		int numToSearch = 30;

		// test= "https://sites.google.com/site/daviddeyellwatercolor/";
		CloseableHttpResponse response;

		/*** output file name ***/
		String filename = "unwrap.nana";
		Hashtable<String, String> searched = new Hashtable(); // used to store
																// things we
																// looked at
																// already
		ArrayList<String> toSearch = new ArrayList<String>();// what we are
																// trying to
																// search
		toSearch.add(test);

		while (!toSearch.isEmpty() && searched.size() < numToSearch) {
			if (searched.contains(toSearch.get(0))) {
				toSearch.remove(0); // we already searched this one, throw it
									// out
				continue;
			}
			
			HttpGet httpget = new HttpGet(toSearch.get(0));

			System.out.println("searched size is: " + searched.size());

			try {
				/*** writer for dl'ed page to file ***/
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));

				/*** process response ***/
				response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					/*** extract page content ***/
					InputStream instream = entity.getContent();
					Document doc = Jsoup.parse(instream, "UTF-8", test);

					/***
					 * extract html elements that contain the <a> tag with href
					 * attribute
					 ***/
					Elements links = doc.select("a[href]");

					/*** Extract links from html to file ***/
					links.unwrap();
					for (Element link : links) {
						writer.write(link.attr("abs:href") + '\n');
						toSearch.add(link.attr("abs:href"));
					}
					searched.put(toSearch.get(0),toSearch.get(0));
					toSearch.remove(0);// we are now done searching for this so
										// remove
					//System.out.println(toSearch);
					instream.close();

				}
				response.close();
				writer.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

			}

		}
		//System.out.println(searched);
		for( String key : searched.keySet() ) {
		    System.out.println(key);
		}
		System.out.println("\ndone");
		System.out.println("we found " + searched.size() + " sites");

	}

}
