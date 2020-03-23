/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

/**
 * A default dumb implementation of {@link SourceFragmentPrinter}, which only prints the given PrinterEvent.
 */
public class DefaultSourceFragmentPrinter implements SourceFragmentPrinter {
	public static final DefaultSourceFragmentPrinter INSTANCE = new DefaultSourceFragmentPrinter();

	private DefaultSourceFragmentPrinter() {
	}

	@Override
	public void onPush() {
	}

	@Override
	public void print(PrinterEvent event) {
		event.print();
	}

	@Override
	public int update(PrinterEvent event) {
		return -1;
	}

	@Override
	public void onFinished() {
	}

	@Override
	public boolean knowsHowToPrint(PrinterEvent event) {
		return true;
	}

}
