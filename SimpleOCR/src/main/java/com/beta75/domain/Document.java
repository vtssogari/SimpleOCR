package com.beta75.domain;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;

import com.beta75.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Document {
	
	static final MediaType PDF = MediaType.application("pdf");
	static final String IMAGE = "image";
	static final String AUDIO = "audio";
	static final String TEXT = "text";
	static final String VIDEO = "video";
	static final String APPLICATION = "application";
	
	private String name;
	private String extension;
	private String path;
	private String mimetype;
	private BasicFileAttributes attr;
	private Metadata meta;
	private List<String> pages;
	private int contentSize;
	private MediaType mediaType;
	
	private boolean ocred;
	private List<String> horc;

	public Document(File file) throws IOException{
		this.name = file.getName();
		this.extension = FilenameUtils.getExtension(this.name);
		this.path = file.getAbsolutePath();
        Path path = Paths.get(file.getAbsolutePath());
		this.attr = Files.readAttributes(path, BasicFileAttributes.class);	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	
	public MediaType getMediaType() {
		return mediaType;
	}
	
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public BasicFileAttributes getAttr() {
		return attr;
	}

	public void setAttr(BasicFileAttributes attr) {
		this.attr = attr;
	}

	public Metadata getMeta() {
		return meta;
	}

	public void setMeta(Metadata meta) {
		this.meta = meta;
	}

	public List<String> getPages() {
		return pages;
	}

	public void setPages(List<String> pages) {
		this.pages = pages;
		this.contentSize = Util.getContentSize(pages);
	}

	public int getPageCount(){
		return this.pages != null ? this.pages.size(): 0;
	}

	public int getContentSize() {
		return contentSize;
	}

	public void setContentSize(int contentSize) {
		this.contentSize = contentSize;
	}

	public boolean isOcred() {
		return ocred;
	}

	public void setOcred(boolean ocred) {
		this.ocred = ocred;
	}

	public List<String> getHorc() {
		return horc;
	}

	public void setHorc(List<String> horc) {
		this.horc = horc;
	}

	public String content(){
		return StringUtils.join(this.getPages(), "\n"); 
	}

	public boolean isPDF(){
        return (this.mediaType == PDF);
	}
	public boolean isImage(){
		return (IMAGE.equalsIgnoreCase(this.mediaType.getType()));
	}
	public boolean isAudio(){
		return (AUDIO.equalsIgnoreCase(this.mediaType.getType()));
	}
	public boolean isVideo(){
		return (VIDEO.equalsIgnoreCase(this.mediaType.getType()));
	}
	
	public boolean hasText(){
		return this.contentSize > 0;
	}
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public String toPrettyJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(this);
		return json;
	}
	
	@Override
	public String toString() {
		return "Document [name=" + name + ", extension=" + extension + ", path=" + path + ", mimetype=" + mimetype + ", attr="
				+ attr + ", metadata=" + meta + ", pages=" + pages + "]";
	}
	
	
}
