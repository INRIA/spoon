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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import spoon.processing.ProcessorProperties;
import spoon.reflect.Factory;

/**
 * This class defines a processor properties accessor that parses an XML
 * description of the properties.
 */
public class XmlProcessorProperties implements ProcessorProperties {

	/**
	 * Defines the tag handler of an XML Spoon property file.
	 */
	public class Loader extends DefaultHandler {
		boolean isValue = false;

		String name;

		Object value;

		/**
		 * Handdles a tag content.
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (isValue) {
				if ((value == null) || !(value instanceof Collection)) {
					value = new ArrayList<Object>();
				}
				((Collection<Object>) value).add(new String(ch, start, length));
			}
		}

		/**
		 * Handdles a tag end.
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (localName.equals("property")) {
				props.put(name, value);
				value = null;
			} else if (localName.equals("value")) {
				isValue = false;
			}
		}

		/**
		 * Handdles a tag start.
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (localName.equals("property")) {
				name = attributes.getValue("name");
				if (attributes.getValue("value") != null) {
					value = attributes.getValue("value");
				}
			} else if (localName.equals("value")) {
				isValue = true;
			}
		}
	}

	Factory factory;

	String processorName;

	private Map<String, Object> props = new TreeMap<String, Object>();

	public XmlProcessorProperties(Factory factory, String processorName) {
		this.processorName = processorName;
		this.factory = factory;
	}

	public XmlProcessorProperties(Factory factory, String processorName,
			InputStream stream) throws IOException, SAXException {
		this.processorName = processorName;
		this.factory = factory;
		load(stream);
	}

	public void addProperty(String name, Object value) {
		props.put(name, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, String name) {
		if (!props.containsKey(name)) {
			return null;
		}
		if (type.isArray()) {
			return (T) factory.convertArray(type.getComponentType(),
					(Collection<Object>) props.get(name));
		}
		return factory.convert(type, props.get(name));
	}

	public String getProcessorName() {
		return processorName;
	}

	private void load(InputStream stream) throws IOException, SAXException {
		if (stream == null) {
			return;
		}
		XMLReader xr;
		xr = XMLReaderFactory.createXMLReader();
		Loader handler = new Loader();
		xr.setContentHandler(handler);
		xr.parse(new InputSource(stream));
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Properties : \n");

		for (Entry<String, Object> ent : props.entrySet()) {
			buf.append(ent.getKey());
			for (int i = ent.getKey().length(); i < 15; i++) {
				buf.append(" ");
			}
			buf.append(": " + ent.getValue() + "\n");
		}
		return buf.toString();
	}
}
