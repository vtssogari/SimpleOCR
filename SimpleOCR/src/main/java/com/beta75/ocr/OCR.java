package com.beta75.ocr;

import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.*;

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

public class OCR {
	
	private static TessBaseAPI init() throws Exception{
		String tessdata = initTraineddata();
		TessBaseAPI api = new TessBaseAPI();
        if (api.Init(tessdata, "eng") != 0) {
           throw new Exception("Could not initialize tesseract.");
        }
        return api;
	}
	
	private static String initTraineddata() throws IOException{
		// Initialize tesseract-ocr with English, without specifying tessdata path
		String currentDir = Util.getWorkingDir();
		String trainedData = currentDir+File.separator+"eng.traineddata";
		//System.out.println("checking eng.traineddata file " + trainedData);
		File trainedDataFile = new File(trainedData);
		if(!trainedDataFile.exists()){
			System.out.println("Not found. Writing "+trainedDataFile);
			String trainedDataResource = "/tessdata/eng_best.traineddata";		
			InputStream in = OCR.class.getClass().getResourceAsStream(trainedDataResource); 		
			java.io.BufferedWriter  writer = new java.io.BufferedWriter(new java.io.FileWriter(trainedDataFile));    
			IOUtils.copy(new InputStreamReader(in), writer);
			writer.flush();
			writer.close();
			in.close();
		}
		return currentDir;
	}
	
	private static String extract(TessBaseAPI api, File imageFile){
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
	
	public static String run(String image) throws Exception {
		return run(new File(image));
	}
	
	public static List<String> batch(List<String> images) throws Exception {
		List<File> imageFiles = new ArrayList<File>();
		for(String image: images){
			imageFiles.add(new File(image));
		}
		return run(imageFiles);
	}
	
	public static List<String> run(List<File> images) throws Exception{
		List<String> list = new ArrayList<String>();
		TessBaseAPI api = init();
		for(File image : images){
			list.add(extract(api, image));
		}
        api.End();
        return list;
	}
	
	public static String run(File image) throws Exception{
        TessBaseAPI api = init();
        String text = extract(api, image);
        api.End();
        return text;
	}
}
