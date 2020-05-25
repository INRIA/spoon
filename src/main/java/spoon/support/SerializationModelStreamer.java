/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;

import spoon.Launcher;
import spoon.reflect.ModelStreamer;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;

/**
 * This class provides a regular Java serialization-based implementation of the
 * model streamer.
 */
public class SerializationModelStreamer implements ModelStreamer {

	/**
	 * Default constructor.
	 */
	public SerializationModelStreamer() {
	}

	@Override
	public void save(Factory f, OutputStream out) throws IOException {
		if (f.getEnvironment().getCompressionType() == CompressionType.GZIP) {
			out = new GZIPOutputStream(out);
		} else if (f.getEnvironment().getCompressionType() == CompressionType.LZMA) {
			out = new LZMACompressorOutputStream(out);
		} else if (f.getEnvironment().getCompressionType() == CompressionType.BZIP2) {
			out = new BZip2CompressorOutputStream(out);
		}
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(out))) {
			oos.writeObject(f);
			oos.flush();
		}
	}

	@Override
	public Factory load(InputStream in) throws IOException {
		try {
			BufferedInputStream buffered = new BufferedInputStream(in);
			try {
				String s = CompressorStreamFactory.detect(buffered);
				if (s.equals(CompressorStreamFactory.GZIP)) {
					in = new GZIPInputStream(buffered);
				} else if (s.equals(CompressorStreamFactory.LZMA)) {
					in = new LZMACompressorInputStream(buffered);
				} else if (s.equals(CompressorStreamFactory.BZIP2)) {
					in = new BZip2CompressorInputStream(buffered);
				}
			} catch (CompressorException e) {
				in = buffered;
			}
			ObjectInputStream ois = new ObjectInputStream(in);
			final Factory f = (Factory) ois.readObject();
			//create query using factory directly
			//because any try to call CtElement#map or CtElement#filterChildren will fail on uninitialized factory
			f.createQuery(f.Module().getAllModules().toArray()).filterChildren(new Filter<CtElement>() {
				@Override
				public boolean matches(CtElement e) {
					e.setFactory(f);
					return false;
				}
			}).list();
			return f;
		} catch (ClassNotFoundException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
			throw new IOException(e.getMessage());
		}
	}

}
