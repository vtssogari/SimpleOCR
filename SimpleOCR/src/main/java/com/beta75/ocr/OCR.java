package com.beta75.ocr;

import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.lept.BOX;
import org.bytedeco.javacpp.lept.BOXA;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.ETEXT_DESC;
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

	private boolean horc = false;

	public OCR(boolean enableHORC) {
		this.horc = enableHORC;
	}

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

	private void extract(TessBaseAPI api, File imageFile, Result result, int pageNum) {
		BytePointer outText;
		PIX image = pixRead(imageFile.getAbsolutePath());
		api.SetImage(image);
		outText = api.GetUTF8Text();
		if(this.horc){
			BytePointer hOCR = api.GetHOCRText(pageNum);
			System.out.println(hOCR.getString());
			result.horc.add(hOCR.getString());
			hOCR.deallocate();
		}
		result.text.add(Util.cleanText(outText.getString()));
		outText.deallocate();
		pixDestroy(image);
	}

	public Result batchOCR(List<String> images) throws Exception {
		List<File> imageFiles = new ArrayList<File>();
		for (String image : images) {
			imageFiles.add(new File(image));
		}
		return processOCR(imageFiles);
	}

	public Result processOCR(List<File> images) throws Exception {
		Result result = new OCR.Result();
		List<String> list = new ArrayList<String>();
		TessBaseAPI api = init();
		for (int i = 0; i < images.size(); i++) {
			File image = images.get(i);
			logger.log(Level.INFO, "OCR extracting text " + image.getName());
			extract(api, image, result, i);
		}
		result.text = list;
		api.End();
		return result;
	}

	public Result processOCR(File image) throws Exception {
		logger.log(Level.INFO, "OCR extracting text " + image.getName());
		Result result = new OCR.Result();
		TessBaseAPI api = init();
		extract(api, image, result, 0);
		api.End();
		return result;
	}

	public class Result {
		public List<String> text = new ArrayList<String>();
		public List<String> horc = new ArrayList<String>();
	}

}
