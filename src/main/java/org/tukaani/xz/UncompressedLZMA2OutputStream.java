/*
 * UncompressedLZMA2OutputStream
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package org.tukaani.xz;

import java.io.DataOutputStream;
import java.io.IOException;

class UncompressedLZMA2OutputStream extends FinishableOutputStream {
    private final ArrayCache arrayCache;

    private FinishableOutputStream out;
    private final DataOutputStream outData;

    private final byte[] uncompBuf;
    private int uncompPos = 0;
    private boolean dictResetNeeded = true;

    private boolean finished = false;
    private IOException exception = null;

    private final byte[] tempBuf = new byte[1];

    static int getMemoryUsage() {
        // uncompBuf + a little extra
        return 70;
    }

    UncompressedLZMA2OutputStream(FinishableOutputStream out,
                                  ArrayCache arrayCache) {
        if (out == null)
            throw new NullPointerException();

        this.out = out;
        outData = new DataOutputStream(out);

        // We only allocate one array from the cache. We will call
        // putArray directly in writeEndMarker and thus we don't use
        // ResettableArrayCache here.
        this.arrayCache = arrayCache;
        uncompBuf = arrayCache.getByteArray(
                LZMA2OutputStream.COMPRESSED_SIZE_MAX, false);
    }

    public void write(int b) throws IOException {
        tempBuf[0] = (byte)b;
        write(tempBuf, 0, 1);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len < 0 || off + len > buf.length)
            throw new IndexOutOfBoundsException();

        if (exception != null)
            throw exception;

        if (finished)
            throw new XZIOException("Stream finished or closed");

        try {
            while (len > 0) {
                int copySize = Math.min(LZMA2OutputStream.COMPRESSED_SIZE_MAX
                                        - uncompPos, len);
                System.arraycopy(buf, off, uncompBuf, uncompPos, copySize);
                len -= copySize;
                uncompPos += copySize;

                if (uncompPos == LZMA2OutputStream.COMPRESSED_SIZE_MAX)
                    writeChunk();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        }
    }

    private void writeChunk() throws IOException {
        outData.writeByte(dictResetNeeded ? 0x01 : 0x02);
        outData.writeShort(uncompPos - 1);
        outData.write(uncompBuf, 0, uncompPos);
        uncompPos = 0;
        dictResetNeeded = false;
    }

    private void writeEndMarker() throws IOException {
        if (exception != null)
            throw exception;

        if (finished)
            throw new XZIOException("Stream finished or closed");

        try {
            if (uncompPos > 0)
                writeChunk();

            out.write(0x00);
        } catch (IOException e) {
            exception = e;
            throw e;
        }

        finished = true;
        arrayCache.putArray(uncompBuf);
    }

    public void flush() throws IOException {
        if (exception != null)
            throw exception;

        if (finished)
            throw new XZIOException("Stream finished or closed");

        try {
            if (uncompPos > 0)
                writeChunk();

            out.flush();
        } catch (IOException e) {
            exception = e;
            throw e;
        }
    }

    public void finish() throws IOException {
        if (!finished) {
            writeEndMarker();

            try {
                out.finish();
            } catch (IOException e) {
                exception = e;
                throw e;
            }
        }
    }

    public void close() throws IOException {
        if (out != null) {
            if (!finished) {
                try {
                    writeEndMarker();
                } catch (IOException e) {}
            }

            try {
                out.close();
            } catch (IOException e) {
                if (exception == null)
                    exception = e;
            }

            out = null;
        }

        if (exception != null)
            throw exception;
    }
}
