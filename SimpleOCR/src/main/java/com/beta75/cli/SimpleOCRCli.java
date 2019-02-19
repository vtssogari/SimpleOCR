package com.beta75.cli;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.beta75.SimpleOCR;
import com.beta75.domain.Document;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(name = "SimpleOCR", mixinStandardHelpOptions = true, version = "SimpleOCR 1.0")
public class SimpleOCRCli implements Callable<Void> {
    
	final static int FORCE = -1;

    @Parameters(arity = "1..*", paramLabel = "FILE", description = "one ore more files/dir to process")
    public File[] files;

    @Option(names = { "-t", "--max-char" }, description = "If PDF file, then threshold character size of each pages to start OCR or not. Overriden by -o")
    public int minimumThresholdCharSize = 10;
    
    @Option(names = "-f", description = "Force OCR processing")
    public boolean force = false;

    @Option(names = "-r", description = "recursive processing sub directory")
    public boolean recursive = false;
    
    @Option(names = "-o", required=true, description = "Output file for the result")
    public String output;
    
    @Option(names = "-x", description = "include hOCR informaiton")
    public boolean hocr = false;
    
    public boolean error = false;
    
    public Void call() throws Exception {
    	System.out.println("called...");
    	if(files == null){
    		error = true;
    	}else{
    		error = false;
    	}
        if(!error){
        	SimpleOCR instance = new SimpleOCR();
        	if (force) {
        		instance.minimumThresholdCharSize = FORCE;
    		} else {
    			instance.minimumThresholdCharSize = minimumThresholdCharSize;
    		}
    		try {
    			List<Document> docs = null;
    			docs = instance.ocr(files, this.recursive, this.hocr);
    			SimpleOCR.saveAsFile(docs, this.output);
    		} catch (Exception e) {
    			e.printStackTrace();
    			System.exit(500);
    		}
        }else{
        	System.exit(500);
        }
        
    	return null;
	}

	public static void main(String[] args) {
		CommandLine.call(new SimpleOCRCli(), args);
	}
	
	@Override
	public String toString() {
		return "CommandParser [files=" + Arrays.toString(files) + ", minimumThresholdCharSize="
				+ minimumThresholdCharSize + ", force=" + force + ", recursive=" + recursive + ", error=" + error + "]";
	}
    
}
