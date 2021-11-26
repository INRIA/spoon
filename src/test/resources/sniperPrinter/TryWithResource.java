package sniperPrinter;

import java.util.zip.ZipFile;
import java.io.BufferedWriter;
import java.nio.file.Files.newBufferedWriter;

public class TryWithResource {
    public void resourcePrinting() {
        try (ZipFile zf = new ZipFile(zipFileName);
             BufferedWriter writer = newBufferedWriter(outputFilePath, charset)) { }
        try (ZipFile zf = new ZipFile(zipFileName);
             BufferedWriter writer = newBufferedWriter(outputFilePath, charset);) { }
    }
}
