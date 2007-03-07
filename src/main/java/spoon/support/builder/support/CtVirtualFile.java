package spoon.support.builder.support;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;

public class CtVirtualFile implements CtFile{

	InputStream contents;
	
	String name = "";
	
	public CtVirtualFile(String _contents) {
		byte[] contentsBA = _contents.getBytes();
		contents = new ByteArrayInputStream(contentsBA);
		
	}
	
	public CtVirtualFile(String _contents, String _name){
		this(_contents);
		name = _name;
	}
	
	public InputStream getContent() {
		return contents;
	}

	public boolean isJava() {
		return true;
	}

	public String getName() {
		return name;
	}

	public CtFolder getParent() {
		return new CtVirtualFolder();
	}

	public String getPath() {
		return "";
	}

	public boolean isFile() {
		return true;
	}
	
}

