package com.beta75.ocr;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.beta75.Document;
import com.beta75.SimpleOCR;

public class BasicOCRTest {

	@Test
	public void testBatch() throws Exception {
		List<File> files = new ArrayList<File>();
		files.add(getTestFile("test2.PNG"));
		files.add(getTestFile("test3.PNG"));
		files.add(getTestFile("test4.PNG"));
		List<String> texts = OCR.run(files);
		List<String> expected = new ArrayList<String>();
		expected.add("ocr");
		expected.add("testing");
		expected.add("abc home");
		for(int i = 0; i < texts.size(); i++){
			assert(texts.get(i).trim().equalsIgnoreCase(expected.get(i)));
		}
	}
	
	@Test
	public void testSingle() throws Exception {
		String testString = "This is testing OCR success";
		File image = getTestFile("test.PNG");
		String text = OCR.run(image);
		System.out.println(text);
		text = text.replaceAll("\n", " ").replaceAll("  ", " ").trim();
		assert(text.equalsIgnoreCase(testString));
	}
	
	@Test
	public void testPDF() throws Exception {
		String pdf = "Non-text-searchable.pdf";
		Document doc = SimpleOCR.ocr(getTestFile(pdf));
		System.out.println(doc.toPrettyJson());
		assert(doc.content().contains("This bs un example of a non-text-scarchable PDF"));
		assert(doc.isPDF());
	}
	@Test
	public void testMultiplePDF() throws Exception {
		String pdf = "test.pdf";
		Document doc = SimpleOCR.ocr(getTestFile(pdf));
		System.out.println(doc.toPrettyJson());
		assert(doc.hasText());
		assert(doc.isPDF());
	}
	@Test
	public void testImage() throws Exception {
		String pdf = "test.PNG";
		String testString = "This is testing OCR\nsuccess";
		Document doc = SimpleOCR.ocr(getTestFile(pdf));
		System.out.println(doc.toPrettyJson());
		System.out.println(doc.content());
		System.out.println(testString);
		assert(doc.content().equalsIgnoreCase(testString));
		assert(doc.isImage());
	}
	
	public File getTestFile(String filename) {
		ClassLoader classLoader = getClass().getClassLoader();
		File image = new File(classLoader.getResource(filename).getFile());
		return image;
	}
}
