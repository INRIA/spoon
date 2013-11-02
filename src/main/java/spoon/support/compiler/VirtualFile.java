package spoon.support.compiler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;

public class VirtualFile implements SpoonFile{

	InputStream contents;
	
	String name = "virtual_file";
	
	public VirtualFile(String _contents) {
		byte[] contentsBA = _contents.getBytes();
		contents = new ByteArrayInputStream(contentsBA);
		
	}
	
	public VirtualFile(String _contents, String _name){
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

	public SpoonFolder getParent() {
		return new VirtualFolder();
	}

	public String getPath() {
		return name;
	}

	public boolean isFile() {
		return true;
	}
	
}

