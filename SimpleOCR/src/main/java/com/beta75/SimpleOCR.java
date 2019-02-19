package com.beta75;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.beta75.domain.Document;
import com.beta75.ocr.OCR;
import com.beta75.pdfbox.PdfToImage;
import com.beta75.tika.DocumentParser;
import com.beta75.util.Util;
import com.uwyn.jhighlight.tools.FileUtils;

public class SimpleOCR {
	static Logger logger = Logger.getLogger(SimpleOCR.class.getName());
	final static int FORCE = -1;
	public int minimumThresholdCharSize = 10;
	
	public List<Document> ocr(File[] files) throws Exception {
		return ocr(files, false, false);
	}
	
	public List<Document> ocr(File[] files, boolean recursive, boolean enableHORC) throws Exception {
		List<Document> docs = new ArrayList<Document>();
		int count = files.length;
		int current = 1;
		for(File f: files){
			logger.log(Level.INFO, "Processing " +  current + "/" + count);
			if(f.isDirectory()){
				if(recursive){
					docs.addAll(ocr(f.listFiles(), true, enableHORC));
				}else{
					docs.addAll(ocr(f.listFiles(), false, enableHORC));
				}
			}else{
				docs.add(ocr(f, enableHORC));
			}
			current++;
		}
		return docs;
	}
	
	public Document ocr(File file, boolean enableHORC) throws Exception {
		logger.log(Level.INFO, "Processing " + file.getName());
		Document doc = DocumentParser.parse(file);
		OCR ocr = new OCR(enableHORC);
		if (doc.isPDF() && (!doc.hasText() || shouldOCR(doc, minimumThresholdCharSize))) {
			// do OCR on PDF
			List<File> images = PdfToImage.convert(file);
			OCR.Result result = ocr.processOCR(images);
			doc.setPages(result.text);
			doc.setHorc(result.horc);
			doc.setOcred(true);
			Util.cleanup(images);
		} else if (doc.isImage()) {
			// do OCR on image
			OCR.Result result = ocr.processOCR(file);
			doc.setOcred(true);
			doc.setPages(result.text);
			doc.setHorc(result.horc);
		}
		return doc;
	}

	public boolean shouldOCR(Document doc, int minSizeContent) {
		if (minSizeContent == FORCE) return true;
		int avgContent = doc.getContentSize() / doc.getPageCount();
		return (minSizeContent > avgContent);
	}
	
	public static String saveAsFile(List<Document> docs, String output) throws IOException {
		File out = new File(output);
		for(Document doc: docs){
			org.apache.commons.io.FileUtils.writeStringToFile(out, doc.toJson() + "\n", true);
		}
		return out.getAbsolutePath();
	}
}
