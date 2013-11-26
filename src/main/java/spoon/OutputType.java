package spoon;

/**
* Types of output
*/
public enum OutputType {
    /** analysis only, models are not pretty-printed to disk */
    nooutput,

    /** one file per top-level class */
    classes,

    /** same compilation units as input */
    compilationunits
}
