package com.brett.mp3tag;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;


public class Parser extends HTMLEditorKit.ParserCallback {

	protected URL base;
	

    private Set<String> urls = new LinkedHashSet<String>();

    public Set<String> getUrls() {
        return urls;
    }
	
	public Parser(URL base) {
		this.base = base;
	}
	
    @Override
    public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
        handleTag(t, a, pos);
    }

    @Override
    public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
        handleTag(t, a, pos);
    }

    private void handleTag(Tag t, MutableAttributeSet a, int pos) {
        if (t == Tag.A) {
            Object href = a.getAttribute(HTML.Attribute.HREF);
            if (href != null) {
                String newUrl = href.toString();
                if (!urls.contains(newUrl)) {
                    urls.add(newUrl);
                }
            }
        }
    }
}
