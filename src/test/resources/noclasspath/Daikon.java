// Main routine for Daikon invariant detector
// For documentation, see file doc/daikon.html in the distribution.

package daikon;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import checkers.quals.Interned;

import utilMDE.Assert;
import utilMDE.FileIOException;
import utilMDE.Fmt;
import utilMDE.Stopwatch;
import utilMDE.TextFile;
import utilMDE.UtilMDE;
import daikon.config.Configuration;
import daikon.derive.Derivation;
import daikon.inv.Equality;
import daikon.inv.Invariant;
import daikon.inv.OutputFormat;
import daikon.inv.binary.sequenceScalar.Member;
import daikon.inv.binary.sequenceScalar.MemberFloat;
import daikon.inv.binary.sequenceScalar.SeqFloatEqual;
import daikon.inv.binary.sequenceScalar.SeqFloatGreaterEqual;
import daikon.inv.binary.sequenceScalar.SeqFloatGreaterThan;
import daikon.inv.binary.sequenceScalar.SeqFloatLessEqual;
import daikon.inv.binary.sequenceScalar.SeqFloatLessThan;
import daikon.inv.binary.sequenceScalar.SeqIntEqual;
import daikon.inv.binary.sequenceScalar.SeqIntGreaterEqual;
import daikon.inv.binary.sequenceScalar.SeqIntGreaterThan;
import daikon.inv.binary.sequenceScalar.SeqIntLessEqual;
import daikon.inv.binary.sequenceScalar.SeqIntLessThan;
import daikon.inv.binary.sequenceString.MemberString;
import daikon.inv.binary.twoScalar.FloatEqual;
import daikon.inv.binary.twoScalar.FloatGreaterEqual;
import daikon.inv.binary.twoScalar.FloatGreaterThan;
import daikon.inv.binary.twoScalar.FloatLessEqual;
import daikon.inv.binary.twoScalar.FloatLessThan;
import daikon.inv.binary.twoScalar.FloatNonEqual;
import daikon.inv.binary.twoScalar.IntEqual;
import daikon.inv.binary.twoScalar.IntGreaterEqual;
import daikon.inv.binary.twoScalar.IntGreaterThan;
import daikon.inv.binary.twoScalar.IntLessEqual;
import daikon.inv.binary.twoScalar.IntLessThan;
import daikon.inv.binary.twoScalar.IntNonEqual;
import daikon.inv.binary.twoScalar.LinearBinary;
import daikon.inv.binary.twoScalar.LinearBinaryFloat;
import daikon.inv.binary.twoScalar.NumericFloat;
import daikon.inv.binary.twoScalar.NumericInt;
import daikon.inv.binary.twoSequence.PairwiseFloatEqual;
import daikon.inv.binary.twoSequence.PairwiseFloatGreaterEqual;
import daikon.inv.binary.twoSequence.PairwiseFloatGreaterThan;
import daikon.inv.binary.twoSequence.PairwiseFloatLessEqual;
import daikon.inv.binary.twoSequence.PairwiseFloatLessThan;
import daikon.inv.binary.twoSequence.PairwiseIntEqual;
import daikon.inv.binary.twoSequence.PairwiseIntGreaterEqual;
import daikon.inv.binary.twoSequence.PairwiseIntGreaterThan;
import daikon.inv.binary.twoSequence.PairwiseIntLessEqual;
import daikon.inv.binary.twoSequence.PairwiseIntLessThan;
import daikon.inv.binary.twoSequence.PairwiseLinearBinary;
import daikon.inv.binary.twoSequence.PairwiseLinearBinaryFloat;
import daikon.inv.binary.twoSequence.PairwiseNumericFloat;
import daikon.inv.binary.twoSequence.PairwiseNumericInt;
import daikon.inv.binary.twoSequence.Reverse;
import daikon.inv.binary.twoSequence.ReverseFloat;
import daikon.inv.binary.twoSequence.SeqSeqFloatEqual;
import daikon.inv.binary.twoSequence.SeqSeqFloatGreaterEqual;
import daikon.inv.binary.twoSequence.SeqSeqFloatGreaterThan;
import daikon.inv.binary.twoSequence.SeqSeqFloatLessEqual;
import daikon.inv.binary.twoSequence.SeqSeqFloatLessThan;
import daikon.inv.binary.twoSequence.SeqSeqIntEqual;
import daikon.inv.binary.twoSequence.SeqSeqIntGreaterEqual;
import daikon.inv.binary.twoSequence.SeqSeqIntGreaterThan;
import daikon.inv.binary.twoSequence.SeqSeqIntLessEqual;
import daikon.inv.binary.twoSequence.SeqSeqIntLessThan;
import daikon.inv.binary.twoSequence.SeqSeqStringEqual;
import daikon.inv.binary.twoSequence.SeqSeqStringGreaterEqual;
import daikon.inv.binary.twoSequence.SeqSeqStringGreaterThan;
import daikon.inv.binary.twoSequence.SeqSeqStringLessEqual;
import daikon.inv.binary.twoSequence.SeqSeqStringLessThan;
import daikon.inv.binary.twoSequence.SubSequence;
import daikon.inv.binary.twoSequence.SubSequenceFloat;
import daikon.inv.binary.twoSequence.SubSet;
import daikon.inv.binary.twoSequence.SubSetFloat;
import daikon.inv.binary.twoSequence.SuperSequence;
import daikon.inv.binary.twoSequence.SuperSequenceFloat;
import daikon.inv.binary.twoSequence.SuperSet;
import daikon.inv.binary.twoSequence.SuperSetFloat;
import daikon.inv.binary.twoString.StringEqual;
import daikon.inv.binary.twoString.StringGreaterEqual;
import daikon.inv.binary.twoString.StringGreaterThan;
import daikon.inv.binary.twoString.StringLessEqual;
import daikon.inv.binary.twoString.StringLessThan;
import daikon.inv.binary.twoString.StringNonEqual;
import daikon.inv.ternary.threeScalar.FunctionBinary;
import daikon.inv.ternary.threeScalar.FunctionBinaryFloat;
import daikon.inv.ternary.threeScalar.LinearTernary;
import daikon.inv.ternary.threeScalar.LinearTernaryFloat;
import daikon.inv.unary.scalar.LowerBound;
import daikon.inv.unary.scalar.LowerBoundFloat;
import daikon.inv.unary.scalar.Modulus;
import daikon.inv.unary.scalar.NonModulus;
import daikon.inv.unary.scalar.NonZero;
import daikon.inv.unary.scalar.NonZeroFloat;
import daikon.inv.unary.scalar.OneOfFloat;
import daikon.inv.unary.scalar.OneOfScalar;
import daikon.inv.unary.scalar.RangeFloat;
import daikon.inv.unary.scalar.RangeInt;
import daikon.inv.unary.scalar.UpperBound;
import daikon.inv.unary.scalar.UpperBoundFloat;
import daikon.inv.unary.sequence.CommonFloatSequence;
import daikon.inv.unary.sequence.CommonSequence;
import daikon.inv.unary.sequence.EltLowerBound;
import daikon.inv.unary.sequence.EltLowerBoundFloat;
import daikon.inv.unary.sequence.EltNonZero;
import daikon.inv.unary.sequence.EltNonZeroFloat;
import daikon.inv.unary.sequence.EltOneOf;
import daikon.inv.unary.sequence.EltOneOfFloat;
import daikon.inv.unary.sequence.EltRangeFloat;
import daikon.inv.unary.sequence.EltRangeInt;
import daikon.inv.unary.sequence.EltUpperBound;
import daikon.inv.unary.sequence.EltUpperBoundFloat;
import daikon.inv.unary.sequence.EltwiseFloatEqual;
import daikon.inv.unary.sequence.EltwiseFloatGreaterEqual;
import daikon.inv.unary.sequence.EltwiseFloatGreaterThan;
import daikon.inv.unary.sequence.EltwiseFloatLessEqual;
import daikon.inv.unary.sequence.EltwiseFloatLessThan;
import daikon.inv.unary.sequence.EltwiseIntEqual;
import daikon.inv.unary.sequence.EltwiseIntGreaterEqual;
import daikon.inv.unary.sequence.EltwiseIntGreaterThan;
import daikon.inv.unary.sequence.EltwiseIntLessEqual;
import daikon.inv.unary.sequence.EltwiseIntLessThan;
import daikon.inv.unary.sequence.NoDuplicates;
import daikon.inv.unary.sequence.NoDuplicatesFloat;
import daikon.inv.unary.sequence.OneOfFloatSequence;
import daikon.inv.unary.sequence.OneOfSequence;
import daikon.inv.unary.sequence.SeqIndexFloatEqual;
import daikon.inv.unary.sequence.SeqIndexFloatGreaterEqual;
import daikon.inv.unary.sequence.SeqIndexFloatGreaterThan;
import daikon.inv.unary.sequence.SeqIndexFloatLessEqual;
import daikon.inv.unary.sequence.SeqIndexFloatLessThan;
import daikon.inv.unary.sequence.SeqIndexFloatNonEqual;
import daikon.inv.unary.sequence.SeqIndexIntEqual;
import daikon.inv.unary.sequence.SeqIndexIntGreaterEqual;
import daikon.inv.unary.sequence.SeqIndexIntGreaterThan;
import daikon.inv.unary.sequence.SeqIndexIntLessEqual;
import daikon.inv.unary.sequence.SeqIndexIntLessThan;
import daikon.inv.unary.sequence.SeqIndexIntNonEqual;
import daikon.inv.unary.string.OneOfString;
import daikon.inv.unary.string.PrintableString;
import daikon.inv.unary.stringsequence.CommonStringSequence;
import daikon.inv.unary.stringsequence.EltOneOfString;
import daikon.inv.unary.stringsequence.OneOfStringSequence;
import daikon.split.ContextSplitterFactory;
import daikon.split.SpinfoFileParser;
import daikon.split.Splitter;
import daikon.split.SplitterFactory;
import daikon.split.SplitterList;
import daikon.suppress.NIS;
import daikon.suppress.NIS.SuppressionProcessor;

/**
 * The "main" method is the main entry point for the Daikon invariant detector.
 * The "mainHelper" method is the entry point, when called programmatically.
 **/
public final class Daikon {

  private Daikon() {
    throw new Error("do not instantiate");
  }

  /**
   * The amount of time to wait between updates of the progress
   * display, measured in milliseconds. A value of -1 means not to
   * print the progress display at all.
   **/
  public static int dkconfig_progress_delay = 1000;

  /** If true, show stack traces for errors such as file format errors **/
  public static boolean dkconfig_show_stack_trace = false;

  public static final String release_version = "4.3.1";
  public static final String release_date = "August 2, 2007";
  public static final String release_string =
    "Daikon version "
      + release_version
      + ", released "
      + release_date
      + "; http://pag.csail.mit.edu/daikon.";

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  Controls whether conditional program points
   * are displayed.
   **/
  public static boolean dkconfig_output_conditionals = true;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  Controls whether invariants are reported over floating-point
   * values.
   **/
  public static boolean dkconfig_enable_floats = true;

