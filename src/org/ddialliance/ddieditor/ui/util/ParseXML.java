package org.ddialliance.ddieditor.ui.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;


public class ParseXML extends DefaultHandler {

	static private SAXParserFactory spf;
	static private javax.xml.parsers.SAXParser sp;
	private String targetElementName;
	private String currentElementname;
	private String value = "";
	
	public ParseXML() {
		spf = SAXParserFactory.newInstance();
		try {
			sp = spf.newSAXParser();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseDocument(String xml) {

		try {
			sp.parse(new InputSource( new StringReader(xml) ), this);
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
    //Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// Remove Namespace
		currentElementname = qName.replaceAll("\\S+:", "");
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (targetElementName.equals(currentElementname)) {
			value = new String(ch, start, length);
			currentElementname = ""; // Element found
		}
	}

	public void endElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

	}

	public String getElement(String xml, String elementName) {
		this.targetElementName = elementName;
		this.value = "";
		parseDocument(xml);
		return value;
	}

}
