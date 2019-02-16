package com.beta75.tika;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PageContentHandler extends ContentHandlerDecorator {

	private StringBuffer sb;
	private List<String> pages = null;
	private int contentSize = 0;
	private String element = null;
	
	@Override
	public void startDocument() throws SAXException {
		pages = new ArrayList<String>();
		this.contentSize = 0;
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
		this.element = name;
		if("body".equals(name)){
			this.sb = new StringBuffer();
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if("body".equals(name)){
			String content = sb.toString().trim();
			this.contentSize += content.length();
			this.pages.add(content);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if("body".equals(element)){
			sb.append(ch);
		}
	}

	public List<String> getPages() {
		return pages;
	}

	public void setPages(List<String> pages) {
		this.pages = pages;
	}

	public int getContentSize() {
		return contentSize;
	}

	@Override
	public String toString() {
		return StringUtils.join(this.pages, "\n");
	}
	
}
