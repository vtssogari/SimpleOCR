package com.beta75.pdfbox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.beta75.util.Util;

public class PdfToImage {

	public static List<File> convert(File pdf) throws Exception {
		String baseFolder = Util.getWorkingDir();
		List<File> files = new ArrayList<File>();
		PDDocument document = PDDocument.load(pdf);
		PDFRenderer renderer = new PDFRenderer(document);
		PDPageTree tree = document.getDocumentCatalog().getPages();
		for(int i=0; i < tree.getCount();i++){
			BufferedImage image = renderer.renderImage(i);
			String filename = baseFolder + File.separator + UUID.randomUUID().toString() +".tiff";
			File f = new File(filename);
			ImageIO.write(image, "TIFF", f);
			files.add(f);
		}
		document.close();
		return files;
	}
	
}