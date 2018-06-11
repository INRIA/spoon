package fr.inria.sandbox;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public String toString() {
        // 0
        var mySubstring = "bla";

        // 1 -> not inferred
        String anotherSub = "bidule";

        // 2
        try (var myFile = new FileReader(new File("/tmp/myfile"));

             // 3 -> not inferred
             FileReader anotherOne = new FileReader(new File("/other/path"))) {
            mySubstring += myFile.toString();
        } catch (IOException e) {
            mySubstring += "error";
        }

        switch (mySubstring) {
            case "bla":

                // 4
                var myboolean = true;

                // 5
                boolean another = false;

                return "";
        }

        // 6
        for (var i = 0; i < mySubstring.length(); i++) {

            // 7 -> not inferred
            for (int j = i; j < 10; j++) {
                return mySubstring;
            }
        }

        return mySubstring;
    }
}
