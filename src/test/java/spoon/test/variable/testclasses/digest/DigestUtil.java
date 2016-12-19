/*
 * Copyright (C) 2006-2016 INRIA and contributors
 *  Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and abiding by the rules of distribution of free software. You can use, modify and/or redistribute the software under the terms of the CeCILL-C license as circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C license and that you accept its terms.
 */

package spoon.test.variable.testclasses.digest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by urli on 19/12/2016.
 */
public class DigestUtil {
    private static final int STREAM_BUFFER_LENGTH = 1024;

    public static MessageDigest getDigest(final String algorithm) {
        return new MessageDigest();
    }

    public static MessageDigest getMd2Digest() {
        return getDigest(MessageDigest.MD2);
    }
    public static MessageDigest getMd5Digest() {
        return getDigest(MessageDigest.MD5);
    }

    public static byte[] digest(final java.security.MessageDigest messageDigest, final byte[] data) {
        return messageDigest.digest(data);
    }

    public static byte[] digest(final java.security.MessageDigest messageDigest, final java.nio.ByteBuffer data) {
        messageDigest.update(data);
        return messageDigest.digest();
    }

    public static MessageDigest updateDigest(final MessageDigest digest, final InputStream data) throws IOException {
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return digest;
    }
}