  /**
   * Boolean.  Just print the total number of possible invariants
   * and exit.
   */
  public static boolean dkconfig_calc_possible_invs;

  /**
   * Integer. Percentage of program points to process.  All program points
   * are sorted by name, and all samples for
   * the first <code>ppt_perc</code> program points are processed.
   * A percentage of 100 matches all program points.
   */
  public static int dkconfig_ppt_perc = 100;

  /**
   * Boolean.  Controls whether or not the total samples read and processed
   * are printed at the end of processing.
   */
  public static boolean dkconfig_print_sample_totals = false;

  // All these variables really need to be organized better.

  public static final String lineSep = Global.lineSep;

  /**
   * Boolean.  Controls whether or not splitting based on the built-in
   * splitting rules is disabled.  The built-in rules look for implications
   * based on boolean return values and also when there are exactly two
   * exit points from a method.
   **/
  public static boolean dkconfig_disable_splitting = false;

  /**
   * Boolean.  Controls whether or not processing information is printed out.
   * Setting this variable to true also automatically sets
   * <code>progress_delay</code> to -1.
   **/
  public static boolean dkconfig_quiet = false;

  // Change this at your peril; high costs in time and space for "false",
  // because so many more invariants get instantiated.
  public static final boolean check_program_types = true;

  // Problem with setting this to true:
  //  get no invariants over any value that can ever be missing
  // Problem with setting this to false:
  //  due to differrent number of samples, IsEqualityComparison is
  //  non-transitive (that is specially handled in the code)
  public static final boolean invariants_check_canBeMissing = false;

  // Specialized version for array elements; only examined if
  // invariants_check_canBeMissing is false
  public static final boolean invariants_check_canBeMissing_arrayelt = true;

  public static final boolean disable_modbit_check_message = false;
  // Not a good idea to set this to true, as it is too easy to ignore the
  // warnings and the modbit problem can cause an error later.
  public static final boolean disable_modbit_check_error = false;

  // When true, don't print textual output.
  public static boolean no_text_output = false;

  // When true, show how much time each program point took.
  // Has no effect unless no_text_output is true.
  public static boolean show_progress = false;

  /**
   * Whether to use the "new" equality set mechanism for handling
   * equality, using canonicals to have instantiation of invariants
   * only over equality sets.
   **/
  public static boolean use_equality_optimization = true;

  /**
   * Whether to use the dynamic constants optimization.  This
   * optimization doesn't instantiate invariants over constant
   * variables (i.e., that that have only seen one value).  When the
   * variable receives a second value, invariants are instantiated and
   * are given the sample representing the previous constant value.
   **/
  public static boolean dkconfig_use_dynamic_constant_optimization = true;

  /**
   * Boolean.  Controls whether the Daikon optimizations (equality
   * sets, suppressions) are undone at the end to create a more
   * complete set of invariants.  Output does not include
   * conditional program points, implications, reflexive and
   * partially reflexive invariants.
   **/
  public static boolean dkconfig_undo_opts = false;

  /**
   * Boolean.  Indicates to Daikon classes and methods that the methods
   * calls should be compatible to DaikonSimple because Daikon and DaikonSimple share
   * methods.  Default value is 'false'.
   **/
  public static boolean using_DaikonSimple = false;

  /**
   * If "always", then invariants are always guarded.
   * If "never", then invariants are never guarded.
   * If "missing", then invariants are guarded only for variables that
   * were missing (``can be missing'') in the dtrace (the observed executions).
   * <p>
   * Guarding means that predicates are attached to invariants ensuring
   * their values can be dereferenced.  For instance, if <code>a.b</code>
   * can be missing, and
   * <samp>a.b == 5</samp>
   * is an invariant, then it is more properly written as
   * <samp>(a != null) ==> (a.b == 5)</samp>.
   **/
  // Perhaps a better default would be "missing".
  public static /*@Interned*/ String dkconfig_guardNulls = "default";

  /**
   * When true compilation errors during splitter file generation
   * will not be reported to the user.
   */
  public static boolean dkconfig_suppressSplitterErrors = false;

  /**
   * Whether to associate the program points in a dataflow hierarchy,
   * as via Nimmer's thesis.  Deactivate only for languages and
   * analyses where flow relation is nonsensical.
   **/
  public static boolean use_dataflow_hierarchy = true;

  /**
   * Whether to use the bottom up implementation of the dataflow
   * hierarchy.  This mechanism builds invariants initially
   * only at the leaves of the partial order.  Upper points are
   * calculated by joining the invariants from each of their children
   * points.
   **/
  // public static boolean dkconfig_df_bottom_up = true;

  // When true, don't print invariants when their controlling ppt
  // already has them.  For example, this is the case for invariants
  // in public methods which are already given as part of the object
  // invariant for that class.
  public static boolean suppress_implied_controlled_invariants = true;

  // When true, don't print EXIT invariants over strictly orig()
  // variables when the corresponding entry ppt already has the
  // invariant.
  public static boolean suppress_implied_postcondition_over_prestate_invariants =
    false;

  // When true, use the Simplify theorem prover (not part of Daikon)
  // to locate logically redundant invariants, and flag them as
  // redundant, so that they are removed from the printed output.
  public static boolean suppress_redundant_invariants_with_simplify = false;

  // Set what output style to use.  DAIKON is the default; ESC style
  // is based on JML; SIMPLIFY style uses first order logical
  // expressions with lots of parens
  public static OutputFormat output_format = OutputFormat.DAIKON;
  // public static OutputFormat output_format = OutputFormat.ESCJAVA;
  // public static OutputFormat output_format = OutputFormat.DBCJAVA;
  // public static OutputFormat output_format = OutputFormat.SIMPLIFY;

  // When true, output numbers of values and samples (also names of variables)
  public static boolean output_num_samples = false;

  public static boolean ignore_comparability = false;

  // Controls which program points/variables are used/ignored.
  public static Pattern ppt_regexp;
  public static Pattern ppt_omit_regexp;
  public static Pattern var_regexp;
  public static Pattern var_omit_regexp;

  /**
   * When true, perform detailed internal checking.
   * These are essentially additional, possibly costly assert statements.
   */
  public static boolean dkconfig_internal_check = false;

  /**
   * If set, only ppts less than ppt_max_name are included.  Used by the
   * configuration option dkconfig_ppt_percent to only work on a specified
   * percent of the ppts.
   */
  public static String ppt_max_name = null;

  // The invariants detected will be serialized and written to this
  // file.
  public static File inv_file;

  // Whether we want the memory monitor activated
  private static boolean use_mem_monitor = false;

  /**
   * Whether Daikon should print its version number and date.
   **/
  public static boolean noversion_output = false;

  /**
   * Whether Daikon is in its inferencing loop.  Used only for
   * assertion checks.
   **/
  public static boolean isInferencing = false;

  /**
   * When true, omit certain invariants from the output .inv
   * file. Generally these are invariants that wouldn't be printed in
   * any case; but by default, they're retained in the .inv file in
   * case they would be useful for later processing. (For instance, we
   * might at some point in the future support resuming processing
   * with more data from an .inv file). These invariants can increase
   * the size of the .inv file, though, so when only limited further
   * processing is needed, it can save space to omit them.
   **/
  public static boolean omit_from_output = false;

  /**
   * An array of flags, indexed by characters, in which a true entry
   * means that invariants of that sort should be omitted from the
   * output .inv file.
   **/
  public static boolean[] omit_types = new boolean[256];

  // These variables are public so other programs can reuse the same
  // command-line options.

  // Please use these switches in the same order in all places where they
  // appear (in the code and in the documentation); it makes the code
  // easier to read and the documentation easier to keep up to date.

  // Control output
  public static final String help_SWITCH = "help";
  // "-o" switch: file to which serialized output is written
  public static final String no_text_output_SWITCH = "no_text_output";
  public static final String format_SWITCH = "format";
  public static final String show_progress_SWITCH = "show_progress";
  public static final String no_show_progress_SWITCH = "no_show_progress";
  public static final String noversion_SWITCH = "noversion";
  public static final String output_num_samples_SWITCH = "output_num_samples";
  public static final String files_from_SWITCH = "files_from";
  public static final String omit_from_output_SWITCH = "omit_from_output";
  // Control invariant detection
  public static final String conf_limit_SWITCH = "conf_limit";
  public static final String list_type_SWITCH = "list_type";
  public static final String no_dataflow_hierarchy_SWITCH = "nohierarchy";
  public static final String suppress_redundant_SWITCH = "suppress_redundant";
  // Process only part of the trace file
  public static final String ppt_regexp_SWITCH = "ppt-select-pattern";
  public static final String ppt_omit_regexp_SWITCH = "ppt-omit-pattern";
  public static final String var_regexp_SWITCH = "var-select-pattern";
  public static final String var_omit_regexp_SWITCH = "var-omit-pattern";
  // Configuration options
  public static final String server_SWITCH = "server"; //YOAV: server mode for Daikon: reads dtrace files as they appear
  public static final String config_SWITCH = "config";
  public static final String config_option_SWITCH = "config_option";
  // Debugging
  public static final String debugAll_SWITCH = "debug";
  public static final String debug_SWITCH = "dbg";
  public static final String track_SWITCH = "track";
  public static final String disc_reason_SWITCH = "disc_reason";
  public static final String mem_stat_SWITCH = "mem_stat";

  public static File server_dir = null; //YOAV: the directory from which we read the dtrace files

  // A pptMap which contains all the Program Points
  public static PptMap all_ppts;

  /** current invariant (used for debugging) **/
  public static Invariant current_inv = null;

  /* List of prototype invariants (one for each type of invariant) */
  public static ArrayList<Invariant> proto_invs = new ArrayList<Invariant>();

  /** Debug tracer. **/
  public static final Logger debugTrace = Logger.getLogger("daikon.Daikon");

  public static final Logger debugProgress =
    Logger.getLogger("daikon.Progress");

  public static final Logger debugEquality =
    Logger.getLogger("daikon.Equality");

  /** Debug tracer for ppt initialization. **/
  public static final Logger debugInit = Logger.getLogger("daikon.init");

  /** Prints out statistics concerning equality sets, suppressions, etc. **/
  public static final Logger debugStats = Logger.getLogger("daikon.stats");

  // Avoid problems if daikon.Runtime is loaded at analysis (rather than
  // test-run) time.  This might have to change when JTrace is used.
  static {
    daikon.Runtime.no_dtrace = true;
  }

  private static Stopwatch stopwatch = new Stopwatch();

