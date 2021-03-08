/**
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package spoon.smpl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EndToEndTestReader provides parsing and simple validation of the following simple INI-like format:
 * <p>
 * [section]
 * arbitrary text
 * more arbitrary text
 * <p>
 * [anothersection]
 * even more arbitrary text
 * <p>
 * The output is a Map<String, String>, e.g {"section" -> "arbitrary text\nmore arbitrary text",
 * "anothersection" -> "even more arbitrary text"}
 */
public class EndToEndTestReader {
	/**
	 * Read all contents of a plain text file.
	 *
	 * @param path     Path to file
	 * @param encoding Character encoding of file
	 * @return Contents of file
	 * @throws IOException on IO errors
	 * @author https://stackoverflow.com/a/326440
	 */
	private static String readFileContents(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	/**
	 * Parse the contents of the given filename.
	 *
	 * @param filename File to read and parse
	 * @return Map from section titles to contents
	 * @throws IOException
	 */
	public static Map<String, String> readFile(String filename) throws IOException {
		return readString(readFileContents(filename, StandardCharsets.UTF_8));
	}

	/**
	 * Try to parse the given filename, falling back to parsing a default given String on IO errors.
	 *
	 * @param filename File to try reading and parsing
	 * @param fallback Default String to fall back to on IO errors
	 * @return Map from section titles to contents
	 */
	public static Map<String, String> readFileOrDefault(String filename, String fallback) {
		try {
			return readFile(filename);
		} catch (IOException e) {
			return readString(fallback);
		}
	}

	/**
	 * Parse the given String.
	 *
	 * @param data String to parse
	 * @return Map from section titles to contents
	 */
	public static Map<String, String> readString(String data) {
		StringBuilder currentBuilder = new StringBuilder();
		Map<String, StringBuilder> resultBuilder = new HashMap<>();
		Map<String, String> result = new HashMap<>();

		Pattern headerPattern = Pattern.compile("\\[([a-z]+)\\]");

		for (String line : data.split("\n")) {
			Matcher header = headerPattern.matcher(line);

			if (header.matches()) {
				currentBuilder = new StringBuilder();
				resultBuilder.put(header.group(1), currentBuilder);
			} else {
				currentBuilder.append(line).append("\n");
			}
		}

		resultBuilder.forEach((k, v) -> result.put(k, v.toString()));
		return result;
	}

	/**
	 * Validate a parsed result, by checking that it includes the section keys "name", "contract",
	 * "patch", "input" and "expected".
	 *
	 * @param data Data to validate
	 * @return Data if valid, otherwise throws IllegalArgumentException
	 */
	public static Map<String, String> validate(Map<String, String> data) {
		for (String key : Arrays.asList("name", "contract", "patch", "input", "expected")) {
			if (!data.containsKey(key)) {
				throw new IllegalArgumentException();
			}
		}

		return data;
	}
}
