package com.beta75.ocr.samples;

import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.lept.BOX;
import org.bytedeco.javacpp.lept.BOXA;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import org.bytedeco.javacpp.tesseract.TessResultRenderer;

import com.beta75.util.Util;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OCR {

	static Logger logger = Logger.getLogger(OCR.class.getName());

	private TessBaseAPI init() throws Exception {
		String tessdata = initTraineddata();
		TessBaseAPI api = new TessBaseAPI();
		if (api.Init(tessdata, "eng") != 0) {
			throw new Exception("Could not initialize tesseract.");
		}
		return api;
	}

	private String initTraineddata() throws IOException {
		String currentDir = Util.getWorkingDir();
		File trainedDataFile = new File(getTrainDataPath());
		if (!trainedDataFile.exists()) {
			logger.log(Level.INFO, "Pretrained data is not found. Writing " + trainedDataFile);
			String trainedDataResource = "/tessdata/eng_best.traineddata";
			InputStream in = OCR.class.getClass().getResourceAsStream(trainedDataResource);
			java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(trainedDataFile));
			IOUtils.copy(new InputStreamReader(in), writer);
			writer.flush();
			writer.close();
			in.close();
		}
		return currentDir;
	}

	private String getTrainDataPath() throws IOException {
		String currentDir = Util.getWorkingDir();
		return currentDir + File.separator + "eng.traineddata";
	}

	private String extract(TessBaseAPI api, File imageFile) {
		String result = null;
		BytePointer outText;
		PIX image = pixRead(imageFile.getAbsolutePath());
		api.SetImage(image);
		outText = api.GetUTF8Text();
		result = Util.cleanText(outText.getString());
		outText.deallocate();
		pixDestroy(image);
		return result;
	}

	public String renderPDF(File imageFile) throws Exception {
		TessBaseAPI api = init();
		String pdfOutputPath = renderPDF(api, imageFile);
		api.End();
		return pdfOutputPath;
	}

	boolean textonly = false;
	String retry_config = null;
	int timeout_millisec = 5000;

	private String renderHOCR(TessBaseAPI api, File imageFile) throws IOException {
		String fileFolder = Util.getWorkingDir();
		String tessdataFolder = Util.getWorkingDir();
		String fileName = UUID.randomUUID().toString();
		String hocrOutput = fileFolder + File.separator + fileName;
		logger.log(Level.INFO, hocrOutput);
		tesseract.TessResultRenderer renderer;

		String inputFile = imageFile.getAbsolutePath();
		lept.PIX image = pixRead(inputFile);
		api.SetImage(image);
		renderer = tesseract.TessHOcrRendererCreate(hocrOutput);
		// tesseract.TessBaseAPISetOutputName(api, pdfOutputName);
		// tesseract.TessBaseAPISetVariable(api, "tessedit_create_pdf", "T");
		tesseract.TessResultRendererBeginDocument(renderer, tessdataFolder);
		boolean success = tesseract.TessBaseAPIProcessPages(api, inputFile, retry_config, timeout_millisec, renderer);
		tesseract.TessResultRendererEndDocument(renderer);
		pixDestroy(image);
		tesseract.TessDeleteResultRenderer(renderer);

		if (!success) {
			hocrOutput = null;
		}

		return hocrOutput;
	}

	private void getBoxes(TessBaseAPI api, List<File> imageFiles) {
		for (File file : imageFiles) {
			PIX image = pixRead(file.getAbsolutePath());
			api.SetImage(image);
			// Lookup all component images
			int[] blockIds = {};
			BOXA boxes = api.GetComponentImages(RIL_TEXTLINE, true, null, blockIds);
			//BOXA boxes = api.GetComponentImages(RIL_PARA, true, null, blockIds);
			for (int i = 0; i < boxes.n(); i++) {
				// For each image box, OCR within its area
		        BytePointer outText;
				BOX box = boxes.box(i);
				api.SetRectangle(box.x(), box.y(), box.w(), box.h());
				outText = api.GetUTF8Text();
				String ocrResult = outText.getString();
				int conf = api.MeanTextConf();
				String boxInformation = String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d, confidence: %d, text: %s", i, box.x(), box.y(), box.w(), box.h(), conf, ocrResult);
				System.out.println(boxInformation);
				outText.deallocate();
			}
			pixDestroy(image);
		}
	}

	private String renderPDF(TessBaseAPI api, List<File> imageFiles) throws IOException {
		String fileFolder = Util.getWorkingDir();
		String tessdataFolder = Util.getWorkingDir();
		String fileName = UUID.randomUUID().toString();
		String pdfOutputName = fileFolder + File.separator + fileName;
		logger.log(Level.INFO, pdfOutputName);
		tesseract.TessResultRenderer renderer;

		for (int i = 0; i < imageFiles.size(); i++) {
			File imageFile = imageFiles.get(i);
			String inputFile = imageFile.getAbsolutePath();
			lept.PIX image = pixRead(inputFile);
			api.SetImage(image);
			renderer = tesseract.TessPDFRendererCreate(pdfOutputName, tessdataFolder, false);
			// tesseract.TessBaseAPISetOutputName(api, pdfOutputName);
			// tesseract.TessBaseAPISetVariable(api, "tessedit_create_pdf",
			// "T");
			tesseract.TessResultRendererBeginDocument(renderer, tessdataFolder);
			boolean success = tesseract.TessBaseAPIProcessPage(api, image, i, inputFile, retry_config, timeout_millisec,
					renderer);
			tesseract.TessResultRendererEndDocument(renderer);
			tesseract.TessDeleteResultRenderer(renderer);
			pixDestroy(image);
			if (!success) {
				pdfOutputName = null;
				break;
			}
		}

		return pdfOutputName;
	}

	private String renderPDF(TessBaseAPI api, File imageFile) throws Exception {
		List<File> files = new ArrayList<File>();
		files.add(imageFile);
		return renderPDF(api, files);
	}

	public Result batchOCR(List<String> images, boolean boxinfo) throws Exception {
		List<File> imageFiles = new ArrayList<File>();
		for (String image : images) {
			imageFiles.add(new File(image));
		}
		return processOCR(imageFiles, boxinfo);
	}

	public Result processOCR(List<File> images, boolean boxinfo) throws Exception {
		Result result = new OCR.Result();
		List<String> list = new ArrayList<String>();
		TessBaseAPI api = init();
		
		for (File image : images) {
			logger.log(Level.INFO, "OCR extracting text " + image.getName());
			list.add(extract(api, image));
			BytePointer hOCR = api.GetHOCRText(images.size());
			System.out.println(hOCR.getString());
			
		}
		result.text = list;
		if (boxinfo) {
			
			getBoxes(api, images);
		}
		api.End();
		return result;
	}

	public Result processOCR(File image, boolean boxInfo) throws Exception {
		logger.log(Level.INFO, "OCR extracting text " + image.getName());
		Result result = new OCR.Result();
		TessBaseAPI api = init();
		result.text.add(extract(api, image));
		if (boxInfo) {
			List<File> images = new ArrayList<File>();
			images.add(image);
			BytePointer hOCR = api.GetHOCRText(images.size());
			System.out.println(hOCR.getString());
		}
		api.End();
		return result;
	}

	public class Result {
		public boolean converted2PDF;
		public List<String> text = new ArrayList<String>();
		public String convertedPDFPath;
	}

}
