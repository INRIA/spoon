/*
 * Copyright (C) 2014 Valerio Bozzolan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package anupam.acrylic;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class Places {
	public static File getScreenshotFolder() {
		File path = new File(Environment.getExternalStorageDirectory(),
				"/Acrylic Paint/");
		path.mkdirs();

		return path;
	}

	public static File getCameraTempFolder() {
		File path = new File(Environment.getExternalStorageDirectory(),
				"/Acrylic Paint/Temp/");
		path.mkdirs();
		// this folder should not be scanned
		File noScanning = new File(path, ".nomedia");
		if (!noScanning.exists())
			try {
				noScanning.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return path;
	}

	public static File getCameraTempFile() {
		return new File(getCameraTempFolder(), "temp.jpg");
	}
}
