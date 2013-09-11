package com.brett.mp3tag;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.text.html.HTMLEditorKit;

public class ImageUrlDogpileFetcher {
	
	public List<String> searchForImageUrls(List<String> searchTerms) {
		List<String> resultList = new ArrayList<String>();
		ParserHelper helper = new ParserHelper();
		String urlStr = "http://www.dogpile.com/info.dogpl/search/images?q=";
		boolean first = true;
		for(String term: searchTerms) {
			if (first) {
				urlStr = urlStr + term;
				first = false;
			}
			else {
				urlStr = urlStr + "+" + term;
			}
		}
				
		try {
			Thread.sleep(250);
			URL url = new URL(urlStr);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setConnectTimeout(15000);
			urlConnection.setReadTimeout(15000);
			String cookie = "ws_prefs=af=None";
			urlConnection.setRequestProperty("Cookie", cookie);
			urlConnection.connect();
			InputStream inputStream = urlConnection.getInputStream();
			Reader reader = new InputStreamReader(inputStream);
			HTMLEditorKit.Parser kitParser = new HtmlParse().getParser();
			Parser parserCallback = new Parser(url);
			kitParser.parse(reader, parserCallback, true);
			Set<String> urls = parserCallback.getUrls();
			reader.close();
			//System.out.println("\nfound " + urls.size() + " urls.");
			
			for (String urlString: urls) {
				//System.out.println(urlString);
				String jpgUrl = helper.extractJpgFromUrl(urlString);
				if (null != jpgUrl) {
					resultList.add(jpgUrl);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

}
