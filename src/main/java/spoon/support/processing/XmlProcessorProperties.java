/**
 * Copyright (C) 2006-2015 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import spoon.processing.ProcessorProperties;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

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
		 * Handles a tag content.
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (isValue) {
				if ((value == null) || !(value instanceof Collection)) {
					value = new ArrayList<Object>();
				}
				((Collection<Object>) value).add(new String(ch, start, length));
			}
		}

		/**
		 * Handles a tag end.
		 */
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("property".equals(localName)) {
				props.put(name, value);
				value = null;
			} else if ("value".equals(localName)) {
				isValue = false;
			}
		}

		/**
		 * Handles a tag start.
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if ("property".equals(localName)) {
				name = attributes.getValue("name");
				if (attributes.getValue("value") != null) {
					value = attributes.getValue("value");
				}
			} else if ("value".equals(localName)) {
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

	public XmlProcessorProperties(Factory factory, String processorName, InputStream stream)
	throws IOException, SAXException {
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
			return (T) convertArray(type.getComponentType(), (Collection<Object>) props.get(name));
		}
		return convert(type, props.get(name));
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

	/**
	 * Converts an object <code>o</code> into an object or a {@link CtReference}
	 * of type <code>type</code>.
	 *
	 * @param <T>
	 * 		the actual type of the object
	 * @param type
	 * 		the type to convert the object into
	 * @param o
	 * 		the object to be converted
	 * @return a primitive object of type T, or a reference
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T convert(Class<T> type, Object o) {
		if (o == null) {
			return null;
		}
		if (type == boolean.class) {
			return (T) new Boolean(o.toString());
		}
		if (type == byte.class) {
			return (T) new Byte(o.toString());
		}
		if (type == char.class) {
			return (T) new Character(o.toString().charAt(0));
		}
		if (type == double.class) {
			return (T) new Double(o.toString());
		}
		if (type == float.class) {
			return (T) new Float(o.toString());
		}
		if (type == int.class) {
			return (T) new Integer(o.toString());
		}
		if (type == long.class) {
			return (T) new Long(o.toString());
		}
		if (CtTypeReference.class.isAssignableFrom(type)) {
			return (T) factory.Type().createReference(o.toString());
		}
		if (CtExecutableReference.class.isAssignableFrom(type)) {
			return (T) factory.Executable().createReference(o.toString());
		}
		if (CtFieldReference.class.isAssignableFrom(type)) {
			return (T) factory.Field().createReference(o.toString());
		}
		if (CtPackageReference.class.isAssignableFrom(type)) {
			return (T) factory.Package().createReference(o.toString());
		}
		if (type.isEnum()) {
			return (T) java.lang.Enum.valueOf((Class<Enum>) type, o.toString());
		}
		return (T) o.toString();
	}

	/**
	 * Converts a collection of object into an array of type <code>type</code>.
	 *
	 * @param <T>
	 * 		the actual type of the array
	 * @param type
	 * 		the type to convert the object into
	 * @param val
	 * 		the collection to be converted
	 * @return an array of type T
	 */
	@SuppressWarnings("unchecked")
	public <T> T convertArray(Class<T> type, Collection<Object> val) {
		if (type.equals(boolean.class)) {
			boolean[] ret = new boolean[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(boolean.class, o);
			}
			return (T) ret;
		} else if (type.equals(byte.class)) {
			byte[] ret = new byte[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(byte.class, o);
			}
			return (T) ret;
		} else if (type.equals(char.class)) {
			char[] ret = new char[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(char.class, o);
			}
			return (T) ret;
		} else if (type.equals(double.class)) {
			double[] ret = new double[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(double.class, o);
			}
			return (T) ret;
		} else if (type.equals(float.class)) {
			float[] ret = new float[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(float.class, o);
			}
			return (T) ret;
		} else if (type.equals(int.class)) {
			int[] ret = new int[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(int.class, o);
			}
			return (T) ret;
		} else if (type.equals(long.class)) {
			long[] ret = new long[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(long.class, o);
			}
			return (T) ret;
		} else if (type.equals(String.class)) {
			String[] ret = new String[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(String.class, o);
			}
			return (T) ret;
		} else if (CtPackageReference.class.isAssignableFrom(type)) {
			CtPackageReference[] ret = new CtPackageReference[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(CtPackageReference.class, o);
			}
			return (T) ret;
		} else if (CtTypeReference.class.isAssignableFrom(type)) {
			CtTypeReference<?>[] ret = new CtTypeReference[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(CtTypeReference.class, o);
			}
			return (T) ret;
		} else if (CtFieldReference.class.isAssignableFrom(type)) {
			CtFieldReference<?>[] ret = new CtFieldReference[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(CtFieldReference.class, o);
			}
			return (T) ret;
		} else if (CtExecutableReference.class.isAssignableFrom(type)) {
			CtExecutableReference<?>[] ret = new CtExecutableReference[val.size()];
			int i = 0;
			for (Object o : val) {
				ret[i++] = convert(CtExecutableReference.class, o);
			}
			return (T) ret;
		} else if (type.isEnum()) {
			Collection<Enum<?>> ret = new ArrayList<Enum<?>>();
			for (Object o : val) {
				ret.add((Enum<?>) convert(type, o));
			}
			return (T) ret.toArray((Enum[]) Array.newInstance(type, 0));
		}
		return null;
	}

}
