package com.beta75;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.beta75.ocr.OCR;
import com.beta75.pdfbox.PdfToImage;
import com.beta75.tika.DocumentParser;
import com.beta75.util.Util;

public class SimpleOCR {

	public static Document ocr(File file) throws Exception {
		int minimumThresholdSize = 10;
		Document doc = DocumentParser.parse(file);
		if(doc.isPDF() && (!doc.hasText() || shouldOCR(doc, minimumThresholdSize)) ){
			// do OCR on PDF
			List<File> images = PdfToImage.convert(file);
			List<String> pages = OCR.run(images);
			doc.setPages(pages);
			Util.cleanup(images);
		}else if(doc.isImage()){
			// do OCR on image
			String page = OCR.run(file);
			List<String> pages = new ArrayList<String>();
			pages.add(page);
			doc.setPages(pages);
		}
		return doc;
	}
	
	public static boolean shouldOCR(Document doc, int minSizeContent){
		int avgContent = doc.getContentSize() / doc.getPageCount();
		return (minSizeContent > avgContent);
	}
}
