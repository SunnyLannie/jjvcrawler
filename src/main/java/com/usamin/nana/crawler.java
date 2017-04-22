package com.usamin.nana;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class crawler {
	Hashtable<String, String> discovered = new Hashtable(); // stores what this
															// url finds

	crawler() {
		discovered.clear();
	}
	CloseableHttpResponse response;
	CloseableHttpClient httpclient = HttpClients.createDefault();

	public Hashtable<String, String> crawl(String crawling) {
		discovered.clear();
		HttpGet httpget = new HttpGet(crawling);


		try {
			/*** writer for dl'ed page to file ***/

			/*** process response ***/
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				/*** extract page content ***/
				InputStream instream = entity.getContent();
				Document doc = Jsoup.parse(instream, "UTF-8", crawling);

				/***
				 * extract html elements that contain the <a> tag with href
				 * attribute
				 ***/
				Elements links = doc.select("a[href]");

				/*** Extract links from html to file ***/
				links.unwrap();
				for (Element link : links) {
					discovered.put(link.attr("abs:href"),(link.attr("abs:href")));
				}

			}
			response.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return discovered;
	}
}