  static String usage =
    UtilMDE.joinLines(
      release_string,
      "Daikon invariant detector, copyright 1998-2007",
      // " by Michael Ernst <mernst@csail.mit.edu>",
      "Uses the Java port of GNU getopt, copyright (c) 1998 Aaron M. Renn",
      // "For licensing information, see the License section of the manual.",
      "Usage:",
      "    java daikon.Daikon [flags...] files...",
      "  Each file is a declaration file or a data trace file; the file type",
      "  is determined by the file name (containing \".decls\" or \".dtrace\").",
      "  For a list of flags, see the Daikon manual, which appears in the ",
      "  Daikon distribution and also at http://pag.csail.mit.edu/daikon/.",
      "  --"+server_SWITCH+" dir",
      "  Server mode for Daikon in which it reads files from <dir> as they appear (sorted lexicographically) until it finds a file ending in '.end'"
      );

  /**
   * Thrown to indicate that main should not print a stack trace, but only
   * print the message itself to the user.
   * Code in Daikon should throw this Exception in cases of user error, an
   * throw other exceptions in cases of a Daikon bug or a system problem
   * (like unpredictable IOExceptions).
   * If the string is null, then this is normal termination, not an error;
   * no message is printed.
   **/
  public static class TerminationMessage extends RuntimeException {
    static final long serialVersionUID = 20050923L;
    public TerminationMessage(String s) { super(s); }
    public TerminationMessage(String format, Object... args) {
      super (String.format (format, args));
    }
    public TerminationMessage(Exception e) { super(e.getMessage()); }
    //public TerminationMessage(String s, LineNumberReader reader, String fileName) {
    //      super(new FileIOException(s, reader, fileName).getMessage()); }
    public TerminationMessage(Object... s) { super(UtilMDE.joinLines(s)); }
    public TerminationMessage() { super(); }
  }

  /**
   * The arguments to daikon.Daikon are file names.  Declaration file names
   * end in ".decls", and data trace file names end in ".dtrace".
   **/
  public static void main(final String[] args) {
    try {
      mainHelper(args);
    } catch (Configuration.ConfigException e) {
      // I don't think this can happen.  -MDE
      System.err.println(e.getMessage());
      System.exit(1);
    } catch (TerminationMessage e) {
      if (e.getMessage() != null) {
        System.err.println(e.getMessage());
        if (dkconfig_show_stack_trace)
          e.printStackTrace();
        System.exit(1);
      } else {
        System.exit(0);
      }
    }
    // Any exception other than TerminationMessage gets propagated.
    // This simplifies debugging by showing the stack trace.
    // (TerminationMessages should be clear enough not to need a stack trace.)
  }

  /**
   * This does the work of main, but it never calls System.exit, so it
   * is appropriate to be called progrmmatically.
   * Termination of the program with a message to the user is indicated by
   * throwing TerminationMessage.
   * @see #main(String[])
   * @see TerminationMessage
   **/
  public static void mainHelper(final String[] args) {
    // Cleanup from any previous runs
    cleanup();

    // Read command line options
    FileOptions files = read_options(args, usage);
    Set<File> decls_files = files.decls;
    Set<String> dtrace_files = files.dtrace;
    Set<File> spinfo_files = files.spinfo;
    Set<File> map_files = files.map;
    if (server_dir==null && (decls_files.size() == 0) && (dtrace_files.size() == 0)) {
      System.out.println("No .decls or .dtrace files specified");
      throw new Daikon.TerminationMessage("No .decls or .dtrace files specified");
    }

    if (Daikon.dkconfig_undo_opts) {
      Daikon.dkconfig_disable_splitting = true;
    }

    if (Daikon.dkconfig_quiet)
      Daikon.dkconfig_progress_delay= -1;

    // Set up debug traces; note this comes after reading command line options.
    LogHelper.setupLogs(Global.debugAll ? LogHelper.FINE : LogHelper.INFO);

    if (!noversion_output) {
      if (!Daikon.dkconfig_quiet)
      System.out.println(release_string);
    }

    // figure out which algorithm to use in NIS to process suppressions
    if (NIS.dkconfig_suppression_processor == SuppressionProcessor.HYBRID) {
      NIS.hybrid_method = true;
    } else {
      if (NIS.dkconfig_suppression_processor == SuppressionProcessor.ANTECEDENT) {
        NIS.antecedent_method = true;
        NIS.hybrid_method = false;
      } else {
        assert (NIS.dkconfig_suppression_processor == SuppressionProcessor.FALSIFIED);
        NIS.antecedent_method = false;
        NIS.hybrid_method = false;
      }
    }

    // Create the list of all invariant types
    setup_proto_invs();

    if (PrintInvariants.print_discarded_invariants) {
      DiscReasonMap.initialize();
    }

    fileio_progress = new FileIOProgress();
    fileio_progress.start();

    // Load declarations and splitters
    load_spinfo_files(spinfo_files);
    all_ppts = load_decls_files(decls_files);
    load_map_files(all_ppts, map_files);

    all_ppts.trimToSize();

    // If requested, just calculate the total number of invariants possible
    if (dkconfig_calc_possible_invs) {
      fileio_progress.shouldStop = true;
      int total_invs = 0;
      // Can't use new for syntax because the default iterator for all_ppts
      // is not the one I want here.
      for (Iterator<PptTopLevel> itor = all_ppts.ppt_all_iterator();
           itor.hasNext();
           ) {
        PptTopLevel ppt = itor.next();
        System.out.printf("Processing %s with %d variables",
                          ppt.name(), ppt.var_infos.length);
        int inv_cnt = 0;
        if (ppt.var_infos.length > 1600) {
          System.out.println("Skipping, too many variables!");
        } else {
          ppt.instantiate_views_and_invariants();
          inv_cnt = ppt.invariant_cnt();
          ppt.clean_for_merge();
          System.out.println(
            inv_cnt + " invariants in " + ppt.name());
          total_invs += inv_cnt;
        }
      }
      System.out.println(total_invs + "invariants total");
      return;
    }

    // Only for assertion checks
    isInferencing = true;

    // Infer invariants
    process_data(all_ppts, dtrace_files);
    isInferencing = false;
    if (Debug.logOn())
      Debug.check(all_ppts, "After process data");

    if (suppress_redundant_invariants_with_simplify) {
      suppressWithSimplify(all_ppts);
    }

    // Check that PptMap created was correct
    all_ppts.repCheck();

    // Remove undesired invariants, if requested
    if (omit_from_output) {
      processOmissions(all_ppts);
    }

    // Write serialized output - must be done before guarding invariants
    if (inv_file != null) {
      try {
        FileIO.write_serialized_pptmap(all_ppts, inv_file);
      } catch (IOException e) {
        throw new RuntimeException(
          "Error while writing .inv file "
            + "'"
            + inv_file
            + "': "
            + e.toString());
      }
    }

//     if ((Daikon.dkconfig_guardNulls == "always") // interned
//         || (Daikon.dkconfig_guardNulls == "missing")) { // interned
//       // This side-effects the PptMap, but it has already been saved
//       // to disk and is now being used only for printing.
//       guardInvariants(all_ppts);
//     }

    // Debug print information about the variables
    if (false) {
      for (PptTopLevel ppt : all_ppts.all_ppts()) {
        System.out.printf ("Dumping variables for ppt %s%n", ppt.name());
        for (VarInfo vi : ppt.var_infos) {
          System.out.printf ("  vi %s%n", vi);
          System.out.printf ("    file_rep_type = %s%n", vi.file_rep_type);
          System.out.printf ("    type = %s%n", vi.type);
        }
      }
    }


    // print out the invariants for each program point
    if (Daikon.dkconfig_undo_opts) {
      // Print out the invariants for each program point (sort first)
      for (Iterator<PptTopLevel> t = all_ppts.pptIterator(); t.hasNext();) {
        PptTopLevel ppt = t.next();

        // We do not need to print out program points that have not seen
        // any samples.
        if (ppt.num_samples() == 0) {
          continue;
        }
        List<Invariant> invs = PrintInvariants.sort_invariant_list(ppt.invariants_vector());
        List<Invariant> filtered_invs = filter_invs(invs);

        // Sometimes the program points actually differ in number of
        // samples seen due to differences in how Daikon and DaikonSimple
        // see the variable hierarchy.
        System.out.println(
        "====================================================");
        System.out.println(ppt.name());
        System.out.println(ppt.num_samples());

        for (Invariant inv : filtered_invs) {
            System.out.println(inv.getClass());
            System.out.println(inv);
        }
      }

      // exit the program
      return;
    }

    // Display invariants
    if (output_num_samples) {
      System.out.println(
        "The --output_num_samples debugging flag is on.");
      System.out.println(
        "Some of the debugging output may only make sense to Daikon programmers.");
    }

    // If they want to see discarded invariants, they probably don't
    // want to see the true ones.
    if (!PrintInvariants.print_discarded_invariants) {
      PrintInvariants.print_invariants(all_ppts);
    } else {
      PrintInvariants.print_reasons(all_ppts);
    }

    if (output_num_samples) {
      Global.output_statistics();
    }
    if (dkconfig_print_sample_totals)
      System.out.println(FileIO.samples_processed + " samples processed");

    // print statistics concerning what invariants are printed
    if (debugStats.isLoggable(Level.FINE)) {
      for (Iterator<PptTopLevel> itor = all_ppts.ppt_all_iterator();
           itor.hasNext();
           ) {
        PptTopLevel ppt = itor.next();
        PrintInvariants.print_filter_stats(debugStats, ppt, all_ppts);
      }
    }

    // Done
    if (!Daikon.dkconfig_quiet) {
      System.out.println("Exiting Daikon.");
    }
  }

  /**
   * Cleans up static variables so that mainHelper can be called more
   * than once.
   */
  public static void cleanup() {

    // Stop the thread that prints out progress information
    if ((fileio_progress != null)
        && (fileio_progress.getState() != Thread.State.NEW)) {
      fileio_progress.shouldStop = true;
      try {
        fileio_progress.join (2000);
      } catch (InterruptedException e) {
      }
      if (fileio_progress.getState() != Thread.State.TERMINATED) {
        throw new TerminationMessage ("Can't stop fileio_progress thead");
      }
    }
    fileio_progress = null;
    progress = "";

    proto_invs.clear();
  }

