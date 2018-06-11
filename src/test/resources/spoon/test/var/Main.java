package fr.inria.sandbox;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public String toString() {
        // first local var -> not inferred
        var mySubstring = "bla";

        // second local var -> inferred
        try (var myFile = new FileReader(new File("/tmp/myfile"));) {
            mySubstring += myFile.toString();
        } catch (IOException e) {
            mySubstring += "error";
        }

        switch (mySubstring) {
            case "bla":

                // third local var -> inferred
                var myOtherString = "loop";
                return myOtherString;
        }

        // fourth local var -> inferred
        for (var i = 0; i < mySubstring.length(); i++) {
            return mySubstring;
        }

        return mySubstring;
    }
}
