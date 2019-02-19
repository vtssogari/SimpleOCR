package com.beta75.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;

import com.beta75.domain.Document;

public class DocumentParser {

	static Logger logger = Logger.getLogger(DocumentParser.class.getName());
	public static Document parse(File file) throws IOException {
		logger.log(Level.INFO,"Parsing " + file.getName());
		Document doc = new Document(file);
		try {
			populate(doc, file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
	    return doc;
	}
	
	private static void populate(Document doc, File file) throws IOException, SAXException, TikaException {
		logger.log(Level.INFO,"Extracting metadata " + file.getName());
		Tika tika = new Tika();
	    String filetype = tika.detect(file);	    
	    doc.setMimetype(filetype);	 
	    doc.setMediaType(MediaType.parse(filetype));
	    getMeta(doc, tika, file);
	}
	
	private static Metadata getMeta(Document doc, Tika tika, File file) throws IOException, SAXException, TikaException {
		// Parser method parameters
		Parser parser = new AutoDetectParser();
		PageContentHandler handler = new PageContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(file);
		ParseContext context = new ParseContext();
		parser.parse(inputstream, handler, metadata, context);
		doc.setPages(handler.getPages());
		doc.setContentSize(handler.getContentSize());
		doc.setMeta(metadata);
		return metadata;
	}

}
