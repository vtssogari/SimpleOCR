package com.beta75.tika;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.beta75.Document;
import com.beta75.ocr.BasicOCRTest;

public class BasicTikaTest {

	@Test
	public void test() throws IOException, SAXException, TikaException {
		Document doc = DocumentParser.parse(getResourceFile("test.PNG"));
		System.out.println(doc.toPrettyJson());
	}

	@Test
	public void testContent() throws IOException, SAXException, TikaException {
		Document doc = DocumentParser.parse(getResourceFile("test.PNG"));
		System.out.println(doc.toPrettyJson());
		assert(doc.isImage());
	}
	@Test
	public void testPDF() throws IOException, SAXException, TikaException {
		Document doc = DocumentParser.parse(getResourceFile("Non-text-Searchable.pdf"));
		System.out.println(doc.toPrettyJson());
		assert(doc.isPDF());
	}
	
	private static File getResourceFile(String filename){
		BasicOCRTest t = new BasicOCRTest();
		File f = t.getTestFile(filename);
		return f;
	}
}
