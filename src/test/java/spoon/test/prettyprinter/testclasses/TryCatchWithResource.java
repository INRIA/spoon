package spoon.test.prettyprinter.testclasses;

import org.apache.commons.io.output.ThresholdingOutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.compress.utils.IOUtils.copy;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 22/04/2022
 */
public class TryCatchWithResource {

    public static byte[] toByteArray(final InputStream inputStream) throws IOException {
        // We use a ThresholdingOutputStream to avoid reading AND writing more than Integer.MAX_VALUE.
        try (final UnsynchronizedByteArrayOutputStream ubaOutput = new UnsynchronizedByteArrayOutputStream();
             final ThresholdingOutputStream thresholdOuput = new ThresholdingOutputStream(Integer.MAX_VALUE, os -> {
                 throw new IllegalArgumentException(
                         String.format("Cannot read more than %,d into a byte array", Integer.MAX_VALUE));
             }, os -> ubaOutput)) {
            copy(inputStream, thresholdOuput);
            return ubaOutput.toByteArray();
        }
    }

}
