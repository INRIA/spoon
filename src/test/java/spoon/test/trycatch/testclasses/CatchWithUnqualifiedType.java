package spoon.test.trycatch.testclasses;

import java.io.IOException;

public class CatchWithUnqualifiedType {
    public static void main(String[] args) {
        try {
            throw new IOException();
        } catch (IOException e) {
        }
    }
}
