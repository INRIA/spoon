/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipFolderExtractTest {

	/**
	 * Creates a zip file containing a directory entry and a file entry, then verifies
	 * that extract() correctly materialises both on disk.
	 */
	@Test
	void extractCreatesFilesAndDirectories(@TempDir Path tempDir) throws IOException {
		// contract: extract() writes directory entries and file entries from the zip to destDir
		Path zipPath = tempDir.resolve("sample.zip");
		byte[] content = "hello spoon".getBytes(StandardCharsets.UTF_8);

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
			// directory entry
			ZipEntry dirEntry = new ZipEntry("subdir/");
			zos.putNextEntry(dirEntry);
			zos.closeEntry();

			// file entry inside the directory
			ZipEntry fileEntry = new ZipEntry("subdir/hello.txt");
			zos.putNextEntry(fileEntry);
			zos.write(content);
			zos.closeEntry();

			// file entry at root of zip
			ZipEntry rootEntry = new ZipEntry("root.txt");
			zos.putNextEntry(rootEntry);
			zos.write("root content".getBytes(StandardCharsets.UTF_8));
			zos.closeEntry();
		}

		Path destDir = tempDir.resolve("extracted");
		destDir.toFile().mkdirs();

		ZipFolder zipFolder = new ZipFolder(zipPath.toFile());
		zipFolder.extract(destDir.toFile());

		assertTrue(destDir.resolve("subdir").toFile().isDirectory(),
				"subdir/ directory entry should be extracted as a directory");
		assertTrue(destDir.resolve("subdir/hello.txt").toFile().isFile(),
				"subdir/hello.txt should be extracted as a file");
		assertTrue(destDir.resolve("root.txt").toFile().isFile(),
				"root.txt should be extracted as a file");

		byte[] extractedBytes = java.nio.file.Files.readAllBytes(destDir.resolve("subdir/hello.txt"));
		org.junit.jupiter.api.Assertions.assertArrayEquals(content, extractedBytes,
				"Extracted file content should match the original");
	}

	/**
	 * Creates a zip file containing a path-traversal (zip-slip) entry and verifies
	 * that extract() does NOT write anything outside destDir.
	 */
	@Test
	void extractPreventsZipSlip(@TempDir Path tempDir) throws IOException {
		// contract: extract() must not write files outside the destination directory (zip-slip protection)
		Path zipPath = tempDir.resolve("evil.zip");

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
			// Attempt a path-traversal attack
			ZipEntry maliciousEntry = new ZipEntry("../../malicious.txt");
			zos.putNextEntry(maliciousEntry);
			zos.write("pwned".getBytes(StandardCharsets.UTF_8));
			zos.closeEntry();
		}

		Path destDir = tempDir.resolve("safe");
		destDir.toFile().mkdirs();

		ZipFolder zipFolder = new ZipFolder(zipPath.toFile());
		// The implementation catches SpoonException internally and only logs it,
		// so we verify the side-effect: the malicious file must NOT have been created.
		zipFolder.extract(destDir.toFile());

		File maliciousFile = tempDir.getParent() != null
				? tempDir.getParent().resolve("malicious.txt").toFile()
				: new File(System.getProperty("java.io.tmpdir"), "malicious.txt");

		assertFalse(maliciousFile.exists(),
				"Zip-slip entry must not be written outside of destDir");
		// Also ensure nothing was placed inside destDir from that entry
		assertFalse(destDir.resolve("malicious.txt").toFile().exists(),
				"Malicious entry should not appear inside destDir either");
	}
}
