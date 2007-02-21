/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
 * 
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify 
 * and/or redistribute the software under the terms of the CeCILL-C license as 
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *  
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */

package spoon.support.processing;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import spoon.AbstractLauncher;
import spoon.support.builder.CtResource;

/**
 * This class defines the SAX handler to parse a Spoonlet deployment descriptor
 * file.
 */
public class SpoonletXmlHandler extends DefaultHandler {

	private AbstractLauncher launcher;

	private List<CtResource> spoonletIndex;

	XmlProcessorProperties prop;

	String propName;

	List<Object> values;

	String buffer;

	/**
	 * Creates a new handler.
	 * 
	 * @param launcher
	 *            the launcher
	 * @param spoonletIndex ?
	 */
	public SpoonletXmlHandler(AbstractLauncher launcher,
			List<CtResource> spoonletIndex) {
		super();
		this.launcher = launcher;
		this.spoonletIndex = spoonletIndex;
	}

	/**
	 * Handles XML element ends.
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("processor")) {
			launcher.getFactory().getEnvironment().setProcessorProperties(
					prop.getProcessorName(), prop);
			prop = null;
		} else if (localName.equals("property")) {
			if (values != null) {
				prop.addProperty(propName, values);
			}
			values = null;
			propName = null;
		} else if (localName.equals("value")) {
			values.add(buffer);
		}
		buffer = null;
		super.endElement(uri, localName, qName);
	}

	/**
	 * Handles characters.
	 */
	@Override
	public void characters(char[] ch, int start, int end) throws SAXException {
		buffer = new String(ch, start, end);
	}

	/**
	 * Handles XML element starts.
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals("processor")) {
			launcher.addProcessor(attributes.getValue("class"));
			prop = new XmlProcessorProperties(launcher.getFactory(), attributes
					.getValue("class"));
		} else if (localName.equals("template")) {
			if (attributes.getValue("path") != null){
				String foldername = attributes.getValue("path");
				for (CtResource r : spoonletIndex) {
					if (r.getName().startsWith(foldername)) {
						launcher.addTemplateResource(r);
					}
				}
			}
			if (attributes.getValue("folder") != null){
				String foldername = attributes.getValue("folder");
				for (CtResource r : spoonletIndex) {
					if (r.getName().startsWith(foldername)) {
						launcher.addTemplateResource(r);
					}
				}
			}			
			if (attributes.getValue("file") != null){
				String filename = attributes.getValue("file");
				for (CtResource r : spoonletIndex) {
					if (r.getName().startsWith(filename)) {
						launcher.addTemplateResource(r);
					}
				}
			}
			
		} else if (localName.equals("property")) {
			propName = attributes.getValue("name");
			if (attributes.getValue("value") != null) {
				prop.addProperty(propName, attributes.getValue("value"));
			} else {
				values = new ArrayList<Object>();
			}
		}
	}
}
