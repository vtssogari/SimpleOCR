package com.beta75.ocr;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.beta75.SimpleOCR;
import com.beta75.domain.Document;
import com.beta75.ocr.OCR.Result;

public class BasicOCRTest {

	SimpleOCR instance = new SimpleOCR();
	
	@Test
	public void testBatch() throws Exception {
		List<File> files = new ArrayList<File>();
		files.add(getTestFile("test2.PNG"));
		files.add(getTestFile("test3.PNG"));
		files.add(getTestFile("test4.PNG"));
		OCR ocr = new OCR(true);
		Result result = ocr.processOCR(files);
		List<String> expected = new ArrayList<String>();
		expected.add("ocr");
		expected.add("testing");
		expected.add("abc home");
		for(int i = 0; i < result.text.size(); i++){
			assert(result.text.get(i).trim().equalsIgnoreCase(expected.get(i)));
		}
	}
	
	@Test
	public void testSingle() throws Exception {
		String testString = "This is testing OCR success";
		File image = getTestFile("test.PNG");
		OCR ocr = new OCR(true);
		Result result = ocr.processOCR(image);
		String text = StringUtils.join(result.text, " ");
		text = text.replaceAll("\n", " ").replaceAll("  ", " ").trim();
		assert(text.equalsIgnoreCase(testString));
	}
	
	@Test
	public void testPDF() throws Exception {
		String pdf = "Non-text-searchable.pdf";
		Document doc = instance.ocr(getTestFile(pdf), true);
		System.out.println(doc.toPrettyJson());
		assert(doc.isOcred());
		assert(doc.isPDF());
	}
	
	@Test
	public void testMultiplePDF() throws Exception {
		String pdf = "test.pdf";
		Document doc = instance.ocr(getTestFile(pdf), true);
		System.out.println(doc.toPrettyJson());
		assert(doc.isOcred());
		assert(doc.isPDF());
	}
	@Test
	public void testImage() throws Exception {
		String pdf = "test.PNG";
		String testString = "This is testing OCR\nsuccess";
		Document doc = instance.ocr(getTestFile(pdf), true);
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
