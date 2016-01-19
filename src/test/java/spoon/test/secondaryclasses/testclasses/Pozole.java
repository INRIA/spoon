/*
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

package spoon.test.secondaryclasses.testclasses;

import javax.swing.JFrame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;

public class Pozole {
	private static final Object CONFLICT_HOOK = new JFrame(new GraphicsConfiguration() {
		@Override
		public GraphicsDevice getDevice() {
			return null;
		}

		@Override
		public ColorModel getColorModel() {
			return null;
		}

		@Override
		public ColorModel getColorModel(int i) {
			return new ColorModel(i) {
				@Override
				public int getRed(int i) {
					return i;
				}

				@Override
				public int getGreen(int i) {
					return 0;
				}

				@Override
				public int getBlue(int i) {
					return 0;
				}

				@Override
				public int getAlpha(int i) {
					return 0;
				}
			};
		}

		@Override
		public AffineTransform getDefaultTransform() {
			return null;
		}

		@Override
		public AffineTransform getNormalizingTransform() {
			return null;
		}

		@Override
		public Rectangle getBounds() {
			return null;
		}
	});
}
