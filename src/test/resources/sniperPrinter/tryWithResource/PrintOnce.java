package sniperPrinter.tryWithResource;

import java.util.zip.ZipFile;
import java.io.BufferedWriter;
import java.nio.file.Files.newBufferedWriter;

public class PrintOnce {
    public void resourcePrinting() {
        try (ZipFile zf = new ZipFile(zipFileName);
             BufferedWriter writer = newBufferedWriter(outputFilePath, charset)) { }
    }
}
