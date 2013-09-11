package com.brett.mp3tag;

import java.net.URLDecoder;

public class ParserHelper {
	public String extractJpgFromUrl(String urlString) {
		String result = null;
		// input is like
		// http://cs.dogpile.com/ClickHandler.ashx?du=http%3a%2f%2fwww.darkduck.net%2fimgs%2fnewimgs2%2fDronesDeep02_300.jpg&ru=...
		String[] parts = urlString.split("du=");
		if (parts.length > 1) {
			String parts2[] = parts[1].split("&ru");
			if (parts2.length > 1) {
				if (isStringJpgUrl(parts2[0])) {
					result =  decodeURL(parts2[0]);
				}
			}
		}
		
		return result;
	}
	
	public String decodeURL(String rawString) {
		String result = URLDecoder.decode(rawString);
		return result;
	}
	
	public boolean isStringJpgUrl(String inputString) {
		// TODO: this could be better, deal with .JPG .jpeg .gif etc.
		if (inputString.startsWith("http")  && inputString.endsWith(".jpg")) {
			return true;
		}
		return false;
	}
}
