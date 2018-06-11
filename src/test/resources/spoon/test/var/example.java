package fr.inria.sandbox;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public String toString() {
        var mySubstring = "bla";

        try (var myFile = new FileReader(new File("/tmp/myfile"));) {
            mySubstring += myFile.toString();
        } catch (IOException e) {
            mySubstring += "error";
        }

        switch (mySubstring) {
            case "bla":
                var myOtherString = "loop";
                return myOtherString;
        }

        for (var i = 0; i < mySubstring.length(); i++) {
            return mySubstring;
        }

        return mySubstring;
    }
}
