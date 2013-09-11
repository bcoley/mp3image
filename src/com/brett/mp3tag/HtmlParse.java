package com.brett.mp3tag;

import javax.swing.text.html.HTMLEditorKit;

public class HtmlParse extends HTMLEditorKit {
 
	private static final long serialVersionUID = 3258984399262844232L;

	public HTMLEditorKit.Parser getParser() {
		return super.getParser();
	}
}
