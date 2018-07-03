/*
 * FilterEncoder
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package org.tukaani.xz;

interface FilterEncoder extends FilterCoder {
    long getFilterID();
    byte[] getFilterProps();
    boolean supportsFlushing();
    FinishableOutputStream getOutputStream(FinishableOutputStream out,
                                           ArrayCache arrayCache);
}
