package com.beta75.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Util {
	public static String getWorkingDir() throws IOException{
		String workingDir =  System.getProperty("user.dir") + File.separator + "tmp";
		File dir = new File(workingDir);
		if(!dir.exists()){
			FileUtils.forceMkdir(dir);
		}
		return workingDir;
	}

	public static void cleanup(List<File> files) throws IOException{
		for(File f: files){
			FileUtils.forceDelete(f);
		}
	}
	
	public static String cleanText(String text) {
		if(text != null){
			return text.trim();
		}else{
			return text;
		}
	}
	
	public static int getContentSize(List<String> contents){
		int size = 0;
		if(contents != null){
			for(String content: contents){
				size += content.length();
			}
		}
		return size;
	}
}