  // Structure for return value of read_options.
  // Return an array of {decls, dtrace, spinfo, map} files.
  public static class FileOptions {
    public Set<File> decls;
    public Set<String> dtrace;
    public Set<File> spinfo;
    public Set<File> map;
    public FileOptions(Set<File> decls, Set<String> dtrace, Set<File> spinfo, Set<File> map) {
      this.decls = decls;
      this.dtrace = dtrace;
      this.spinfo = spinfo;
      this.map = map;
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Read in the command line options
  // Return {decls, dtrace, spinfo, map} files.
  protected static FileOptions read_options(String[] args, String usage) {
    if (args.length == 0) {
      System.out.println(
        "Daikon error: no files supplied on command line.");
      System.out.println(usage);
      throw new Daikon.TerminationMessage();
    }

    // LinkedHashSet because it can be confusing to users if files (of the
    // same type) are gratuitously processed in a different order than they
    // were supplied on the command line.
    HashSet<File> decl_files = new LinkedHashSet<File>();
    HashSet<String> dtrace_files = new LinkedHashSet<String>(); /* either file names or "-"*/
    HashSet<File> spinfo_files = new LinkedHashSet<File>();
    HashSet<File> map_files = new LinkedHashSet<File>();

    LongOpt[] longopts =
      new LongOpt[] {
        // Control output
        new LongOpt(help_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        new LongOpt(no_text_output_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        new LongOpt(format_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(show_progress_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        new LongOpt(no_show_progress_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        new LongOpt(noversion_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        new LongOpt(output_num_samples_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        new LongOpt(files_from_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(omit_from_output_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        // Control invariant detection
        new LongOpt(conf_limit_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(list_type_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(no_dataflow_hierarchy_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        new LongOpt(suppress_redundant_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        // Process only part of the trace file
        new LongOpt(ppt_regexp_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(ppt_omit_regexp_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(var_regexp_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(var_omit_regexp_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        // Configuration options
        new LongOpt(server_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(config_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(config_option_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        // Debugging
        new LongOpt(debugAll_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        new LongOpt(debug_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(track_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(disc_reason_SWITCH, LongOpt.REQUIRED_ARGUMENT, null, 0),
        new LongOpt(mem_stat_SWITCH, LongOpt.NO_ARGUMENT, null, 0),
        };
    Getopt g = new Getopt("daikon.Daikon", args, "ho:", longopts);
    int c;

    while ((c = g.getopt()) != -1) {
      switch (c) {
        case 0 :
          // got a long option
          String option_name = longopts[g.getLongind()].getName();

          // Control output
          if (help_SWITCH.equals(option_name)) {
            System.out.println(usage);
            throw new Daikon.TerminationMessage();
          } else if (no_text_output_SWITCH.equals(option_name)) {
            no_text_output = true;
          } else if (format_SWITCH.equals(option_name)) {
            String format_name = g.getOptarg();
            Daikon.output_format = OutputFormat.get(format_name);
            if (Daikon.output_format == null) {
              throw new Daikon.TerminationMessage(
                "Unknown output format:  --format " + format_name);
            }
          } else if (show_progress_SWITCH.equals(option_name)) {
            show_progress = true;
            LogHelper.setLevel("daikon.Progress", LogHelper.FINE);
          } else if (no_show_progress_SWITCH.equals(option_name)) {
            show_progress = false;
          } else if (noversion_SWITCH.equals(option_name)) {
            noversion_output = true;
          } else if (output_num_samples_SWITCH.equals(option_name)) {
            output_num_samples = true;
          } else if (files_from_SWITCH.equals(option_name)) {
            String files_from_filename = g.getOptarg();
            try {
              for (String filename : new TextFile(files_from_filename)) {
                // Ignore blank lines in file.
                if (filename.equals("")) {
                  continue;
                }
                // This code is duplicated below outside the options loop.
                // These aren't "endsWith()" because there might be a suffix
                // on the end (eg, a date, or ".gz").
                File file = new File(filename);
                if (!file.exists()) {
                  throw new Daikon.TerminationMessage(
                    "File " + filename + " not found.");
                }
                if (filename.indexOf(".decls") != -1) {
                  decl_files.add(file);
                } else if (filename.indexOf(".dtrace") != -1) {
                  dtrace_files.add(filename);
                } else if (filename.indexOf(".spinfo") != -1) {
                  spinfo_files.add(file);
                } else if (filename.indexOf(".map") != -1) {
                  map_files.add(file);
                } else {
                  throw new Daikon.TerminationMessage(
                    "Unrecognized file extension: "
                      + filename);
                }
              }
            } catch (IOException e) {
              throw new RuntimeException(String.format("Error reading --files_from file: %s", files_from_filename));
            }
            break;
          } else if (omit_from_output_SWITCH.equals(option_name)) {
            String f = g.getOptarg();
            for (int i = 0; i < f.length(); i++) {
              if ("0rs".indexOf(f.charAt(i)) == -1)
                throw new Daikon.TerminationMessage(
                  "omit_from_output flag letter '"
                    + f.charAt(i)
                    + "' is unknown");
              omit_types[f.charAt(i)] = true;
            }
            omit_from_output = true;
          }
          // Control invariant detection
          else if (conf_limit_SWITCH.equals(option_name)) {
            double limit = Double.parseDouble(g.getOptarg());
            if ((limit < 0.0) || (limit > 1.0)) {
              throw new Daikon.TerminationMessage(
                conf_limit_SWITCH + " must be between [0..1]");
            }
            Configuration.getInstance().apply(
              "daikon.inv.Invariant.confidence_limit",
              String.valueOf(limit));
          } else if (list_type_SWITCH.equals(option_name)) {
            try {
              String list_type_string = g.getOptarg();
              ProglangType.list_implementors.add(
                list_type_string);
            } catch (Exception e) {
              throw new Daikon.TerminationMessage("Problem parsing " + list_type_SWITCH + " option: " + e);
            }
            break;
          } else if (
            no_dataflow_hierarchy_SWITCH.equals(option_name)) {
            use_dataflow_hierarchy = false;
          } else if (suppress_redundant_SWITCH.equals(option_name)) {
            suppress_redundant_invariants_with_simplify = true;
          }

          // Process only part of the trace file
          else if (ppt_regexp_SWITCH.equals(option_name)) {
            if (ppt_regexp != null)
              throw new Daikon.TerminationMessage(
                "multiple --"
                  + ppt_regexp_SWITCH
                  + " regular expressions supplied on command line");
            String regexp_string = g.getOptarg();
            try {
              // System.out.println("Regexp = " + regexp_string);
              ppt_regexp =
                Pattern.compile(regexp_string);
            } catch (Exception e) {
              throw new Daikon.TerminationMessage("Bad regexp " + regexp_string + " for " + ppt_regexp_SWITCH + ": " + e.getMessage());
            }
            break;
          } else if (ppt_omit_regexp_SWITCH.equals(option_name)) {
            if (ppt_omit_regexp != null)
              throw new Daikon.TerminationMessage(
                "multiple --"
                  + ppt_omit_regexp_SWITCH
                  + " regular expressions supplied on command line");
            String regexp_string = g.getOptarg();
            try {
              // System.out.println("Regexp = " + regexp_string);
              ppt_omit_regexp =
                Pattern.compile(regexp_string);
            } catch (Exception e) {
              throw new Daikon.TerminationMessage("Bad regexp " + regexp_string + " for " + ppt_omit_regexp_SWITCH + ": " + e.getMessage());
            }
            break;
          } else if (var_regexp_SWITCH.equals(option_name)) {
            if (var_regexp != null)
              throw new Daikon.TerminationMessage(
                "multiple --"
                  + var_regexp_SWITCH
                  + " regular expressions supplied on command line");
            String regexp_string = g.getOptarg();
            try {
              // System.out.println("Regexp = " + regexp_string);
              var_regexp =
                Pattern.compile(regexp_string);
            } catch (Exception e) {
              throw new Daikon.TerminationMessage("Bad regexp " + regexp_string + " for " + var_regexp_SWITCH + ": " + e.getMessage());
            }
            break;
          } else if (var_omit_regexp_SWITCH.equals(option_name)) {
            if (var_omit_regexp != null)
              throw new Daikon.TerminationMessage(
                "multiple --"
                  + var_omit_regexp_SWITCH
                  + " regular expressions supplied on command line");
            String regexp_string = g.getOptarg();
            try {
              // System.out.println("Regexp = " + regexp_string);
              var_omit_regexp =
                Pattern.compile(regexp_string);
            } catch (Exception e) {
              throw new Daikon.TerminationMessage("Bad regexp " + regexp_string + " for " + var_omit_regexp_SWITCH + ": " + e.getMessage());
            }
            break;
          }
          else if (server_SWITCH.equals(option_name)) {
            String input_dir = g.getOptarg();
            server_dir = new File(input_dir);
            if (!server_dir.isDirectory() || !server_dir.canRead() || !server_dir.canWrite())
              throw new RuntimeException(
                "Could not open config file in server directory " + server_dir);
            break;

          // Configuration options

          } else if (config_SWITCH.equals(option_name)) {
            String config_file = g.getOptarg();
            try {
              InputStream stream =
                new FileInputStream(config_file);
              Configuration.getInstance().apply(stream);
            } catch (IOException e) {
              throw new Daikon.TerminationMessage(
                // Is this the only possible reason for an IOException?
                "Could not open config file " + config_file);
            }
            break;
          } else if (config_option_SWITCH.equals(option_name)) {
            String item = g.getOptarg();
            try {
              Configuration.getInstance().apply(item);
            } catch (daikon.config.Configuration.ConfigException e) {
              throw new Daikon.TerminationMessage(e);
            }
            break;
          }
          else if (debugAll_SWITCH.equals(option_name)) {
            Global.debugAll = true;
          } else if (debug_SWITCH.equals(option_name)) {
            LogHelper.setLevel(g.getOptarg(), LogHelper.FINE);
          } else if (track_SWITCH.equals(option_name)) {
            LogHelper.setLevel("daikon.Debug", LogHelper.FINE);
            String error = Debug.add_track(g.getOptarg());
            if (error != null) {
              throw new Daikon.TerminationMessage(
                "Error parsing track argument '"
                  + g.getOptarg()
                  + "' - "
                  + error);
            }
          } else if (disc_reason_SWITCH.equals(option_name)) {
            try {
              PrintInvariants.discReasonSetup(g.getOptarg());
            } catch (IllegalArgumentException e) {
              throw new Daikon.TerminationMessage(e);
            }
          } else if (mem_stat_SWITCH.equals(option_name)) {
            use_mem_monitor = true;
          } else {
            throw new Daikon.TerminationMessage(
              "Unknown option " + option_name + " on command line");
          }
          break;
        case 'h' :
          System.out.println(usage);
          throw new Daikon.TerminationMessage();
        case 'o' :
          String inv_filename = g.getOptarg();

          if (inv_file != null) {
            throw new Daikon.TerminationMessage("multiple serialization output files supplied on command line: " + inv_file + " " + inv_filename);
          }

          inv_file = new File(inv_filename);

          if (!UtilMDE.canCreateAndWrite(inv_file)) {
            throw new Daikon.TerminationMessage("Cannot write to serialization output file " + inv_file);
          }
          break;
          //
        case '?' :
          // break; // getopt() already printed an error
          System.out.println(usage);
          throw new Daikon.TerminationMessage();
          //
        default :
          System.out.println("getopt() returned " + c);
          break;
      }
    }

    // This code is duplicated above within the switch processing.
    // First check that all the file names are OK, so we don't do lots of
    // processing only to bail out at the end.
    for (int i = g.getOptind(); i < args.length; i++) {
      String filename = args[i];
      File file = null;
      if (!filename.equals("-") && !filename.equals("+")) {
        file = new File(filename);
        if (!file.exists()) {
            throw new Daikon.TerminationMessage("File " + file + " not found.");
        }
        filename = file.toString();
      }
      // These aren't "endsWith()" because there might be a suffix on the end
      // (eg, a date or ".gz").
      if (filename.indexOf(".decls") != -1) {
        decl_files.add(file);
      } else if (filename.indexOf(".dtrace") != -1) {
        dtrace_files.add(filename);
        // Always output an invariant file by default, even if none is
        // specified on the command line.
        if (inv_file == null) {
          String basename;
          // This puts the .inv file in the current directory.
          basename = new File(filename).getName();
          // This puts the .inv file in the same directory as the .dtrace file.
          // basename = filename;
          int base_end = basename.indexOf(".dtrace");
          String inv_filename = basename.substring(0, base_end) + ".inv.gz";

            inv_file = new File(inv_filename);
             if (!UtilMDE.canCreateAndWrite(inv_file)) {
           throw new Daikon.TerminationMessage("Cannot write to file " + inv_file);
           }
        }
      } else if (filename.indexOf(".spinfo") != -1) {
        spinfo_files.add(file);
      } else if (filename.indexOf(".map") != -1) {
        map_files.add(file);
      } else if (filename.equals("-") || filename.equals("+")) {
        dtrace_files.add(filename);
      } else {
        throw new Daikon.TerminationMessage("Unrecognized file type: " + file);
      }
    }

    // Set the fuzzy float comparison ratio.  This needs to be done after
    // any configuration options (which may set the ratio) are processed.
    Global.fuzzy.set_rel_diff(Invariant.dkconfig_fuzzy_ratio);

    // Setup ppt_max_name based on the specified percentage of ppts to process
    if (dkconfig_ppt_perc != 100) {
      ppt_max_name = setup_ppt_perc(decl_files, dkconfig_ppt_perc);
      System.out.println("Max ppt name = " + ppt_max_name);
    }

    // Validate guardNulls option
    PrintInvariants.validateGuardNulls();

    return new FileOptions(decl_files, dtrace_files, spinfo_files, map_files);
  }

  /**
   * Creates the list of prototype invariants for all Daikon invariants.
   * New invariants must be added to this list
   */
  public static void setup_proto_invs() {

    // Unary scalar invariants
    {
      // OneOf (OneOf.java.jpp)
      proto_invs.add(OneOfScalar.get_proto());
      proto_invs.add(OneOfFloat.get_proto());
      proto_invs.add(OneOfString.get_proto());

      // NonZero (NonZero.java.jpp)
      proto_invs.add(NonZero.get_proto());
      proto_invs.add(NonZeroFloat.get_proto());

      // Lower and Upper bound (Bound.java.jpp)
      proto_invs.add(LowerBound.get_proto());
      proto_invs.add(LowerBoundFloat.get_proto());
      proto_invs.add(UpperBound.get_proto());
      proto_invs.add(UpperBoundFloat.get_proto());

      // Modulus and NonModulus (Modulus.java and NonModulus.java)
      proto_invs.add(Modulus.get_proto());
      proto_invs.add(NonModulus.get_proto());

      // Range invariant (Range.java.jpp)
      proto_invs.addAll(RangeInt.get_proto_all());
      proto_invs.addAll(RangeFloat.get_proto_all());

      // Printable String
      proto_invs.add (PrintableString.get_proto());

      // Positive (x > 0) (Postive.java).  Positive is a sample invariant
      // that is only included as an example.
      // proto_invs.add (Postive.get_proto());
    }

    // Unary sequence invariants
    {
      // OneOf (OneOf.java.jpp)
      proto_invs.add(OneOfSequence.get_proto());
      proto_invs.add(OneOfFloatSequence.get_proto());
      proto_invs.add(OneOfStringSequence.get_proto());
      proto_invs.add(EltOneOf.get_proto());
      proto_invs.add(EltOneOfFloat.get_proto());
      proto_invs.add(EltOneOfString.get_proto());

      // Range invariant (Range.java.jpp)
      proto_invs.addAll(EltRangeInt.get_proto_all());
      proto_invs.addAll(EltRangeFloat.get_proto_all());

      // Sequence Index Comparisons (SeqIndexComparison.java.jpp)
      proto_invs.add(SeqIndexIntEqual.get_proto());
      proto_invs.add(SeqIndexIntNonEqual.get_proto());
      proto_invs.add(SeqIndexIntGreaterEqual.get_proto());
      proto_invs.add(SeqIndexIntGreaterThan.get_proto());
      proto_invs.add(SeqIndexIntLessEqual.get_proto());
      proto_invs.add(SeqIndexIntLessThan.get_proto());
      proto_invs.add(SeqIndexFloatEqual.get_proto());
      proto_invs.add(SeqIndexFloatNonEqual.get_proto());
      proto_invs.add(SeqIndexFloatGreaterEqual.get_proto());
      proto_invs.add(SeqIndexFloatGreaterThan.get_proto());
      proto_invs.add(SeqIndexFloatLessEqual.get_proto());
      proto_invs.add(SeqIndexFloatLessThan.get_proto());

      // foreach i compare a[i] to a[i+1] (EltwiseIntComparisons.java.jpp)
      proto_invs.add(EltwiseIntEqual.get_proto());
      proto_invs.add(EltwiseIntLessEqual.get_proto());
      proto_invs.add(EltwiseIntGreaterEqual.get_proto());
      proto_invs.add(EltwiseIntLessThan.get_proto());
      proto_invs.add(EltwiseIntGreaterThan.get_proto());
      proto_invs.add(EltwiseFloatEqual.get_proto());
      proto_invs.add(EltwiseFloatLessEqual.get_proto());
      proto_invs.add(EltwiseFloatGreaterEqual.get_proto());
      proto_invs.add(EltwiseFloatLessThan.get_proto());
      proto_invs.add(EltwiseFloatGreaterThan.get_proto());

      // EltNonZero (EltNonZero.java.jpp)
      proto_invs.add(EltNonZero.get_proto());
      proto_invs.add(EltNonZeroFloat.get_proto());

      // No Duplicates (NoDuplicates.java.jpp)
      proto_invs.add(NoDuplicates.get_proto());
      proto_invs.add(NoDuplicatesFloat.get_proto());

      // Element bounds (Bound.java.jpp)
      proto_invs.add(EltLowerBound.get_proto());
      proto_invs.add(EltUpperBound.get_proto());
      proto_invs.add(EltLowerBoundFloat.get_proto());
      proto_invs.add(EltUpperBoundFloat.get_proto());

      // CommonSequence (CommonSequence.java.jpp)
      proto_invs.add (CommonSequence.get_proto());
      proto_invs.add (CommonFloatSequence.get_proto());

      // CommonStringSequence (CommonStringSubsequence.java)
      proto_invs.add (CommonStringSequence.get_proto());    }

    // Binary scalar-scalar invariants
    {
      // Int, Float, String comparisons (from IntComparisons.java.jpp)
      proto_invs.add(IntEqual.get_proto());
      proto_invs.add(IntNonEqual.get_proto());
      proto_invs.add(IntLessThan.get_proto());
      proto_invs.add(IntGreaterThan.get_proto());
      proto_invs.add(IntLessEqual.get_proto());
      proto_invs.add(IntGreaterEqual.get_proto());
      proto_invs.add(FloatEqual.get_proto());
      proto_invs.add(FloatNonEqual.get_proto());
      proto_invs.add(FloatLessThan.get_proto());
      proto_invs.add(FloatGreaterThan.get_proto());
      proto_invs.add(FloatLessEqual.get_proto());
      proto_invs.add(FloatGreaterEqual.get_proto());
      proto_invs.add(StringEqual.get_proto());
      proto_invs.add(StringNonEqual.get_proto());
      proto_invs.add(StringLessThan.get_proto());
      proto_invs.add(StringGreaterThan.get_proto());
      proto_invs.add(StringLessEqual.get_proto());
      proto_invs.add(StringGreaterEqual.get_proto());

      // LinearBinary over integer/float (from LinearBinary.java.jpp)
      proto_invs.add(LinearBinary.get_proto());
      proto_invs.add(LinearBinaryFloat.get_proto());

      // Numeric invariants (from Numeric.java.jpp)
      proto_invs.addAll(NumericInt.get_proto_all());
      proto_invs.addAll(NumericFloat.get_proto_all());
    }

    // Binary sequence-sequence invariants
    {
      // Numeric invariants (from Numeric.java.jpp)
      proto_invs.addAll(PairwiseNumericInt.get_proto_all());
      proto_invs.addAll(PairwiseNumericFloat.get_proto_all());

      // Lexical sequence comparisons (from SeqComparison.java.jpp)
      proto_invs.add(SeqSeqIntEqual.get_proto());
      proto_invs.add(SeqSeqIntLessThan.get_proto());
      proto_invs.add(SeqSeqIntGreaterThan.get_proto());
      proto_invs.add(SeqSeqIntLessEqual.get_proto());
      proto_invs.add(SeqSeqIntGreaterEqual.get_proto());
      proto_invs.add(SeqSeqFloatEqual.get_proto());
      proto_invs.add(SeqSeqFloatLessThan.get_proto());
      proto_invs.add(SeqSeqFloatGreaterThan.get_proto());
      proto_invs.add(SeqSeqFloatLessEqual.get_proto());
      proto_invs.add(SeqSeqFloatGreaterEqual.get_proto());
      proto_invs.add(SeqSeqStringEqual.get_proto());
      proto_invs.add(SeqSeqStringLessThan.get_proto());
      proto_invs.add(SeqSeqStringGreaterThan.get_proto());
      proto_invs.add(SeqSeqStringLessEqual.get_proto());
      proto_invs.add(SeqSeqStringGreaterEqual.get_proto());

      // Pairwise sequence comparisons (from PairwiseIntComparison.java.jpp)
      proto_invs.add(PairwiseIntEqual.get_proto());
      proto_invs.add(PairwiseIntLessThan.get_proto());
      proto_invs.add(PairwiseIntGreaterThan.get_proto());
      proto_invs.add(PairwiseIntLessEqual.get_proto());
      proto_invs.add(PairwiseIntGreaterEqual.get_proto());
      proto_invs.add(PairwiseFloatEqual.get_proto());
      proto_invs.add(PairwiseFloatLessThan.get_proto());
      proto_invs.add(PairwiseFloatGreaterThan.get_proto());
      proto_invs.add(PairwiseFloatLessEqual.get_proto());
      proto_invs.add(PairwiseFloatGreaterEqual.get_proto());

      // Array Reverse (from Reverse.java.jpp)
      proto_invs.add(Reverse.get_proto());
      proto_invs.add(ReverseFloat.get_proto());

      // Pairwise Linear Binary (from PairwiseLinearBinary.java.jpp)
      proto_invs.add(PairwiseLinearBinary.get_proto());
      proto_invs.add(PairwiseLinearBinaryFloat.get_proto());

      // Subset and Superset (from SubSet.java.jpp)
      proto_invs.add(SubSet.get_proto());
      proto_invs.add(SuperSet.get_proto());
      proto_invs.add(SubSetFloat.get_proto());
      proto_invs.add(SuperSetFloat.get_proto());

      // Subsequence (from SubSequence.java.jpp)
      proto_invs.add(SubSequence.get_proto());
      proto_invs.add(SubSequenceFloat.get_proto());
      proto_invs.add(SuperSequence.get_proto());
      proto_invs.add(SuperSequenceFloat.get_proto());
    }

    // Binary sequence-scalar invariants
    {
      // Comparison of scalar to each array element (SeqIntComparison.java.jpp)
      proto_invs.add(SeqIntEqual.get_proto());
      proto_invs.add(SeqIntLessThan.get_proto());
      proto_invs.add(SeqIntGreaterThan.get_proto());
      proto_invs.add(SeqIntLessEqual.get_proto());
      proto_invs.add(SeqIntGreaterEqual.get_proto());
      proto_invs.add(SeqFloatEqual.get_proto());
      proto_invs.add(SeqFloatLessThan.get_proto());
      proto_invs.add(SeqFloatGreaterThan.get_proto());
      proto_invs.add(SeqFloatLessEqual.get_proto());
      proto_invs.add(SeqFloatGreaterEqual.get_proto());

      // Scalar is an element of the array (Member.java.jpp)
      proto_invs.add(Member.get_proto());
      proto_invs.add(MemberFloat.get_proto());
      proto_invs.add(MemberString.get_proto());
    }

    // Ternary invariants
    {
      // FunctionBinary (FunctionBinary.java.jpp)
      proto_invs.addAll(FunctionBinary.get_proto_all());
      proto_invs.addAll(FunctionBinaryFloat.get_proto_all());

      // LinearTernary (LinearTernary.java.jpp)
      proto_invs.add(LinearTernary.get_proto());
      proto_invs.add(LinearTernaryFloat.get_proto());
    }

    // Remove any elements that are not enabled
    for (Iterator<Invariant> i = proto_invs.iterator(); i.hasNext(); ) {
      Invariant inv = i.next();
      Assert.assertTrue (inv != null);
      if (!inv.enabled())
        i.remove();
    }
  }

  /**
   * Creates upper program points by merging together the invariants
   * from all of the lower points.
   */
  public static void createUpperPpts (PptMap all_ppts) {

    // Process each ppt that doesn't have a parent
    for (Iterator<PptTopLevel> i = all_ppts.pptIterator(); i.hasNext(); ) {
      PptTopLevel ppt = i.next();
      // System.out.printf ("considering ppt %s parents: %s, children: %s\n",
      //                     ppt.name, ppt.parents, ppt.children);
      if (ppt.parents.size() == 0) {
        ppt.mergeInvs();
      }
    }
  }

  /**
   * Create combined exit points, setup splitters, and add orig and
   * derived variables,
   */
  public static void init_ppt (PptTopLevel ppt, PptMap all_ppts) {

    if (!Daikon.using_DaikonSimple) {
      // Setup splitters.  This must be done before adding derived variables.
      // Do not add splitters to ppts that were already created by splitters!
      if (! (ppt instanceof PptConditional)) {
        setup_splitters(ppt);
      }
    }

    // Create orig and derived variables
    progress = "Creating orig variables for: " + ppt.name;
    create_orig_vars (ppt, all_ppts);
    if (!Derivation.dkconfig_disable_derived_variables) {
      progress = "Creating derived variables for: " + ppt.name;
      ppt.create_derived_variables();
    }

    if (!Daikon.using_DaikonSimple) {
      // Initialize equality sets on leaf nodes
      setupEquality(ppt);
      // System.out.printf ("initialized equality %s for ppt %s%n",
      //                    ppt.equality_view, ppt.name());

      // Recursively initialize ppts created by splitters
      if (ppt.has_splitters()) {
        for (Iterator<PptConditional> ii = ppt.cond_iterator(); ii.hasNext();){
          PptConditional ppt_cond = ii.next();
          init_ppt (ppt_cond, all_ppts);
        }
      }
    }
  }


  /**
   * Create EXIT program points as needed for EXITnn program points.
   */
  public static void create_combined_exits(PptMap ppts) {

    // We can't add the newly created exit Ppts directly to ppts while we
    // are iterating over it, so store them temporarily in this map.
    PptMap exit_ppts = new PptMap();

    for (Iterator<PptTopLevel> i = ppts.pptIterator(); i.hasNext(); ) {
      PptTopLevel ppt = i.next();
      // skip unless its an EXITnn
      if (!ppt.is_subexit())
        continue;

      PptTopLevel exitnn_ppt = ppt;
      PptName exitnn_name = exitnn_ppt.ppt_name;
      PptName exit_name = ppt.ppt_name.makeExit();
      PptTopLevel exit_ppt = exit_ppts.get(exit_name);

      if (debugInit.isLoggable(Level.FINE))
        debugInit.fine ("create_combined_exits: encounted exit "
                        + exitnn_ppt.name());

      // Create the exit, if necessary
      if (exit_ppt == null) {
        // this is a hack.  it should probably filter out orig and derived
        // vars instead of taking the first n.
        int len = ppt.num_tracevars + ppt.num_static_constant_vars;
        VarInfo[] exit_vars = new VarInfo[len];
        for (int j = 0; j < len; j++) {
          exit_vars[j] = new VarInfo(ppt.var_infos[j]);
          exit_vars[j].varinfo_index = ppt.var_infos[j].varinfo_index;
          exit_vars[j].value_index = ppt.var_infos[j].value_index;
          exit_vars[j].equalitySet = null;
        }

        exit_ppt
          = new PptTopLevel(exit_name.getName(), PptTopLevel.PptType.EXIT,
                            ppt.parent_relations, ppt.flags, exit_vars);

        // exit_ppt.ppt_name.setVisibility(exitnn_name.getVisibility());
        exit_ppts.add(exit_ppt);
        if (debugInit.isLoggable(Level.FINE))
          debugInit.fine ("create_combined_exits: created exit "
                          + exit_name);
        init_ppt (exit_ppt, ppts);
      }
    }

    // Now add the newly created Ppts to the global map.
    for (Iterator<PptTopLevel> i = exit_ppts.pptIterator(); i.hasNext(); ) {
      PptTopLevel ppt = i.next();
      ppts.add(ppt);
    }
  }

  // The function filters out the reflexive invs in binary slices,
  // reflexive and partially reflexive invs in ternary slices
  // and also filters out the invariants that have not seen enough
  // samples in ternary slices.
  static List<Invariant> filter_invs(List<Invariant> invs) {
    List<Invariant> new_list = new ArrayList<Invariant>();

    for (Invariant inv : invs) {
      VarInfo[] vars = inv.ppt.var_infos;

      // This check is the most non-intrusive way to filter out the invs
      // Filter out reflexive invariants in the binary invs
      if (!((inv.ppt instanceof PptSlice2) && vars[0] == vars[1])) {

        // Filter out the reflexive and partially reflexive invs in the
        // ternary slices
        if (!((inv.ppt instanceof PptSlice3) && (vars[0] == vars[1]
            || vars[1] == vars[2] || vars[0] == vars[2]))) {
          if (inv.ppt.num_values() != 0) {

            // filters out "warning: too few samples for
            // daikon.inv.ternary.threeScalar.LinearTernary invariant"
            if (inv.isActive()) {
              new_list.add(inv);
            }
          }
        }
      }
    }

    return new_list;
  }
  /**
   * Add orig() variables to the given EXIT/EXITnn point, Does nothing if
   * exit_ppt is not an EXIT/EXITnn.
   */
  private static void create_orig_vars(PptTopLevel exit_ppt, PptMap ppts) {
    if (! exit_ppt.ppt_name.isExitPoint()) {
      return;
    }

    if (debugInit.isLoggable(Level.FINE)) {
      debugInit.fine ("Doing create and relate orig vars for: "
                       + exit_ppt.name());
    }

    PptTopLevel entry_ppt = ppts.get(exit_ppt.ppt_name.makeEnter());
    Assert.assertTrue(entry_ppt != null, exit_ppt.name());

    // Add "orig(...)" (prestate) variables to the program point.
    // Don't bother to include the constants.  Walk through
    // entry_ppt's vars.  For each non-constant, put it on the
    // new_vis worklist after fixing its comparability information.
    exit_ppt.num_orig_vars = entry_ppt.num_tracevars;
    VarInfo[] new_vis = new VarInfo[exit_ppt.num_orig_vars];
    {
      VarInfo[] entry_ppt_vis = entry_ppt.var_infos;
      int new_vis_index = 0;
      for (int k = 0; k < entry_ppt.num_declvars; k++) {
        VarInfo vi = entry_ppt_vis[k];
        Assert.assertTrue(!vi.isDerived(), "Derived when making orig(): "
                          + vi.name());
        if (vi.isStaticConstant())
          continue;
        VarInfo origvar = VarInfo.origVarInfo(vi);
        // Fix comparability
        VarInfo postvar = exit_ppt.find_var_by_name (vi.name());
        if (postvar == null) {
          System.out.printf ("Cant find var %s in exit of ppt %s%n", vi,
                             exit_ppt.name());
          for (VarInfo cvi : entry_ppt.var_infos)
            System.out.printf ("  entry var = %s%n", cvi);
          for (VarInfo cvi : exit_ppt.var_infos)
            System.out.printf ("  exit var = %s%n", cvi);
          assert false;
          throw new RuntimeException("this can't happen: postvar is null");
        }
        origvar.postState = postvar;
        origvar.comparability = postvar.comparability.makeAlias();

        // Add to new_vis
        new_vis[new_vis_index] = origvar;
        new_vis_index++;
        //System.out.printf ("adding origvar %s to ppt %s%n", origvar.name(),
        //                   exit_ppt.name());
      }
      Assert.assertTrue(new_vis_index == exit_ppt.num_orig_vars);
    }
    exit_ppt.addVarInfos(new_vis);
  }


  ///////////////////////////////////////////////////////////////////////////
  // Read decls, dtrace, etc. files

  private static PptMap load_decls_files(Set<File> decl_files) {
    stopwatch.reset();
    try {
      if (!Daikon.dkconfig_quiet) {
        System.out.print("Reading declaration files ");
      }
      PptMap all_ppts = FileIO.read_declaration_files(decl_files);
      if (debugTrace.isLoggable(Level.FINE)) {
        debugTrace.fine("Initializing partial order");
      }
      fileio_progress.clear();
      if (!Daikon.dkconfig_quiet && decl_files.size() > 0) {
        System.out.print(" (read ");
        System.out.print(UtilMDE.nplural(decl_files.size(), "decls file"));
        System.out.println(")");
      }
      return all_ppts;
    } catch (IOException e) {
      // System.out.println();
      // e.printStackTrace();
      throw new Daikon.TerminationMessage("Error parsing decl file", e);
    } finally {
      debugProgress.fine(
        "Time spent on read_declaration_files: " + stopwatch.format());
    }
  }

  private static void load_spinfo_files(Set<File> spinfo_files) {
    if (dkconfig_disable_splitting || spinfo_files.isEmpty()) {
      return;
    }
    stopwatch.reset();
    try {
      System.out.print("Reading splitter info files ");
      create_splitters(spinfo_files);
      System.out.print(" (read ");
      System.out.print(UtilMDE.nplural(spinfo_files.size(), "spinfo file"));
      System.out.println(")");
    } catch (IOException e) {
      System.out.println();
      e.printStackTrace();
      throw new Error(e);
    } finally {
      debugProgress.fine("Time spent on load_spinfo_files: "
                         + stopwatch.format());
    }
  }

  private static void load_map_files(PptMap all_ppts, Set<File> map_files) {
    stopwatch.reset();
    if (!dkconfig_disable_splitting && map_files.size() > 0) {
      System.out.print("Reading map (context) files ");
      ContextSplitterFactory.load_mapfiles_into_splitterlist(
        map_files,
        ContextSplitterFactory.dkconfig_granularity);
      System.out.print(" (read ");
      System.out.print(
        UtilMDE.nplural(map_files.size(), "map (context) file"));
      System.out.println(")");
      debugProgress.fine(
        "Time spent on load_map_files: " + stopwatch.format());
    }
  }

  /**
   * Sets up splitting on all ppts.  Currently only binary splitters
   * over boolean returns or exactly two return statements are enabled
   * by default (though other splitters can be defined by the user)
   */
  public static void setup_splitters(PptTopLevel ppt) {
    if (dkconfig_disable_splitting) {
      return;
    }

    SplitterFactory.load_splitters(ppt, parsedSplitters);

    Splitter[] pconds = null;
    if (SplitterList.dkconfig_all_splitters) {
      pconds = SplitterList.get_all();
    } else {
      pconds = SplitterList.get(ppt.name());
    }
    if (pconds != null) {
      if (Global.debugSplit.isLoggable(Level.FINE)) {
	Global.debugSplit.fine(
			       "Got "
			       + UtilMDE.nplural(pconds.length, "splitter")
			       + " for "
			       + ppt.name());
      }
      ppt.addConditions(pconds);
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Infer invariants over the trace data

  /** Indicate progress for FileIOProgress. **/
  public static String progress = "";

  /**
   * The number of columns of progress information to display. In many
   * Unix shells, this can be set to an appropriate value by
   * <samp>--config_option daikon.Daikon.progress_display_width=$COLUMNS</samp>.
   **/
  public static int dkconfig_progress_display_width = 80;

  /** A way to output FileIO progress information easily. */
  private static FileIOProgress fileio_progress = null;
  public static class FileIOProgress extends Thread {
    public FileIOProgress() {
      setDaemon(true);
      pctFmt = NumberFormat.getPercentInstance();
      pctFmt.setMinimumFractionDigits(2);
      pctFmt.setMaximumFractionDigits(2);
      df = DateFormat.getTimeInstance(/*DateFormat.LONG*/
      );
    }
    /**
     * Clients should set this variable instead of calling Thread.stop(),
     * which is deprecated.  Typically a client calls "display()" before
     * setting this.
     **/
    public boolean shouldStop = false;
    private static NumberFormat pctFmt;
    private DateFormat df;
    public void run() {
      if (dkconfig_progress_delay == -1)
        return;
      while (true) {
        if (shouldStop) {
          clear();
          return;
        }
        display();
        try {
          sleep(dkconfig_progress_delay);
        } catch (InterruptedException e) {
          // hmm
        }
      }
    }
    /** Clear the display; good to do before printing to System.out. **/
    public void clear() {
      if (dkconfig_progress_delay == -1)
        return;
      // "display("");" is wrong becuase it leaves the timestamp and writes
      // spaces across the screen.
      String status =
        UtilMDE.rpad("", dkconfig_progress_display_width - 1);
      System.out.print("\r" + status);
      System.out.print("\r"); // return to beginning of line
      System.out.flush();
    }
    /**
     * Displays the current status.
     * Call this if you don't want to wait until the next automatic display.
     **/
    public void display() {
      if (dkconfig_progress_delay == -1)
        return;
      display(message());
    }
    /** Displays the given message. **/
    public void display(String message) {
      if (dkconfig_progress_delay == -1)
        return;
      String status =
        UtilMDE.rpad(
          "[" + df.format(new Date()) + "]: " + message,
          dkconfig_progress_display_width - 1);
      System.out.print("\r" + status);
      System.out.flush();
      // System.out.println (status);

      if (debugTrace.isLoggable(Level.FINE)) {
        debugTrace.fine(
          "Free memory: "
            + java.lang.Runtime.getRuntime().freeMemory());
        debugTrace.fine(
          "Used memory: "
            + (java.lang.Runtime.getRuntime().totalMemory()
              - java.lang.Runtime.getRuntime().freeMemory()));
	if (FileIO.data_trace_state != null)
	  debugTrace.fine("Active slices: " +
			  FileIO.data_trace_state.all_ppts.countSlices());
      }
    }
    private String message() {
      if (FileIO.data_trace_state == null) {
        if (Daikon.progress == null) {
          return "[no status]";
        } else {
          return Daikon.progress;
        }
      }
      String filename = FileIO.data_trace_state.filename;
      LineNumberReader lnr = FileIO.data_trace_state.reader;
      String line;
      if (lnr == null) {
        line = "?";
      } else {
        long lineNum = lnr.getLineNumber();
        line = String.valueOf(lineNum);
        if (FileIO.data_trace_state.total_lines > 0) {
          double frac =
            lineNum / (double) FileIO.data_trace_state.total_lines;
          String percent = pctFmt.format(frac);
          line = line + ", " + percent;
        }
      }
      return "Reading " + filename + " (line " + line + ") ...";
    }
  }

  /**
   * The data-processing routine of the daikon engine.  At this
   * point, the decls and spinfo files have been loaded, all of the
   * program points have been setup, and candidate invariants have
   * been instantiated.  This routine processes data to falsify the
   * candidate invariants.
   **/
  private static void process_data(PptMap all_ppts, Set<String> dtrace_files) {
    MemMonitor monitor = null;
    if (use_mem_monitor) {
      monitor = new MemMonitor("stat.out");
      new Thread((Runnable) monitor).start();
    }

    stopwatch.reset();

    // Preprocessing
    setup_NISuppression();

    // Processing (actually using dtrace files)
    try {
      fileio_progress.clear();
      if (!Daikon.dkconfig_quiet) {
        System.out.println(
          "Processing trace data; reading "
            + UtilMDE.nplural(dtrace_files.size(), "dtrace file")
            + ":");
      }
      FileIO.read_data_trace_files(dtrace_files, all_ppts);
      fileio_progress.shouldStop = true;
      // Final update, so "100%", not "99.70%", is the last thing printed.
      fileio_progress.display();
      if (!Daikon.dkconfig_quiet) {
        System.out.println();
      }
      // System.out.print("Creating implications "); // XXX untested code
      // for (PptTopLevel ppt : all_ppts) {
      //   System.out.print('.');
      //   ppt.addImplications();
      // }
      // System.out.println();
    } catch (IOException e) {
      System.out.println();
      e.printStackTrace();
      throw new Error(e);
    } finally {
      debugProgress.fine(
        "Time spent on read_data_trace_files: " + stopwatch.format());
    }

    if (monitor != null) {
      monitor.stop();
    }

    if (FileIO.dkconfig_read_samples_only) {
      throw new Daikon.TerminationMessage(
        Fmt.spf(
          "Finished reading %s samples",
          "" + FileIO.samples_processed));
    }

    if (all_ppts.size() == 0) {
      String message = "No program point declarations were found.";
      if (FileIO.omitted_declarations != 0) {
        message += lineSep + "  " + FileIO.omitted_declarations + " "
          + ((FileIO.omitted_declarations == 1)
             ? "declaration was"
             : "declarations were")
          + " omitted by regexps (e.g., --ppt-select-pattern).";
      }
      throw new Daikon.TerminationMessage(message);
    }

    // System.out.println("samples processed: " + FileIO.samples_processed);
    // if  {
    int unmatched_count = FileIO.call_stack.size() + FileIO.call_hashmap.size();
    if ((use_dataflow_hierarchy
         && FileIO.samples_processed == unmatched_count)
        || (FileIO.samples_processed == 0)) {
      throw new Daikon.TerminationMessage("No samples found for any of "
                                          + UtilMDE.nplural(all_ppts.size(),
                                                            "program point"));
    }

    // ppt_stats (all_ppts);

    //     if (debugStats.isLoggable (Level.FINE)) {
    //       PptSliceEquality.print_equality_stats (debugStats, all_ppts);
    //     }

    // Print equality set info
    //     for (Iterator<PptTopLevel> i = all_ppts.pptIterator(); i.hasNext(); ) {
    //       PptTopLevel ppt = i.next();
    //       Fmt.pf ("ppt: %s", ppt.name);
    //       if ((ppt.equality_view == null) || (ppt.equality_view.invs == null))
    //       continue;
    //       for (Invariant inv : ppt.equality_view.invs) {
    //       Equality e = (Equality) inv;
    //       Fmt.pf ("    equality set = %s", e);
    //       }
    //     }

    // Fmt.pf ("printing ternary invariants");
    // PrintInvariants.print_all_ternary_invs (all_ppts);
    // System.exit(0);

    // Postprocessing

    stopwatch.reset();

    debugProgress.fine("Create Combined Exits ... ");
    create_combined_exits(all_ppts);

    // Post process dynamic constants
    if (dkconfig_use_dynamic_constant_optimization) {
      debugProgress.fine("Constant Post Processing ... ");
      for (Iterator<PptTopLevel> itor = all_ppts.ppt_all_iterator();
        itor.hasNext();
        ) {
        PptTopLevel ppt = itor.next();
        if (ppt.constants != null)
          ppt.constants.post_process();
      }
    }

    // Initialize the partial order hierarchy
    debugProgress.fine("Init Hierarchy ... ");
    if (FileIO.new_decl_format)
      PptRelation.init_hierarchy_new (all_ppts);
    else
      PptRelation.init_hierarchy(all_ppts);
    debugProgress.fine("Init Hierarchy ... done");

    // Calculate invariants at all non-leaf ppts
    if (use_dataflow_hierarchy) {
      debugProgress.fine("createUpperPpts");
      createUpperPpts(all_ppts);
      debugProgress.fine("createUpperPpts ... done");
    }

    // Equality data for each PptTopLevel.
    if (Daikon.use_equality_optimization && !Daikon.dkconfig_undo_opts) {
      debugProgress.fine("Equality Post Process ... ");
      for (Iterator<PptTopLevel> itor = all_ppts.ppt_all_iterator();
        itor.hasNext();
        ) {
        PptTopLevel ppt = itor.next();
        ppt.postProcessEquality();
      }
      debugProgress.fine("Equality Post Process ... done");
    }

    // undo optimizations; results in a more redundant but more complete
    // set of invariants
    if (Daikon.dkconfig_undo_opts) {
      undoOpts(all_ppts);
    }

    // Debug print information about equality sets
    if (debugEquality.isLoggable(Level.FINE)) {
      for (Iterator<PptTopLevel> itor = all_ppts.ppt_all_iterator();
        itor.hasNext();
        ) {
        PptTopLevel ppt = itor.next();
        debugEquality.fine(ppt.name() + ": " + ppt.equality_sets_txt());
      }
    }

    debugProgress.fine ("Time spent on non-implication postprocessing: "
                        + stopwatch.format());

    isInferencing = false;

    // Add implications
    stopwatch.reset();
    fileio_progress.clear();
    if (! Daikon.dkconfig_disable_splitting) {
      // This isn't helpful to users.  Perhaps add an option that prints it.
      // if (!Daikon.dkconfig_quiet) {
      //   System.out.println("Creating implications");
      // }
      debugProgress.fine("Adding Implications ... ");
      for (Iterator<PptTopLevel> itor = all_ppts.pptIterator(); itor.hasNext();) {
        PptTopLevel ppt = itor.next();
        // debugProgress.fine ("  Adding Implications for " + ppt.name);
        ppt.addImplications();
      }
      debugProgress.fine("Time spent adding implications: "
                         + stopwatch.format());
    }
  }

  private static class Count {
    public int val;
    Count (int val) {
      this.val = val;
    }
  }

  /**
   * Print out basic statistics (samples, invariants, variables, etc)
   * about each ppt
   */
  public static void ppt_stats (PptMap all_ppts) {

    int all_ppt_cnt = 0;
    int ppt_w_sample_cnt = 0;
    for (Iterator<PptTopLevel> i = all_ppts.pptIterator(); i.hasNext(); ) {
      PptTopLevel ppt = i.next();
      all_ppt_cnt++;
      if (ppt.num_samples() == 0)
        continue;
      ppt_w_sample_cnt++;
      Fmt.pf ("%s", ppt.name());
      Fmt.pf ("  samples    = " + ppt.num_samples());
      Fmt.pf ("  invariants = " + ppt.invariant_cnt());
      Map<ProglangType,Count> type_map = new LinkedHashMap<ProglangType,Count>();
      int leader_cnt = 0;
      for (VarInfo v : ppt.var_infos) {
        if (!v.isCanonical())
          continue;
        leader_cnt++;
        Count cnt = type_map.get (v.file_rep_type);
        if (cnt == null)
          type_map.put (v.file_rep_type, cnt = new Count(0));
        cnt.val++;
      }
      Fmt.pf ("  vars       = " + ppt.var_infos.length);
      Fmt.pf ("  leaders    = " + leader_cnt);
      for (Map.Entry<ProglangType,Count> e : type_map.entrySet()) {
        ProglangType file_rep_type = e.getKey();
        Count cnt = e.getValue();
        Fmt.pf ("  %s  = %s", file_rep_type, "" + cnt.val);
      }
    }
    Fmt.pf ("Total ppt count     = " + all_ppt_cnt);
    Fmt.pf ("PPts w/sample count = " + ppt_w_sample_cnt);
  }

  /**
   * Process the invariants with simplify to remove redundant invariants
   */
  private static void suppressWithSimplify(PptMap all_ppts) {
    System.out.print("Invoking Simplify to identify redundant invariants");
    System.out.flush();
    stopwatch.reset();
    for (Iterator<PptTopLevel> itor = all_ppts.ppt_all_iterator(); itor.hasNext();) {
      PptTopLevel ppt = itor.next();
      ppt.mark_implied_via_simplify(all_ppts);
      System.out.print(".");
      System.out.flush();
    }
    System.out.println(stopwatch.format());
  }

  /**
   * Initialize NIS suppression
   */
  public static void setup_NISuppression() {
    NIS.init_ni_suppression();
  }

  /**
   * Initialize the equality sets for each variable
   */

  public static void setupEquality (PptTopLevel ppt) {

    if (!Daikon.use_equality_optimization)
      return;

    // Skip points that are not leaves.
    if (use_dataflow_hierarchy) {
      PptTopLevel p = ppt;
      if (ppt instanceof PptConditional)
        p = ((PptConditional)ppt).parent;

      // Rather than defining leaves as :::GLOBAL or :::EXIT54 (numbered
      // exit), we define them as everything except
      // ::EXIT (combined), :::ENTER, :::THROWS, :::OBJECT
      //  and :::CLASS program points.  This scheme ensures that arbitrarly
      //  named program points such as :::POINT (used by convertcsv.pl)
      //  will be treated as leaves.
      if (p.ppt_name.isCombinedExitPoint() ||
          p.ppt_name.isEnterPoint() ||
          p.ppt_name.isThrowsPoint() ||
          p.ppt_name.isObjectInstanceSynthetic() ||
          p.ppt_name.isClassStaticSynthetic()) {
        return;
      }

      if (ppt.has_splitters())
        return;
    }

    // Create the initial equality sets
    ppt.equality_view = new PptSliceEquality(ppt);
    ppt.equality_view.instantiate_invariants();
  }

  /**
   * Create user defined splitters
   */

  private static List<SpinfoFileParser> parsedSplitters = new ArrayList<SpinfoFileParser>();

  public static void create_splitters(Set<File> spinfo_files)
    throws IOException {
    for (File filename : spinfo_files) {
      SpinfoFileParser p = SplitterFactory.parse_spinfofile (filename);
      parsedSplitters.add(p);
    }
  }


//   /**
//    * Guard the invariants at all PptTopLevels. Note that this changes
//    * the contents of the PptTopLevels, and the changes made should
//    * probably not be written out to an inv file (save the file before
//    * this is called).
//    */
//   public static void guardInvariants(PptMap allPpts) {
//     for (PptTopLevel ppt : allPpts.asCollection()) {
//       if (ppt.num_samples() == 0)
//         continue;
//       // Make sure isDerivedParam is set before guarding.  Otherwise
//       // we'll never get it correct.
//       for (int iVarInfo = 0;
//         iVarInfo < ppt.var_infos.length;
//         iVarInfo++) {
//         boolean temp =
//           ppt.var_infos[iVarInfo].isDerivedParamAndUninteresting();
//       }
//
//       ppt.guardInvariants();
//     }
//   }

  /**
   * Removed invariants as specified in omit_types
   */
  private static void processOmissions(PptMap allPpts) {
    if (omit_types['0'])
      allPpts.removeUnsampled();
    for (PptTopLevel ppt : allPpts.asCollection()) {
      ppt.processOmissions(omit_types);
    }
  }

  /**
   * Returns the max ppt that corresponds to the specified percentage
   * of ppts (presuming that only those ppts <= max_ppt will be
   * processed).
   */
  private static String setup_ppt_perc(Collection<File> decl_files, int ppt_perc) {

    // Make sure the percentage is valid
    if ((ppt_perc < 1) || (ppt_perc > 100))
      // The number should already have been checked, so use Error instead of Daikon.TerminationMessage
      throw new Error(
        "ppt_perc of " + ppt_perc + " is out of range 1..100");
    if (ppt_perc == 100)
      return null;

    // Keep track of all of the ppts in a set ordered by the ppt name
    Set<String> ppts = new TreeSet<String>();

    // Read all of the ppt names out of the decl files
    try {
      for (File file : decl_files) {

        // Open the file
        LineNumberReader fp = UtilMDE.lineNumberFileReader(file);

        // Read each ppt name from the file
        for (String line = fp.readLine();
             line != null;
             line = fp.readLine()) {
          if (line.equals("") || FileIO.isComment(line))
            continue;
          if (!line.equals("DECLARE"))
            continue;
          String ppt_name = fp.readLine();
          ppts.add(ppt_name);
        }

        fp.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new Error(e);
    }

    // Determine the ppt_name that matches the specified percentage.  Always
    // return the last exit point from the method (so we don't get half the
    // exits from a method or enters without exits, etc)
    int ppt_cnt = (ppts.size() * ppt_perc) / 100;
    if (ppt_cnt == 0)
      throw new Daikon.TerminationMessage(
        "ppt_perc of "
        + ppt_perc
        + "% results in processing 0 out of "
        + ppts.size()
        + " ppts");
    for (Iterator<String> i = ppts.iterator(); i.hasNext();) {
      String ppt_name = i.next();
      if (--ppt_cnt <= 0) {
        String last_ppt_name = ppt_name;
        while (i.hasNext()) {
          ppt_name = i.next();
          if ((last_ppt_name.indexOf("EXIT") != -1)
            && (ppt_name.indexOf("EXIT") == -1))
            return (last_ppt_name);
          last_ppt_name = ppt_name;
        }
        return (ppt_name);
      }
    }
    // Execution should not reach this line
    throw new Error("ppt_cnt " + ppt_cnt + " ppts.size " + ppts.size());
  }

 /**
  * Undoes the invariants suppressed for the dynamic constant,
  * suppression and equality set optimizations (should yield the same
  * invariants as the simple incremental algorithm
  */
  public static void undoOpts(PptMap all_ppts) {

    //undo suppressions
    Iterator<PptTopLevel> suppress_it = all_ppts.ppt_all_iterator();

    while (suppress_it.hasNext()) {
      PptTopLevel p = suppress_it.next();
      NIS.create_suppressed_invs(p);
    }

    //undo equality sets
    Iterator<PptTopLevel> equality_it = all_ppts.ppt_all_iterator();
    while (equality_it.hasNext()) {

      PptTopLevel ppt = equality_it.next();
      PptSliceEquality sliceEquality = ppt.equality_view;

      // some program points have no equality sets?
       if (sliceEquality == null) {
         // System.out.println(ppt.name);
         continue;
       }


      // get the new leaders
      List<Equality> allNewInvs = new ArrayList<Equality>();

      for (Invariant eq_as_inv : sliceEquality.invs) {
        Equality eq = (Equality) eq_as_inv;
        VarInfo leader = eq.leader();
        List<VarInfo> vars = new ArrayList<VarInfo>();

        for (VarInfo var : eq.getVars()) {
          if (!var.equals(leader)) {
            vars.add(var);
          }
        }

        if (vars.size() > 0) {

          // Create new equality sets for all of the non-equal vars
          List<Equality> newInvs = sliceEquality.createEqualityInvs(vars, eq);

          // Create new slices and invariants for each new leader
          // copyInvsFromLeader(sliceEquality, leader, vars);
          sliceEquality.copyInvsFromLeader(leader, vars);

          // Keep track of all of the new invariants created.
          // Add all of the new equality sets to our list
          allNewInvs.addAll(newInvs);
        }
      }

      sliceEquality.invs.addAll(allNewInvs);

    }
  }
}
