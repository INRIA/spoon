package daikon;

import daikon.derive.ValueAndModified;
import daikon.config.Configuration;
import daikon.diff.InvMap;
import daikon.inv.Invariant;
import static daikon.PptRelation.PptRelationType;
import static daikon.PptTopLevel.PptFlags;
import static daikon.PptTopLevel.PptType;
import static daikon.VarInfo.RefType;
import static daikon.VarInfo.VarKind;
import static daikon.VarInfo.VarFlags;
import static daikon.VarInfo.LangFlags;

import checkers.quals.Interned;

import utilMDE.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.*;
import java.io.Serializable;
import java.net.*;
import java.util.*;

public final class FileIO {

  /** Nobody should ever instantiate a FileIO. **/
  private FileIO() {
    throw new Error();
  }

  /// Constants

  static final String declaration_header = "DECLARE";

  // Program point name tags
  public static final String ppt_tag_separator = ":::";
  public static final String enter_suffix = "ENTER";
  public static final String enter_tag = ppt_tag_separator + enter_suffix;
  // EXIT does not necessarily appear at the end of the program point name;
  // a number may follow it.
  public static final String exit_suffix = "EXIT";
  public static final String exit_tag = ppt_tag_separator + exit_suffix;
  public static final String throws_suffix = "THROWS";
  public static final String throws_tag = ppt_tag_separator + throws_suffix;
  public static final String object_suffix = "OBJECT";
  public static final String object_tag = ppt_tag_separator + object_suffix;
  public static final String class_static_suffix = "CLASS";
  public static final String class_static_tag = ppt_tag_separator
                                                        + class_static_suffix;
  public static final String global_suffix = "GLOBAL";

  private static final String lineSep = Global.lineSep;


  /// Settings

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.

  /**
   * When true, just ignore exit ppts that don't have a matching enter
   * ppt rather than exiting with an error.  Unmatched exits can occur
   * if only a portion of a dtrace file is processed
   */
  public static boolean dkconfig_ignore_missing_enter = false;

  /**
   * Boolean.  When false, set modbits to 1 iff the printed
   * representation has changed.  When true, set modbits to 1 if the
   * printed representation has changed; leave other modbits as is.
   **/
  public static boolean dkconfig_add_changed = true;

  /**
   * Integer.  Maximum number of lines to read from the dtrace file.  If
   * 0, reads the entire file.
   */
  public static int dkconfig_max_line_number = 0;

  /**
   * Boolean. When false, don't count the number of lines in the dtrace file
   * before reading.  This will disable the percentage progress printout.
   */
  public static boolean dkconfig_count_lines = true;

  /**
   * Boolean.  When true, only read the samples, but don't process them.
   * Used to gather timing information.
   */
  public static boolean dkconfig_read_samples_only = false;

  /** Boolean.  When true, don't print a warning about unmatched procedure
   * entries, which are ignored by Daikon (unless the --nohierarchy switch
   * is provided).
   **/
  public static boolean dkconfig_unmatched_procedure_entries_quiet = false;

  /**
   * Boolean.  If true, prints the unmatched procedure entries
   * verbosely.
   **/
  public static boolean dkconfig_verbose_unmatched_procedure_entries = false;

  /**
   * Boolean.  When true, suppress exceptions related to file reading.
   * This permits Daikon to continue even if there is a malformed trace
   * file.  Use this with care:  in general, it is better to fix the
   * problem that caused a bad trace file, rather than to suppress the
   * exception.
   **/
  public static boolean dkconfig_continue_after_file_exception = false;

  /**
   * Long integer. If non-zero, this value will be used as the number
   * of lines in (each) dtrace file input for the purposes of the
   * progress display, and the counting of the lines in the file will
   * be suppressed.
   */
  public static long dkconfig_dtrace_line_count = 0;

  /** True if declaration records are in the new format **/
  public static boolean new_decl_format = true;

  /// Variables

  // This hashmap maps every program point to an array, which contains the
  // old values of all variables in scope the last time the program point
  // was executed. This enables us to determine whether the values have been
  // modified since this program point was last executed.
  static HashMap<PptTopLevel,String[]> ppt_to_value_reps = new HashMap<PptTopLevel,String[]>();

  // For debugging purposes: printing out a modified trace file with
  // changed modbits.
  private static boolean to_write_nonce = false;
  private static String nonce_value, nonce_string;

  // (This implementation as a public static variable is a bit unclean.)
  // Number of ignored declarations.
  public static int omitted_declarations = 0;

  // Logging Categories

  /** true prints info about variables marked as missing/nonsensical **/
  public static boolean debug_missing = false;

  /** Debug tracer for reading. **/
  public static final Logger debugRead = Logger.getLogger("daikon.FileIO.read");
  /** Debug tracer for printing. **/
  public static final Logger debugPrint =
    Logger.getLogger("daikon.FileIO.printDtrace");

  /** Debug tracer for printing variable values. **/
  public static final Logger debugVars = Logger.getLogger("daikon.FileIO.vars");

  public static final SimpleLog debug_decl = new SimpleLog(false);

  /** Errors while processing ppt declarations */
  public static class DeclError extends IOException {

    static final long serialVersionUID = 20060518L;

    public DeclError (String msg) {
      super (msg);
    }

    public static DeclError detail (ParseState state, String format,
                                    Object... args) {
      String msg = String.format (format, args)
        + String.format (" at line %d in file %s",
                 state.reader.getLineNumber(), state.filename);
      return new DeclError (msg);
    }
  }

  /**
   * Parents in the ppt/variable hierarchy for a particular program point
   */
  static final class ParentRelation implements java.io.Serializable {
    static final long serialVersionUID = 20060622L;
    PptRelationType rel_type;
    String parent_ppt_name;
    int id;
    public String toString() { return parent_ppt_name + "[" + id + "] "
                                 + rel_type; };
  }

  // Utilities
  // The Daikon manual states that "#" is the comment starter, but
  // some code assumes "//", so permit both (at least temporarily).
  // static final String comment_prefix = "//";
  public static final boolean isComment(String s) {
    return s.startsWith("//") || s.startsWith("#");
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Declaration files
  ///

  /**
   * @param files files to be read (java.io.File)
   * @return a new PptMap containing declarations read from the files
   * listed in the argument; connection information (controlling
   * variables and entry ppts) is set correctly upon return.
   **/
  public static PptMap read_declaration_files(Collection<File> files) throws IOException {
    PptMap all_ppts = new PptMap();
    // Read all decls, creating PptTopLevels and VarInfos
    for (File file : files) {
      Daikon.progress = "Reading " + file;
      if (!Daikon.dkconfig_quiet) {
        System.out.print("."); // show progress
      }
      read_declaration_file(file, all_ppts);
    }
    return all_ppts;
  }

  /** Read one decls file; add it to all_ppts. **/
  public static void read_declaration_file(File filename, PptMap all_ppts)
    throws IOException {
    if (Daikon.using_DaikonSimple) {
      Processor processor = new DaikonSimple.SimpleProcessor();
      read_data_trace_file(filename.toString(), all_ppts, processor, true,
                           false);
    } else {
      Processor processor = new Processor();
      read_data_trace_file(filename.toString(), all_ppts, processor, true,
                           true);
    }

  }

  /**
   * Reads one ppt declaration.  The next line should be the ppt record.
   * After completion, the file pointer will be pointing at the next
   * record (ie, the blank line at the end of the ppt declaration will
   * have been read in)
   */
  private static PptTopLevel read_ppt_decl (ParseState state, String top_line)
    throws IOException {

    // process the ppt record
    String line = top_line;
    Scanner scanner = new Scanner (line);
    /*@Interned*/ String record_name = need (state, scanner, "'ppt'");
    if (record_name != "ppt") { // interned
      decl_error (state, "found '%s' where 'ppt' expected", record_name);
    }
    /*@Interned*/ String ppt_name = need (state, scanner, "ppt name");

    // Check to see if the program point is new
    if (state.all_ppts.containsName(ppt_name)) {
      if (state.ppts_are_new) {
        decl_error (state, "Duplicate declaration of ppt '%s'", ppt_name);
      } else { // ppts are already in the map
        skip_decl (state.reader);
        return state.all_ppts.get (ppt_name);
      }
    }

    // Information that will populate the new program point
    Map<String,VarDefinition> varmap
      = new LinkedHashMap<String,VarDefinition>();
    VarDefinition vardef = null;
    List<ParentRelation> ppt_parents = new ArrayList<ParentRelation>();
    EnumSet<PptFlags> ppt_flags = EnumSet.noneOf (PptFlags.class);
    PptType ppt_type = PptType.POINT;

    // Read the records that define this program point
    while ((line = state.reader.readLine()) != null) {
      debug_decl.log ("read line %s%n", line);
      line = line.trim();
      if (line.length() == 0)
        break;

      scanner = new Scanner (line);
      /*@Interned*/ String record = scanner.next().intern();
      if (vardef == null) {
        if (record == "parent") { // interned
          ppt_parents.add (parse_ppt_parent (state, scanner));
        } else if (record == "flags") { // interned
          parse_ppt_flags (state, scanner, ppt_flags);
        } else if (record == "variable") { // interned
          vardef = new VarDefinition (state, scanner);
          if (var_included (vardef.name))
            varmap.put (vardef.name, vardef);
        } else if (record == "ppt-type") { // interned
          ppt_type = parse_ppt_type (state, scanner);
        } else {
          decl_error (state, "record '%s' found where %s expected", record,
                      "'parent' or 'flags'");
        }
      } else { // there must be a current variable
        if (record == "var-kind") { // interned
          vardef.parse_var_kind (scanner);
        } else if (record == "enclosing-var") { // interned
          vardef.parse_enclosing_var (scanner);
        } else if (record == "reference-type") { // interned
          vardef.parse_reference_type (scanner);
        } else if (record == "array") { // interned
          vardef.parse_array (scanner);
        } else if (record == "rep-type") { // interned
          vardef.parse_rep_type (scanner);
        } else if (record == "dec-type") { // interned
          vardef.parse_dec_type (scanner);
        } else if (record == "flags") { // interned
          vardef.parse_flags (scanner);
        } else if (record == "lang-flags") { // interned
          vardef.parse_lang_flags (scanner);
        } else if (record == "parent") { // interned
          vardef.parse_parent (scanner, ppt_parents);
        } else if (record == "comparability") { // interned
          vardef.parse_comparability (scanner);
        } else if (record == "constant") { // interned
          vardef.parse_constant (scanner);
        } else if (record == "variable") { // interned
          vardef = new VarDefinition (state, scanner);
          if (varmap.containsKey (vardef.name))
            decl_error (state, "var %s declared twice", vardef.name);
          if (var_included (vardef.name))
            varmap.put (vardef.name, vardef);
        } else {
          decl_error (state, "Unexpected variable item '%s' found", record);
        }
      }
    }

    // If we are excluding this ppt, just read the data and throw it away
    if (!ppt_included (ppt_name)) {
      omitted_declarations++;
      return null;
    }

    VarInfo[] vi_array = new VarInfo[varmap.size()];
    int ii = 0;
    for (VarDefinition vd : varmap.values()) {
      vi_array[ii++] = new VarInfo (vd);
    }

    PptTopLevel newppt = new PptTopLevel(ppt_name, ppt_type, ppt_parents,
                                         ppt_flags, vi_array);
    return newppt;
  }

  /** Parses a ppt parent hierarchy record and returns it. **/
  private static ParentRelation parse_ppt_parent (ParseState state,
       Scanner scanner) throws DeclError {

    ParentRelation pr = new ParentRelation();
    pr.rel_type = parse_enum_val (state, scanner, PptRelationType.class,
                                  "relation type");
    pr.parent_ppt_name = need (state, scanner, "ppt name");
    pr.id = Integer.parseInt (need (state, scanner, "relation id"));
    need_eol (state, scanner);
    return (pr);
  }

  /**
   * Parses a program point flag record.  Adds any specified flags to
   * to flags.
   */
  private static void parse_ppt_flags (ParseState state, Scanner scanner,
                                  EnumSet<PptFlags> flags) throws DeclError {

    flags.add (parse_enum_val (state, scanner, PptFlags.class, "ppt flags"));
    while (scanner.hasNext())
      flags.add (parse_enum_val (state, scanner, PptFlags.class, "ppt flags"));
  }

  /** Parses a ppt-type record and returns the type **/
  private static PptType parse_ppt_type (ParseState state, Scanner scanner)
    throws DeclError {

    PptType ppt_type
      = parse_enum_val (state, scanner, PptType.class, "ppt type");
    need_eol (state, scanner);
    return (ppt_type);
  }


  // The "DECLARE" line has already been read.
  private static PptTopLevel read_declaration(ParseState state)
    throws IOException {

    // We have just read the "DECLARE" line.
    String ppt_name = state.reader.readLine();
    if (ppt_name == null) {
      throw new Daikon.TerminationMessage(
        "File ends with \"DECLARE\" with no following program point name",
        state.reader, state.filename);
    }
    ppt_name = ppt_name.intern();
    VarInfo[] vi_array = read_VarInfos(state, ppt_name);

    // System.out.printf ("Ppt %s with %d variables\n", ppt_name,
    //                   vi_array.length);

    // This program point name has already been encountered.
    if (state.all_ppts.containsName(ppt_name)) {
      if (state.ppts_are_new) { // yoav: ppts_are_new is always set to true, so we should remove it
        PptTopLevel existing_ppt = state.all_ppts.get(ppt_name);
        VarInfo[] existing_vars = existing_ppt.var_infos;
        if (existing_ppt.num_declvars!=vi_array.length) {
          throw new Daikon.TerminationMessage("Duplicate declaration of program point \""
                      + ppt_name + "\" with a different number of VarInfo objects: old VarInfo number="+existing_ppt.num_declvars+", new VarInfo number="+vi_array.length,
                                               state.reader, state.filename);
        }

        for (int i=0; i<vi_array.length; i++) {
          String oldName = existing_vars[i].str_name();
          String newName = vi_array[i].str_name();
          if (!oldName.equals(newName)) {
            throw new Daikon.TerminationMessage("Duplicate declaration of program point \""
                                                 + ppt_name + "\" with two different VarInfo: old VarInfo="+oldName+", new VarInfo="+newName, state.reader, state.filename);
          }
        }
      } else { // ppts are already in the map
        return state.all_ppts.get (ppt_name);
      }
    }

    // If we are excluding this ppt, just throw it away
    if (!ppt_included (ppt_name)) {
      omitted_declarations++;
      return null;
    }


    // taking care of visibility information
    // the information is needed in the variable hierarchy because private methods
    // should not be linked under the object program point
    // the ppt name is truncated before putting it in the pptMap because the visibility
    // information is only present in the decls file and not the dtrace file

    //    if (ppt_name.startsWith("public")) {
    //      int position = ppt_name.indexOf("public");
    //      ppt_name = ppt_name.substring(7);
    //      PptTopLevel newppt = new PptTopLevel(ppt_name, vi_array);
    //      newppt.ppt_name.setVisibility("public");
    //      return newppt;
    //    }
    //    if (ppt_name.startsWith("private")) {
    //      int position = ppt_name.indexOf("private");
    //      ppt_name = ppt_name.substring(8);
    //      PptTopLevel newppt = new PptTopLevel(ppt_name, vi_array);
    //      newppt.ppt_name.setVisibility("private");
    //      return newppt;
    //    }
    //    if (ppt_name.startsWith("protected")) {
    //      int position = ppt_name.indexOf("protected");
    //      ppt_name = ppt_name.substring(10);
    //      PptTopLevel newppt = new PptTopLevel(ppt_name, vi_array);
    //      newppt.ppt_name.setVisibility("protected");
    //      return newppt;
    //    }

    //TODO: add a new config variable to turn this accessibility flag processing on?
    PptTopLevel newppt = new PptTopLevel(ppt_name, vi_array);
    // newppt.ppt_name.setVisibility("package-protected");
    return newppt;
    // return new PptTopLevel(ppt_name, vi_array);
  }
  private static VarInfo[] read_VarInfos(ParseState state, String ppt_name)
    throws IOException {

    // The var_infos that will populate the new program point
    List<VarInfo> var_infos = new ArrayList<VarInfo>();

    // Each iteration reads a variable name, type, and comparability.
    // Possibly abstract this out into a separate function??
    VarInfo vi;
    while ((vi = read_VarInfo(state, ppt_name)) != null) {
      for (VarInfo vi2 : var_infos) {
        if (vi.name() == vi2.name()) {
          throw new Daikon.TerminationMessage("Duplicate variable name " + vi.name(), state.reader,
                                           state.filename);
        }
      }
      // Can't do this test in read_VarInfo, it seems, because of the test
      // against null above.
      if (!var_included (vi.name())) {
        continue;
      }
      var_infos.add(vi);
    }

    return var_infos.toArray(new VarInfo[var_infos.size()]);
  }

  // So that warning message below is only printed once
  private static boolean seen_string_rep_type = false;

  /**
   * Read a variable name, type, and comparability; construct a VarInfo.
   * Return null after reading the last variable in this program point
   * declaration.
   **/
  private static VarInfo read_VarInfo(
    ParseState state,
    String ppt_name)
    throws IOException {
    LineNumberReader file = state.reader;
    int varcomp_format = state.varcomp_format;
    File filename = state.file;

    String line = file.readLine();
    if ((line == null) || (line.equals("")))
      return null;
    String varname = line;
    String proglang_type_string_and_aux = file.readLine();
    String file_rep_type_string = file.readLine();
    String comparability_string = file.readLine();
    if ( // (varname == null) || // just cheeck varname above
        (proglang_type_string_and_aux == null)
        || (file_rep_type_string == null)
        || (comparability_string == null))
      throw new Daikon.TerminationMessage(
        "End of file "
          + filename
          + " while reading variable "
          + varname
          + " in declaration of program point "
          + ppt_name);
    int equals_index = file_rep_type_string.indexOf(" = ");
    String static_constant_value_string = null;
    Object static_constant_value = null;
    boolean is_static_constant = false;
    if (equals_index != -1) {
      is_static_constant = true;
      static_constant_value_string =
        file_rep_type_string.substring(equals_index + 3);
      file_rep_type_string = file_rep_type_string.substring(0, equals_index);
    }
    // XXX temporary, for compatibility with older .dtrace files.  12/20/2001
    if ("String".equals(file_rep_type_string)) {
      file_rep_type_string = "java.lang.String";
      if (!seen_string_rep_type) {
        seen_string_rep_type = true;
        System.err.println("Warning: Malformed trace file.  Representation type 'String' should be "+
                           "'java.lang.String' instead on line " +
                           (file.getLineNumber()-1) + " of " + filename);
      }
    }
    // This is for people who were confused by the above temporary
    // workaround when it didn't have a warning. But this has never
    // worked, so it's fatal.
    else if ("String[]".equals(file_rep_type_string)) {
      throw new Daikon.TerminationMessage("Representation type 'String[]' should be " +
                                           "'java.lang.String[]' instead for variable " + varname,
                                           file, filename);
    }
    /// XXX

    int hash_position = proglang_type_string_and_aux.indexOf('#');
    String aux_string = "";
    if (hash_position == -1) {
      hash_position = proglang_type_string_and_aux.length();
    } else {
      aux_string =
        proglang_type_string_and_aux.substring(
          hash_position + 1,
          proglang_type_string_and_aux.length());
    }

    String proglang_type_string =
      proglang_type_string_and_aux.substring(0, hash_position).trim();

    ProglangType prog_type;
    ProglangType file_rep_type;
    ProglangType rep_type;
    VarInfoAux aux;
    try {
      prog_type = ProglangType.parse(proglang_type_string);
      file_rep_type = ProglangType.rep_parse(file_rep_type_string);
      rep_type = file_rep_type.fileTypeToRepType();
      aux = VarInfoAux.parse(aux_string);
    } catch (IOException e) {
      throw new Daikon.TerminationMessage(file, filename, e);
    }

    if (static_constant_value_string != null) {
      static_constant_value =
        rep_type.parse_value(static_constant_value_string);
      // Why can't the value be null?
      Assert.assertTrue(static_constant_value != null);
    }
    VarComparability comparability = null;
    try {
      comparability = VarComparability.parse(varcomp_format,
                                             comparability_string, prog_type);
    } catch (Exception e) {
      throw new Daikon.TerminationMessage
        (String.format ("Error parsing comparability (%s) at line %d "
                        + "in file %s", e, file.getLineNumber(), filename));
    }
    // Not a call to Assert.assert in order to avoid doing the (expensive)
    // string concatenations.
    if (!VarInfo.legalFileRepType(file_rep_type)) {
      throw new Daikon.TerminationMessage(
        "Unsupported representation type "
          + file_rep_type.format()
          + " (parsed as "
          + rep_type
          + ")"
          + " for variable "
          + varname,
        file,
        filename);
    }
    if (!VarInfo.legalRepType(rep_type)) {
      throw new Daikon.TerminationMessage(
        "Unsupported (converted) representation type "
          + file_rep_type.format()
          + " for variable "
          + varname,
        file,
        filename);
    }
    // COMPARABILITY TEST
    // if (!(comparability.alwaysComparable()
    //       || ((VarComparabilityImplicit)comparability).dimensions == file_rep_type.dimensions())) {
    //   throw new FileIOException(
    //     "Rep type " + file_rep_type.format() + " has " + file_rep_type.dimensions() + " dimensions"
    //       + " but comparability " + comparability + " has " + ((VarComparabilityImplicit)comparability).dimensions + " dimensions"
    //       + " for variable "
    //       + varname,
    //     file,
    //     filename);
    // }

    return new VarInfo(varname,
      prog_type,
      file_rep_type,
      comparability,
      is_static_constant,
      static_constant_value,
      aux);
  }

  private static int read_var_comparability (ParseState state, String line)
    throws IOException {

    // System.out.printf("read_var_comparability, line = '%s' %b%n", line,
    //                   new_decl_format);
    String comp_str = null;
    if (new_decl_format) {
      Scanner scanner = new Scanner (line);
      scanner.next();
      comp_str = need (state, scanner, "comparability");
      need_eol (state, scanner);
    } else { // old format
      comp_str = state.reader.readLine();
      if (comp_str == null) {
        throw new Daikon.TerminationMessage("Found end of file, expected comparability",
                                         state.reader, state.filename);
      }
    }

    if (comp_str.equals("none")) {
      return (VarComparability.NONE);
    } else if (comp_str.equals("implicit")) {
      return (VarComparability.IMPLICIT);
    } else {
      throw new Daikon.TerminationMessage("Unrecognized VarComparability '" + comp_str
                                       + "'", state.reader, state.filename);
    }
  }

  private static /*@Interned*/ String read_input_language (ParseState state, String line)
    throws IOException {

    Scanner scanner = new Scanner (line);
    scanner.next();
    /*@Interned*/ String input_lang = need (state, scanner, "input language");
    need_eol (state, scanner);
    return input_lang;
  }

  private static void read_decl_version (ParseState state, String line)
    throws IOException {
    Scanner scanner = new Scanner (line);
    scanner.next();
    /*@Interned*/ String version = need (state, scanner, "declaration version number");
    need_eol (state, scanner);
    if (version == "2.0")       // interned
      new_decl_format = true;
    else if (version == "1.0")  // interned
      new_decl_format = false;
    else
      decl_error (state, "'%s' found where 1.0 or 2.0 expected",
                  version);
  }

  private static void read_list_implementors (LineNumberReader reader,
                                              File filename)
    throws IOException {
    // Each line following is the name (in JVM form) of a class
    // that implements java.util.List.
    for (;;) {
      String line = reader.readLine();
      if (line == null || line.equals(""))
        break;
      if (isComment(line))
        continue;
      ProglangType.list_implementors.add(line.intern());
    }
  }






  ///////////////////////////////////////////////////////////////////////////
  /// invocation tracking for dtrace files entry/exit grouping
  ///

  static final class Invocation implements Comparable<Invocation> {
    PptTopLevel ppt; // used in printing and in suppressing duplicates
    // Rather than a valuetuple, place its elements here.
    Object[] vals;
    int[] mods;

    static Object canonical_hashcode = new Object();

    Invocation(PptTopLevel ppt, Object[] vals, int[] mods) {
      this.ppt = ppt;
      this.vals = vals;
      this.mods = mods;
    }

    // Print the Invocation on two lines, indented by two spaces
    String format() {
      return format(true);
    }

    // Print the Invocation on one or two lines, indented by two spaces
    String format(boolean show_values) {
      if (! show_values) {
        return "  " + ppt.ppt_name.getNameWithoutPoint();
      }

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("  " + ppt.ppt_name.getNameWithoutPoint());
      pw.print("    ");

      // [adonovan] is this sound? Let me know if not (sorry).
      //Assert.assertTrue(ppt.var_infos.length == vals.length);

      for (int j = 0; j < vals.length; j++) {
        if (j != 0)
          pw.print(", ");

        pw.print(ppt.var_infos[j].name() + "=");

        Object val = vals[j];
        if (val == canonical_hashcode)
          pw.print("<hashcode>");
        else if (val instanceof int[])
          pw.print(ArraysMDE.toString((int[]) val));
        else if (val instanceof String)
          pw.print(val == null ? "null" : UtilMDE.escapeNonASCII((String) val));
        else
          pw.print(val);
      }
      pw.println();

      return sw.toString();
    }

    /** Change uses of hashcodes to canonical_hashcode. **/
    public Invocation canonicalize() {
      Object[] new_vals = new Object[vals.length];
      System.arraycopy(vals, 0, new_vals, 0, vals.length);
      VarInfo[] vis = ppt.var_infos;
      // Warning: abstraction violation!
      for (VarInfo vi : vis) {
        if ((vi.value_index != -1)
          && (vi.file_rep_type == ProglangType.HASHCODE)) {
          new_vals[vi.value_index] = canonical_hashcode;
        }
      }
      return new Invocation(ppt, new_vals, mods);
    }

    // Return true if the invocations print the same
    public boolean equals(Object other) {
      if (other instanceof FileIO.Invocation)
        return this.format().equals(((FileIO.Invocation) other).format());
      else
        return false;
    }

    public int compareTo(Invocation other) {
      return ppt.name().compareTo(other.ppt.name());
    }

    public int hashCode() {
      return this.format().hashCode();
    }
  }

  // call_hashmap is for procedures with a (global, not per-procedure)
  // nonce that indicates which returns are associated with which entries.
  // call_stack is for functions without nonces.

  // I could save some Object overhead by using two parallel stacks
  // instead of Invocation objects; but that's not worth it.
  static Stack<Invocation> call_stack = new Stack<Invocation>();
  static HashMap<Integer,Invocation> call_hashmap = new HashMap<Integer,Invocation>();

  /** Reads data trace files using the default sample processor. **/
  public static void read_data_trace_files(Collection<String> files,
                                           PptMap all_ppts) throws IOException {

    Processor processor = new Processor();
    read_data_trace_files(files, all_ppts, processor, true);
  }

  /**
   * Read data from .dtrace files.
   * Calls @link{read_data_trace_file(File,PptMap,Pattern,false)} for each
   * element of filenames.
   *
   * @param ppts_are_new - true if declarations of ppts read from the data
   *                       trace file are new (and thus are not in all_ppts)
   *                       false if the ppts may already be there.
   **/
  public static void read_data_trace_files(Collection<String> files,
                PptMap all_ppts, Processor processor, boolean ppts_are_new)
                throws IOException {

    for (String filename : files) {
      try {
        read_data_trace_file(filename, all_ppts, processor, false,
                             ppts_are_new);
      } catch (IOException e) {
        if (e.getMessage().equals("Corrupt GZIP trailer")) {
          System.out.println(
            filename
              + " has a corrupt gzip trailer.  "
              + "All possible data was recovered.");
        } else {
          throw e;
        }
      }
    }
    if (Daikon.server_dir!=null) {
      // Yoav: server mode
      while (true) {
        String[] dir_files = Daikon.server_dir.list();
        Arrays.sort(dir_files);
        boolean hasEnd = false;
        for (String f:dir_files) {
          if (f.endsWith(".end")) hasEnd = true;
          if (f.endsWith(".end") || f.endsWith(".start")) continue;
          if (files.contains(f)) continue;
          files.add(f);
          System.out.println("Reading "+f);
          read_data_trace_file(new File(Daikon.server_dir,f).toString(), all_ppts, processor, false, ppts_are_new);
        }
        if (hasEnd) break;
        try { Thread.sleep(1000); } catch(java.lang.InterruptedException e) {}
      }
    }

    process_unmatched_procedure_entries();

    warn_if_hierarchy_mismatch(all_ppts);
  }

  // Determine if dataflow hierarchy should have been used, and print
  // warning if this does not match Daikon.use_dataflow_hierarchy.
  // Dataflow hierarchy should be used only when all program points
  // correspond to points normally found in traces from a
  // programming languages.
  private static void warn_if_hierarchy_mismatch(PptMap all_ppts) {

    boolean some_program_points = false;
    boolean all_program_points = true;

    // go through each top level ppt, and make all_program_points
    // false if at least one of them is not a program point normally
    // found in traces from programming languages
    for (Iterator<PptTopLevel> all_ppts_iter = all_ppts.ppt_all_iterator();
        all_ppts_iter.hasNext(); ) {
      PptTopLevel ppt_top_level = all_ppts_iter.next();

      boolean is_program_point =
        (ppt_top_level.ppt_name.isExitPoint() ||
        ppt_top_level.ppt_name.isEnterPoint() ||
        ppt_top_level.ppt_name.isThrowsPoint() ||
        ppt_top_level.ppt_name.isObjectInstanceSynthetic() ||
        ppt_top_level.ppt_name.isClassStaticSynthetic() ||
        ppt_top_level.ppt_name.isGlobalPoint());

      all_program_points = all_program_points && is_program_point;
      some_program_points = some_program_points || is_program_point;
    }

    // if all program points correspond to a programming language,
    // but the dataflow hierarchy has been turned off, then
    // suggest not using the --nohierarchy flag
    //    if (all_program_points && (!Daikon.use_dataflow_hierarchy)) {
    //      System.out.println("Warning: data trace appears to be over" +
    //                         " a program execution, but dataflow" +
    //                         " hierarchy has been turned off," +
    //                         " consider running Daikon without the" +
    //                         " --nohierarchy flag");
    //    }

    // if some of the program points do not correspond to a
    // points from a programming language, and the dataflow
    // hierarchy is being used, suggest using the --nohierarchy flag.
    if (Daikon.use_dataflow_hierarchy &&
        (!all_program_points) &&
        some_program_points) {
      System.out.println("Warning: Daikon is using a dataflow" +
                         " hierarchy analysis on a data trace" +
                         " that does not appear to be over a" +
                         " program execution, consider running"+
                         " Daikon with the --nohierarchy flag.");
    }
  }


  /**
   * Class used to specify the processor to use for sample data.  By
   * default, the internal process_sample routine will be called.
   */
  public static class Processor {
    public void process_sample(
                               PptMap all_ppts,
                               PptTopLevel ppt,
                               ValueTuple vt,
                               Integer nonce) {
      FileIO.process_sample(all_ppts, ppt, vt, nonce);
    }
  }


  /** Read data from .dtrace file using standard data processor. **/
  static void read_data_trace_file(String filename, PptMap all_ppts)
    throws IOException {
    Processor processor = new Processor();
    read_data_trace_file(filename, all_ppts, processor, false, true);
  }

  /**
   * Class used to encapsulate state information while parsing
   * decl/dtrace files.
   */

  public enum ParseStatus {
    NULL,               // haven't read anything yet
    DECL,               // got a decl
    SAMPLE,             // got a sample
    COMPARABILITY,      // got a VarComparability declaration
    LIST,               // got a ListImplementors declaration
    EOF,                // found EOF
    ERROR,              // continuable error; fatal errors thrown as exceptions
    TRUNCATED           // dkconfig_max_line_number reached
  };

  public static class ParseState {
    public String filename;
    public boolean is_decl_file;
    public boolean ppts_are_new;
    public PptMap all_ppts;
    public LineNumberReader reader;
    public File file;
    public long total_lines;
    public int varcomp_format;
    public ParseStatus status;
    public PptTopLevel ppt;     // returned when state=DECL or SAMPLE
    public Integer nonce;       // returned when state=SAMPLE
    public ValueTuple vt;       // returned when state=SAMPLE
    public long lineNum;

    public ParseState (String raw_filename, boolean decl_file_p,
                       boolean ppts_are_new, PptMap ppts) throws IOException {
      // Pretty up raw_filename for use in messages
      file = new File(raw_filename);
      if (raw_filename.equals("-")) {
        filename = "standard input";
      }
      else if (raw_filename.equals("+")) {
        filename = "chicory socket";
      }
      else {
        // Remove directory parts, to make it shorter
        filename = file.getName();
      }

      is_decl_file = decl_file_p;
      this.ppts_are_new = ppts_are_new;
      all_ppts = ppts;

      // Do we need to count the lines in the file?
      total_lines = 0;
      boolean count_lines = dkconfig_count_lines;
      if (is_decl_file) {
        count_lines = false;
      } else if (dkconfig_dtrace_line_count != 0) {
        total_lines = dkconfig_dtrace_line_count;
        count_lines = false;
      } else if (filename.equals("-")) {
        count_lines = false;
      } else if (Daikon.dkconfig_progress_delay == -1) {
        count_lines = false;
      } else if ((new File(raw_filename)).length() == 0) {
        // Either it's actually empty, or it's something like a pipe.
        count_lines = false;
      }

      if (count_lines) {
        Daikon.progress = "Checking size of " + filename;
        total_lines = UtilMDE.count_lines(raw_filename);
      } else {
        // System.out.printf ("no count %b %d %s %d %d\n", is_decl_file,
        //                    dkconfig_dtrace_line_count, filename,
        //  Daikon.dkconfig_progress_delay, (new File(raw_filename)).length());
      }

      // Open the reader stream
      if (raw_filename.equals("-")) {
        // "-" means read from the standard input stream
        Reader file_reader = new InputStreamReader(System.in, "ISO-8859-1");
        reader = new LineNumberReader(file_reader);
      }
      else if (raw_filename.equals("+")) { //socket comm with Chicory
        InputStream chicoryInput = connectToChicory();
        InputStreamReader chicReader = new InputStreamReader(chicoryInput);
        reader = new LineNumberReader(chicReader);
      } else {
        reader = UtilMDE.lineNumberFileReader(raw_filename);
      }

      varcomp_format = VarComparability.IMPLICIT;
      status = ParseStatus.NULL;
      ppt = null;
    }
  }

  private static InputStream connectToChicory()
    {


        ServerSocket daikonServer = null;
        try
        {
            daikonServer = new ServerSocket(0); //bind to any free port

            //tell Chicory what port we have!
            System.out.println("DaikonChicoryOnlinePort=" + daikonServer.getLocalPort());

            daikonServer.setReceiveBufferSize(64000);

        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to create server", e);
        }

        Socket chicSocket = null;
        try
        {
            daikonServer.setSoTimeout(5000);

            //System.out.println("waiting for chicory connection on port " + daikonServer.getLocalPort());
            chicSocket = daikonServer.accept();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to connect to Chicory", e);
        }


        try
        {
            return chicSocket.getInputStream();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to get Chicory's input stream", e);
        }

    }


  /** Stash state here to be examined/printed by other parts of Daikon. */
  public static ParseState data_trace_state = null;

  /**
   * Total number of samples passed to process_sample().
   * Not part of data_trace_state because it's global over all files
   * processed by Daikon.
   */
  public static int samples_processed = 0;


  /** Read data from .dtrace file. **/
  static void read_data_trace_file(String filename, PptMap all_ppts,
                                   Processor processor,
                                   boolean is_decl_file, boolean ppts_are_new)
    throws IOException {

    if (debugRead.isLoggable(Level.FINE)) {
      debugRead.fine ("read_data_trace_file " + filename
                      + ((Daikon.ppt_regexp != null)
                         ? " " + Daikon.ppt_regexp.pattern() : "")
                      + ((Daikon.ppt_omit_regexp != null)
                         ? " " + Daikon.ppt_omit_regexp.pattern() : ""));
    }

    new_decl_format = false;

    data_trace_state = new ParseState(filename, is_decl_file, ppts_are_new,
                                      all_ppts);

    // Used for debugging: write new data trace file.
    if (Global.debugPrintDtrace) {
      Global.dtraceWriter
             = new PrintWriter(new FileWriter(new File(filename + ".debug")));
    }

    while (true) {
      read_data_trace_record (data_trace_state);
      if (data_trace_state.status == ParseStatus.SAMPLE) {
        // Keep track of the total number of samples we have seen.
        samples_processed++;
        // Add orig and derived variables; pass to inference (add_and_flow)
        try {
          processor.process_sample (data_trace_state.all_ppts,
                                    data_trace_state.ppt,
                                    data_trace_state.vt,
                                    data_trace_state.nonce);
        } catch (Error e) {
          if (! dkconfig_continue_after_file_exception) {
            throw new Daikon.TerminationMessage(e, data_trace_state.reader, data_trace_state.filename);
          } else {
            System.out.println ();
            System.out.println ("WARNING: Error while processing "
                                + "trace file - record ignored");
            System.out.print ("Ignored backtrace:");
            e.printStackTrace(System.out);
            System.out.println ();
          }
        }
      }
      else if ((data_trace_state.status == ParseStatus.EOF)
               || (data_trace_state.status == ParseStatus.TRUNCATED)) {
        break;
      }
      else
        ;  // don't need to do anything explicit for other records found
    }

    if (Global.debugPrintDtrace) {
      Global.dtraceWriter.close();
    }

    Daikon.progress = "Finished reading " + data_trace_state.filename;
    data_trace_state = null;
  }


  // read a single record (declaration or sample) from a dtrace file.
  public static void read_data_trace_record (ParseState state)
    throws IOException {

    LineNumberReader reader = state.reader;

    // "line_" is uninterned, "line" is interned
    for (String line_ = reader.readLine(); line_ != null;
         line_ = reader.readLine()) {
      if (line_.equals("") || isComment(line_)) {
        continue;
      }
      state.lineNum = reader.getLineNumber();

      // stop at a specified point in the file
      if ((dkconfig_max_line_number > 0)
          && (state.lineNum > dkconfig_max_line_number))
        {
          state.status = ParseStatus.TRUNCATED;
          return;
        }

      String line = line_.intern();

      // First look for declarations in the dtrace stream
      if (is_declaration_header (line)) {
        if (new_decl_format)
          state.ppt = read_ppt_decl (state, line);
        else
          state.ppt = read_declaration(state);
        // ppt can be null if this declaration was skipped because of
        // --ppt-select-pattern or --ppt-omit-pattern.
        if (state.ppt != null) {
          if (!state.all_ppts.containsName (state.ppt.name())) {
            state.all_ppts.add(state.ppt);
            Daikon.init_ppt(state.ppt, state.all_ppts);
          }
        }
        state.status = ParseStatus.DECL;
        return;
      }
      if (line.equals ("VarComparability")
          || line.startsWith ("var-comparability")) {
        state.varcomp_format = read_var_comparability (state, line);
        state.status = ParseStatus.COMPARABILITY;
        return;
      }
      if (line.startsWith ("input-language")) {
        String input_language = read_input_language (state, line);
        return;
      }
      if (line.startsWith ("decl-version")) {
        read_decl_version (state, line);
        return;
      }
      if (line.equals("ListImplementors")) {
        read_list_implementors (reader, state.file);
        state.status = ParseStatus.LIST;
        return;
      }
      String ppt_name = line;
      if (new_decl_format)
        ppt_name = unescape_decl(line).intern();
      if (!ppt_included (ppt_name)) {
        // System.out.printf ("skipping ppt %s\n", line);
        while ((line != null) && !line.equals(""))
          line = reader.readLine();
        continue;
      }
      // System.out.printf ("Not skipping ppt  %s\n", line);

      // If we got here, we're looking at a sample and not a declaration.
      // For compatibility with previous implementation, if this is a
      // declaration file, skip over samples.
      if (state.is_decl_file) {
        if (debugRead.isLoggable(Level.FINE))
          debugRead.fine("Skipping paragraph starting at line "
                         + reader.getLineNumber()
                         + " of file "
                         + state.filename
                         + ": "
                         + line);
        while ((line != null) && (!line.equals("")) && (!isComment(line))) {
          System.out.println("Unrecognized paragraph contains line = `"
                             + line
                             + "'");
          System.out.println(" line: null="
                             + false // (line != null)
                             + " empty="
                             + (line.equals(""))
                             + " comment="
                             + (isComment(line)));
          line = reader.readLine();
        }
        continue;
      }


      // Parse the ppt name
      try {
        new PptName(ppt_name);
      } catch (Throwable t) {
        if (t instanceof Daikon.TerminationMessage)
          throw new Daikon.TerminationMessage ("%s: in %s line %d",
                      t.getMessage(), state.filename, reader.getLineNumber());
        else
          throw new Daikon.TerminationMessage
          (String.format ("Illegal program point name '%s' (%s) in %s line %d",
             ppt_name, t.getMessage(), state.filename, reader.getLineNumber()));
      }

      if (state.all_ppts.size() == 0) {
        throw new Daikon.TerminationMessage("No declarations were provided before the first sample.  Perhaps you did not supply the proper .decls file to Daikon.  (Or, there could be a bug in the front end that created the .dtrace file " + state.filename
                        + ".)");
      }

      PptTopLevel ppt = state.all_ppts.get(ppt_name);
      if (ppt == null) {
        throw new Daikon.TerminationMessage("No declaration was provided for program point " + ppt_name
                        + " which appears in dtrace file " + state.filename
                        + " at line " + reader.getLineNumber());
      }

      VarInfo[] vis = ppt.var_infos;

      // not vis.length, as that includes constants, derived variables, etc.
      // Actually, we do want to leave space for _orig vars.
      // And for the time being (and possibly forever), for derived variables.
      int num_tracevars = ppt.num_tracevars;
      int vals_array_size = ppt.var_infos.length - ppt.num_static_constant_vars;

      // Read an invocation nonce if one exists
      Integer nonce = null;

      // arbitrary number, hopefully big enough; catch exceptions
      reader.mark(100);
      String nonce_name_maybe;
      try {
        nonce_name_maybe = reader.readLine();
      } catch (Exception e) {
        nonce_name_maybe = null;
      }
      reader.reset();
      if ("this_invocation_nonce".equals(nonce_name_maybe)) {

        String nonce_name = reader.readLine();
        Assert.assertTrue(nonce_name != null && nonce_name.equals("this_invocation_nonce"));
        String nonce_number = reader.readLine();
        if (nonce_number == null) {
          throw new Daikon.TerminationMessage("File ended while trying to read nonce",
                                    reader,
                                    state.file);
        }
        nonce = new Integer(nonce_number);

        if (Global.debugPrintDtrace) {
          to_write_nonce = true;
          nonce_value = nonce.toString();
          nonce_string = nonce_name_maybe;
        }
      }

      Object[] vals = new Object[vals_array_size];
      int[] mods = new int[vals_array_size];

      // Read a single record from the trace file;
      // fills up vals and mods arrays by side effect.
      try {
        read_vals_and_mods_from_trace_file (reader, state.filename,
                                            ppt, vals, mods);
      } catch (IOException e) {
        String nextLine = reader.readLine();
        if ((e instanceof EOFException) || (nextLine == null)) {
          System.out.println ();
          System.out.println ("WARNING: Unexpected EOF while processing "
                        + "trace file - last record of trace file ignored");
          state.status = ParseStatus.EOF;
          return;
        } else if (dkconfig_continue_after_file_exception) {
          System.out.println ();
          System.out.println ("WARNING: IOException while processing "
                              + "trace file - record ignored");
          System.out.print ("Ignored backtrace:");
          e.printStackTrace(System.out);
          System.out.println ();
          while (nextLine != null && ! nextLine.equals("")) {
            // System.out.println("Discarded line " + reader.getLineNumber()
            //                     + ": " + nextLine);
            nextLine = reader.readLine();
          }
          continue;
        } else {
          throw e;
        }
      }

      state.ppt = ppt;
      state.nonce = nonce;
      state.vt = ValueTuple.makeUninterned(vals, mods);
      state.status = ParseStatus.SAMPLE;
      return;
    }

    state.status = ParseStatus.EOF;
    return;
  }


  /**
   * Add orig() and derived variables to vt (by side effect), then
   * supply it to the program point for flowing.
   * @param vt trace data only; modified by side effect to add derived vars
   **/
  public static void process_sample(
                                    PptMap all_ppts,
                                    PptTopLevel ppt,
                                    ValueTuple vt,
                                    Integer nonce) {

    // Add orig variables.  This must be above the check below because
    // it saves away the orig values from enter points for later use
    // by exit points.
    boolean ignore = add_orig_variables(ppt, vt.vals, vt.mods, nonce);
    if (ignore)
      return;

    // Only process the leaves of the ppt tree.
    // This test assumes that all leaves are numbered exit program points
    // -- that is, points of the form foo:::EXIT22 for which isExitPoint()
    // is true and isCombinedExitPoint() is false.  "Combined" exit points
    // of the form foo:::EXIT are not processed -- they are assumed to be
    // non-leaves.
    if (Daikon.use_dataflow_hierarchy) {

      // Rather than defining leaves as :::EXIT54 (numbered exit)
      // program points define them as everything except
      // ::EXIT (combined), :::ENTER, :::THROWS, :::OBJECT, ::GLOBAL
      //  and :::CLASS program points.  This scheme ensures that arbitrarly
      //  named program points such as :::POINT (used by convertcsv.pl)
      //  will be treated as leaves.

      //OLD:
      //if (!ppt.ppt_name.isExitPoint())
      //  return;
      if (ppt.ppt_name.isEnterPoint() ||
          ppt.ppt_name.isThrowsPoint() ||
          ppt.ppt_name.isObjectInstanceSynthetic() ||
          ppt.ppt_name.isClassStaticSynthetic() ||
          ppt.ppt_name.isGlobalPoint()) {
        return;
      }

      //OLD:if (ppt.ppt_name.isCombinedExitPoint()) {
      if (ppt.ppt_name.isExitPoint() && ppt.ppt_name.isCombinedExitPoint()) {
        // not Daikon.TerminationMessage; caller has more info (e.g., filename)
        throw new RuntimeException("Bad program point name " + ppt.name
                                   + " is a combined exit point name");
      }
    }

    // Add derived variables
    add_derived_variables(ppt, vt.vals, vt.mods);

    // Causes interning
    vt = new ValueTuple(vt.vals, vt.mods);

    if (debugRead.isLoggable(Level.FINE)) {
      debugRead.fine ("Adding ValueTuple to " + ppt.name());
      debugRead.fine ("  length is " + vt.vals.length);
    }

    // If we are only reading the sample, don't process them
    if (dkconfig_read_samples_only) {
      return;
    }

    ppt.add_bottom_up (vt, 1);

    if (debugVars.isLoggable (Level.FINE))
      debugVars.fine (ppt.name() + " vars: " + Debug.int_vars (ppt, vt));

    if (Global.debugPrintDtrace) {
      Global.dtraceWriter.close();
    }

  }

  /** Returns non-null if this procedure has an unmatched entry. **/
  static boolean has_unmatched_procedure_entry(PptTopLevel ppt) {
    for (Invocation invok : call_hashmap.values()) {
      if (invok.ppt == ppt) {
        return true;
      }
    }
    for (Invocation invok : call_stack) {
      if (invok.ppt == ppt) {
        return true;
      }
    }
    return false;
  }


  /**
   * Print each call that does not have a matching exit
   */
  public static void process_unmatched_procedure_entries() {

    if (dkconfig_unmatched_procedure_entries_quiet)
      return;

    int unmatched_count = call_stack.size() + call_hashmap.size();

    if ((!call_stack.empty()) || (!call_hashmap.isEmpty())) {
      System.out.println();
      System.out.print(
        "No return from procedure observed "
          + UtilMDE.nplural(unmatched_count, "time") + ".");
      if (Daikon.use_dataflow_hierarchy) {
        System.out.print("  Unmatched entries are ignored!");
      }
      System.out.println();
      if (!call_hashmap.isEmpty()) {
        System.out.println("Unterminated calls:");
        if (dkconfig_verbose_unmatched_procedure_entries) {
          // Print the invocations in sorted order.
          // (Does this work?  The keys are integers. -MDE 7/1/2005.)
          TreeSet<Integer> keys = new TreeSet<Integer>(call_hashmap.keySet());
          ArrayList<Invocation> invocations = new ArrayList<Invocation>();
          for (Integer i : keys) {
            invocations.add(call_hashmap.get(i));
          }
          print_invocations_verbose(invocations);
        } else {
          print_invocations_grouped(call_hashmap.values());
        }
      }

      if (!call_stack.empty()) {
        if (dkconfig_verbose_unmatched_procedure_entries) {
          System.out.println("Remaining " +
                             UtilMDE.nplural(unmatched_count, "stack")
                             + " call summarized below.");
          print_invocations_verbose(call_stack);
        } else {
          print_invocations_grouped(call_stack);
        }
      }
      System.out.print("End of report for procedures not returned from.");
      if (Daikon.use_dataflow_hierarchy) {
        System.out.print("  Unmatched entries are ignored!");
      }
      System.out.println();
    }
  }

  /** Print all the invocations in the collection, in order. **/
  static void print_invocations_verbose(Collection<Invocation> invocations) {
    for (Invocation invok : invocations) {
      System.out.println(invok.format());
    }
  }

  /**
   * Print the invocations in the collection, in order, and
   * suppressing duplicates.
   **/
  static void print_invocations_grouped(Collection<Invocation> invocations) {
    Map<Invocation,Integer> counter = new HashMap<Invocation,Integer>();

    for (Invocation invok : invocations) {
      invok = invok.canonicalize();
      if (counter.containsKey(invok)) {
        Integer oldCount = counter.get(invok);
        Integer newCount = new Integer(oldCount.intValue() + 1);
        counter.put(invok, newCount);
      } else {
        counter.put(invok, new Integer(1));
      }
    }

    // Print the invocations in sorted order.
    TreeSet<Invocation> keys = new TreeSet<Invocation>(counter.keySet());
    for (Invocation invok : keys) {
      Integer count = counter.get(invok);
      System.out.println(invok.format(false) + " : "
                         + UtilMDE.nplural(count.intValue(), "invocation"));
    }
  }

  // This procedure reads a single record from a trace file and
  // fills up vals and mods by side effect.  The ppt name and
  // invocation nonce (if any) have already been read.
  private static void read_vals_and_mods_from_trace_file
                        (LineNumberReader reader, String filename,
                         PptTopLevel ppt, Object[] vals, int[] mods)
    throws IOException
  {
    VarInfo[] vis = ppt.var_infos;
    int num_tracevars = ppt.num_tracevars;

    String[] oldvalue_reps = ppt_to_value_reps.get(ppt);
    if (oldvalue_reps == null) {
      // We've not encountered this program point before.  The nulls in
      // this array will compare non-equal to whatever is in the trace
      // file, which is the desired behavior.
      oldvalue_reps = new String[num_tracevars];
    }

    if (Global.debugPrintDtrace) {
      Global.dtraceWriter.println(ppt.name());

      if (to_write_nonce) {
        Global.dtraceWriter.println(nonce_string);
        Global.dtraceWriter.println(nonce_value);
        to_write_nonce = false;
      }
    }

    for (int vi_index = 0, val_index = 0;
      val_index < num_tracevars;
      vi_index++) {
      Assert.assertTrue(vi_index < vis.length
      // , "Got to vi_index " + vi_index + " after " + val_index + " of " + num_tracevars + " values"
      );
      VarInfo vi = vis[vi_index];
      Assert.assertTrue((!vi.is_static_constant) || (vi.value_index == -1)
      // , "Bad value_index " + vi.value_index + " when static_constant_value = " + vi.static_constant_value + " for " + vi.repr() + " at " + ppt_name
      );
      if (vi.is_static_constant)
        continue;
      Assert.assertTrue(val_index == vi.value_index
      // , "Differing val_index = " + val_index
      // + " and vi.value_index = " + vi.value_index
      // + " for " + vi.name + lineSep + vi.repr()
      );

      // In errors, say "for program point", not "at program point" as the
      // latter confuses Emacs goto-error.

      String line = reader.readLine();
      if (line == null) {
        throw new Daikon.TerminationMessage(
          "Unexpected end of file at "
            + data_trace_state.filename
            + " line "
            + reader.getLineNumber() + lineSep
            + "  Expected variable "
            + vi.name()
            + ", got "
            + "null" // line
            + " for program point "
            + ppt.name());
      }

      // Read lines until an included variable is found
      while ((line != null)
             && !line.equals("")
             && !var_included(line)) {
        line = reader.readLine(); // value (discard it)
        line = reader.readLine(); // modbit
        if (line == null
            || !((line.equals("0") || line.equals("1") || line.equals("2")))) {
          throw new Daikon.TerminationMessage("Bad modbit '" + line + "'",
                                              reader, data_trace_state.filename);
        }
        line = reader.readLine(); // next variable name
      }

      if (!line.trim().equals (vi.str_name())) {
        throw new Daikon.TerminationMessage(
          "Mismatch between .dtrace file and .decls file.  Expected variable "
            + vi.name()
            + ", got "
            + line
            + " for program point "
            + ppt.name(),
          reader,
          data_trace_state.filename);
      }
      line = reader.readLine();
      if (line == null) {
        throw new Daikon.TerminationMessage(
          "Unexpected end of file at "
            + data_trace_state.filename
            + " line "
            + reader.getLineNumber() + lineSep
            + "  Expected value for variable "
            + vi.name()
            + ", got "
            + "null" // line
            + " for program point "
            + ppt.name());
      }
      String value_rep = line;
      line = reader.readLine();
      if (line == null) {
        throw new Daikon.TerminationMessage(
          "Unexpected end of file at "
            + data_trace_state.filename
            + " line "
            + reader.getLineNumber() + lineSep
            + "  Expected modbit for variable "
            + vi.name()
            + ", got "
            + "null" // line
            + " for program point "
            + ppt.name());
      }
      if (!((line.equals("0") || line.equals("1") || line.equals("2")))) {
        throw new Daikon.TerminationMessage("Bad modbit `" + line + "'",
                                  reader, data_trace_state.filename);
      }
      int mod = ValueTuple.parseModified(line);

      // System.out.println("Mod is " + mod + " at " + data_trace_state.filename + " line " + reader.getLineNumber());
      // System.out.pringln("  for variable " + vi.name()
      //                   + " for program point " + ppt.name());

      // MISSING_FLOW is only found during flow algorithm
      Assert.assertTrue (mod != ValueTuple.MISSING_FLOW,
                         "Data trace value can't be missing due to flow");

      if (mod != ValueTuple.MISSING_NONSENSICAL) {
        // Set the modbit now, depending on whether the value of the variable
        // has been changed or not.
        if (value_rep.equals(oldvalue_reps[val_index])) {
          if (!dkconfig_add_changed) {
            mod = ValueTuple.UNMODIFIED;
          }
        } else {
          mod = ValueTuple.MODIFIED;
        }
      }

      mods[val_index] = mod;
      oldvalue_reps[val_index] = value_rep;

      if (Global.debugPrintDtrace) {
        Global.dtraceWriter.println(vi.name());
        Global.dtraceWriter.println(value_rep);
        Global.dtraceWriter.println(mod);
      }
      Debug dbg = Debug.newDebug(FileIO.class, ppt, Debug.vis(vi));
      if (dbg != null)
        dbg.log(
          "Var " + vi.name() + " has value " + value_rep + " mod " + mod);

      // Both uninit and nonsensical mean missing modbit 2, because
      // it doesn't make sense to look at x.y when x is uninitialized.
      if (ValueTuple.modIsMissingNonsensical(mod)) {
        if (!(value_rep.equals("nonsensical")
          || value_rep.equals("uninit") // backward compatibility (9/27/2002)
          || value_rep.equals("missing"))) {
          throw new Daikon.TerminationMessage(
            "Modbit indicates missing value for variable "
              + vi.name() + " with value \"" + value_rep + "\";" + lineSep
            + "  text of value should be \"nonsensical\" or \"uninit\" at "
              + data_trace_state.filename + " line " + reader.getLineNumber());
        } else {
          // Keep track of variables that can be missing
          if (debug_missing && !vi.canBeMissing) {
              System.out.printf ("Var %s ppt %s at line %d missing%n",
                               vi, ppt.name(),
                               FileIO.data_trace_state.reader.getLineNumber());
              System.out.printf ("val_index = %d, mods[val_index] = %d%n",
                                 val_index, mods[val_index]);
          }
          vi.canBeMissing = true;
        }
        vals[val_index] = null;
      } else {
        // System.out.println("Mod is " + mod + " (missing=" +
        // ValueTuple.MISSING + "), rep=" + value_rep +
        // "(modIsMissing=" + ValueTuple.modIsMissing(mod) + ")");

        try {
          vals[val_index] = vi.rep_type.parse_value(value_rep);
          if (vals[val_index] == null) {
            mods[val_index] = ValueTuple.MISSING_NONSENSICAL;
            if (debug_missing && !vi.canBeMissing)
              System.out.printf ("Var %s ppt %s at line %d null-not missing%n",
                               vi, ppt.name(),
                               FileIO.data_trace_state.reader.getLineNumber());
            vi.canBeMissing = true;
          }
        } catch (Exception e) {
          throw new Daikon.TerminationMessage(
            "Error while parsing value "
              + value_rep
              + " for variable "
              + vi.name()
              + " of type "
              + vi.rep_type
              + ": "
              + e.toString(),
            reader,
            filename);
        }
      }
      val_index++;

    }

    ppt_to_value_reps.put(ppt, oldvalue_reps);

    if (Global.debugPrintDtrace) {
      Global.dtraceWriter.println();
    }

    // Expecting the end of a block of values.
    String line = reader.readLine();
    // First, we might get some variables that ought to be omitted.
    while ((line != null)
           && !line.equals("")
           && !var_included(line)) {
      line = reader.readLine(); // value
      line = reader.readLine(); // modbit
      line = reader.readLine(); // next variable name
    }
    Assert.assertTrue(
      (line == null) || (line.equals("")),
      "Expected blank line at line " + reader.getLineNumber() + ": " + line);
  }

  /**
   * If this is an function entry ppt, stores the values of all of the
   * variables away for use at the exit.  If this is an exit, finds the
   * values at enter and adds them as the value sof the orig variables.
   * Normally returns false.  Returns true if this is an exit without
   * a matching enter.  See dkconfig_ignore_missing_enter for more info.
   * If true is returned, this ppt should be ignored by the caller
   **/
  public static boolean add_orig_variables(PptTopLevel ppt,
                                     // HashMap cumulative_modbits,
                                     Object[] vals, int[] mods, Integer nonce) {
    VarInfo[] vis = ppt.var_infos;
    String fn_name = ppt.ppt_name.getNameWithoutPoint();
    String ppt_name = ppt.name();
    if (ppt_name.endsWith(enter_tag)) {
      Invocation invok = new Invocation(ppt, vals, mods);
      if (nonce == null) {
        call_stack.push(invok);
      } else {
        call_hashmap.put(nonce, invok);
      }
      return false;
    }

    if (ppt.ppt_name.isExitPoint() || ppt.ppt_name.isThrowsPoint()) {
      Invocation invoc;
      // Set invoc
      {
        if (nonce == null) {
          if (call_stack.empty()) {
            // Not Daikon.TerminationMessage:  caller knows context such as
            // file name and line number.
            throw new Error(
              "Function exit without corresponding entry: " + ppt.name());
          }
          invoc = call_stack.pop();
          while (invoc.ppt.ppt_name.getNameWithoutPoint() != fn_name) {
            // Should also mark as a function that made an exceptional exit
            // at runtime.
            System.err.println(
              "Exceptional exit from function "
                + fn_name
                + ", expected to first exit from "
                + invoc.ppt.ppt_name.getNameWithoutPoint()
                + ((data_trace_state.filename == null)
                  ? ""
                  : "; at "
                    + data_trace_state.filename
                    + " line "
                    + data_trace_state.reader.getLineNumber()));
            invoc = call_stack.pop();
          }
        } else {
          // nonce != null
          invoc = call_hashmap.get(nonce);
          if (dkconfig_ignore_missing_enter && (invoc == null)) {
            //System.out.printf ("Didn't find call with nonce %d to match %s" +
            //                   " ending at %s line %d\n", nonce, ppt.name(),
            //                   data_trace_state.filename,
            //                   data_trace_state.reader.getLineNumber());
            return true;
          } else if (invoc == null) {
            // Not Daikon.TerminationMessage:  caller knows context such as
            // file name and line number.
            throw new Error(
              "Didn't find call with nonce "
                + nonce
                + " to match "
                + ppt.name()
                + " ending at "
                + data_trace_state.filename
                + " line "
                + data_trace_state.reader.getLineNumber());
          }
          invoc = call_hashmap.get(nonce);
          call_hashmap.remove(nonce);
        }
      }
      Assert.assertTrue(invoc != null);

      // Loop through each orig variable and get its value/mod bits from
      // the ENTER point.  vi_index is the index into var_infos at the
      // ENTER point.  val_index is the index into vals[] and mods[] at
      // ENTER point.  Note that vis[] includes static constants but
      // vals[] and mods[] do not.  Also that we don't create orig versions
      // of static constants
      int vi_index = 0;
      for (int val_index = 0; val_index < ppt.num_orig_vars; val_index++) {
        VarInfo vi = vis[ppt.num_tracevars + ppt.num_static_constant_vars
                         + val_index];
        assert (!vi.is_static_constant) : "orig constant " + vi;

        // Skip over constants in the entry point
        while (invoc.ppt.var_infos[vi_index].is_static_constant)
          vi_index++;

        // Copy the vals and mod bits from entry to exit
        vals[ppt.num_tracevars + val_index] = invoc.vals[val_index];
        int mod = invoc.mods[val_index];
        mods[ppt.num_tracevars + val_index] = mod;

        // If the value was missing, mark this variable as can be missing
        // Carefully check that we have orig version of the variable from
        // the ENTER point.
        if (ValueTuple.modIsMissingNonsensical (mod)) {
          if (debug_missing && !vi.canBeMissing) {
            System.out.printf ("add_orig: var %s missing[%d/%d]%n", vi,
                               val_index, vi_index);
          }
          vi.canBeMissing = true;
          assert invoc.vals[val_index] == null;
          assert vi.name() == invoc.ppt.var_infos[vi_index].prestate_name()
            : vi.name() + " != "+ invoc.ppt.var_infos[vi_index];
          assert invoc.ppt.var_infos[vi_index].canBeMissing
            : invoc.ppt.var_infos[vi_index];
        }
        vi_index++;
      }
    }
    return false;
  }

  /** Add derived variables **/
  public static void add_derived_variables(PptTopLevel ppt,
                                            Object[] vals,
                                            int[] mods) {
    // This ValueTuple is temporary:  we're temporarily suppressing interning,
    // which we will do after we have all the values available.
    ValueTuple partial_vt = ValueTuple.makeUninterned(vals, mods);
    int filled_slots =
      ppt.num_orig_vars + ppt.num_tracevars + ppt.num_static_constant_vars;
    for (int i = 0; i < filled_slots; i++) {
      Assert.assertTrue(!ppt.var_infos[i].isDerived());
    }
    for (int i = filled_slots; i < ppt.var_infos.length; i++) {
      if (!ppt.var_infos[i].isDerived()) {
        // Check first because repr() can be slow
        Assert.assertTrue(
          ppt.var_infos[i].isDerived(),
          "variable not derived: " + ppt.var_infos[i].repr());
      }
    }
    int num_const = ppt.num_static_constant_vars;
    for (int i = filled_slots; i < ppt.var_infos.length; i++) {
      // Add this derived variable's value
      ValueAndModified vm =
        ppt.var_infos[i].derived.computeValueAndModified(partial_vt);
      vals[i - num_const] = vm.value;
      mods[i - num_const] = vm.modified;
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Serialized PptMap files
  ///

  /**
   * Use a special record type.  Saving as one object allows for
   * reference-sharing, easier saves and loads, and potential for
   * later overriding of SerialFormat.readObject if the save format
   * changes (ick).
   **/
  static final class SerialFormat implements Serializable {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20060905L;

    public SerialFormat(PptMap map, Configuration config) {
      this.map = map;
      this.config = config;
      this.new_decl_format = FileIO.new_decl_format;

    }
    public PptMap map;
    public Configuration config;
    public boolean new_decl_format = false;
  }

  public static void write_serialized_pptmap(PptMap map, File file)
    throws IOException {
    SerialFormat record = new SerialFormat(map, Configuration.getInstance());
    UtilMDE.writeObject(record, file);
  }

  /**
   * Read either a serialized PptMap or a InvMap and return a
   * PptMap.  If an InvMap is specified, it is converted to a PptMap
   */
  public static PptMap read_serialized_pptmap(
    File file,
    boolean use_saved_config)
    throws IOException {

    try {
      Object obj = UtilMDE.readObject(file);
      if (obj instanceof FileIO.SerialFormat) {
        SerialFormat record = (SerialFormat) obj;
        if (use_saved_config) {
          Configuration.getInstance().overlap(record.config);
        }
        FileIO.new_decl_format = record.new_decl_format;
        return (record.map);
      } else if (obj instanceof InvMap) {
        InvMap invs = (InvMap) obj;
        PptMap ppts = new PptMap();
        for (Iterator<PptTopLevel> i = invs.pptIterator(); i.hasNext();) {
          PptTopLevel ppt = i.next();
          PptTopLevel nppt = new PptTopLevel(ppt.name, ppt.var_infos);
          nppt.set_sample_number(ppt.num_samples());
          ppts.add(nppt);
          List<Invariant> inv_list = invs.get(ppt);
          for (Invariant inv : inv_list) {
            PptSlice slice = nppt.get_or_instantiate_slice(inv.ppt.var_infos);
            inv.ppt = slice;
            slice.addInvariant(inv);
          }
        }
        return (ppts);
      } else {
        throw new IOException(
          "Unexpected serialized file type: " + obj.getClass());
      }
    } catch (ClassNotFoundException e) {
      throw (IOException)(new IOException("Error while loading inv file").initCause(e));
    } catch (InvalidClassException e) {
      throw new IOException(
        "It is likely that the .inv file format has changed, because a Daikon data structure has been modified, so your old .inv file is no longer readable by Daikon.  Please regenerate your .inv file."
        // + lineSep + e.toString()
        );
    }
    // } catch (StreamCorruptedException e) { // already extends IOException
    // } catch (OptionalDataException e) {    // already extends IOException
  }

  /**
   * Returns whether or not the specified ppt name should be included
   * in processing.  Ppts can be excluded because they match the omit_regexp,
   * don't match ppt_regexp, or are greater than ppt_max_name.
   */
  public static boolean ppt_included(String ppt_name) {

    // System.out.println ("ppt_name = '" + ppt_name + "' max name = '"
    //                     + Daikon.ppt_max_name + "'");
    if (((Daikon.ppt_omit_regexp != null)
         && Daikon.ppt_omit_regexp.matcher(ppt_name).find())
        || ((Daikon.ppt_regexp != null)
            && !Daikon.ppt_regexp.matcher(ppt_name).find())
        || ((Daikon.ppt_max_name != null)
            && ((Daikon.ppt_max_name.compareTo(ppt_name) < 0)
                && (ppt_name.indexOf(global_suffix) == -1)))) {
      return (false);
    } else {
      return (true);
    }
  }

  public static boolean var_included(String var_name) {
    assert ! var_name.equals("");
    if (((Daikon.var_omit_regexp != null)
         && Daikon.var_omit_regexp.matcher(var_name).find())
        || ((Daikon.var_regexp != null)
            && !Daikon.var_regexp.matcher(var_name).find())) {
      return (false);
    } else {
      return true;
    }
  }

  /**
   * Skips over a decl.  Essentially reads in everything up to and including
   * the next blank line.
   */
  private static void skip_decl (LineNumberReader reader) throws IOException {
    String line = reader.readLine();
    // This fails if some lines of a declaration (e.g., the comparability
    // field) are empty.
    while ((line != null) && !line.equals("")) {
      line = reader.readLine();
    }
  }

  /**
   * Converts the declaration record versoin of a name into its correct
   * version.  In the declaration record, blanks are encoded as \_ and
   * backslashes as \\.
   */
  private static String unescape_decl (String orig) {
    StringBuilder sb = new StringBuilder(orig.length());
    // The previous escape character was seen just before this position.
    int post_esc = 0;
    int this_esc = orig.indexOf('\\');
    while (this_esc != -1) {
      if (this_esc == orig.length()-1) {
        sb.append(orig.substring(post_esc, this_esc+1));
        post_esc = this_esc+1;
        break;
      }
      switch (orig.charAt(this_esc+1)) {
      case 'n':
        sb.append(orig.substring(post_esc, this_esc));
        sb.append('\n');        // not lineSep
        post_esc = this_esc+2;
        break;
      case 'r':
        sb.append(orig.substring(post_esc, this_esc));
        sb.append('\r');
        post_esc = this_esc+2;
        break;
      case '_':
        sb.append (orig.substring(post_esc, this_esc));
        sb.append (' ');
        post_esc = this_esc+2;
        break;
      case '\\':
        // This is not in the default case because the search would find
        // the quoted backslash.  Here we incluce the first backslash in
        // the output, but not the first.
        sb.append(orig.substring(post_esc, this_esc+1));
        post_esc = this_esc+2;
        break;

      default:
        // In the default case, retain the character following the
        // backslash, but discard the backslash itself.  "\*" is just
        // a one-character string.
        sb.append(orig.substring(post_esc, this_esc));
        post_esc = this_esc+1;
        break;
      }
      this_esc = orig.indexOf('\\', post_esc);
    }
    if (post_esc == 0)
      return orig;
    sb.append(orig.substring(post_esc));
    return sb.toString();
  }

  /**
   * Class that holds all of the information from the declaration record
   * concerning a particular variable
   */
  public static class VarDefinition implements java.io.Serializable, Cloneable{
    static final long serialVersionUID = 20060524L;
    transient ParseState state;
    public String name;
    public VarKind kind = null;
    public String enclosing_var;
    public String relative_name = null;
    public RefType ref_type = RefType.POINTER;
    public int arr_dims = 0;
    public List<String> function_args = null;
    public ProglangType rep_type = null;
    public ProglangType declared_type = null;
    public EnumSet<VarFlags> flags = EnumSet.noneOf (VarFlags.class);
    public EnumSet<LangFlags> lang_flags = EnumSet.noneOf (LangFlags.class);
    public VarComparability comparability = null;
    public String parent_ppt = null;
    public int parent_relation_id = 0;
    public String parent_variable = null;
    public Object static_constant_value = null;

    public VarDefinition clone() {
      try {
        return (VarDefinition) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new Error("This can't happen: ", e);
      }
    }

    public VarDefinition copy () {
      try {
        VarDefinition copy = this.clone();
        copy.flags = flags.clone();
        copy.lang_flags = lang_flags.clone();
        return copy;
      } catch (Throwable t) {
        throw new RuntimeException (t);
      }
    }

    /** Clears the parent relation if one existed **/
    public void clear_parent_relation() {
      parent_ppt = null;
      parent_relation_id = 0;
      parent_variable = null;
    }

    /**
     * Initialize from the 'variable <name>' record.  Scanner should be
     * pointing at name.
     */
    public VarDefinition (ParseState state, Scanner scanner) throws DeclError {
      this.state = state;
      name = need (scanner, "name");
      need_eol (scanner);
      if (state.varcomp_format == VarComparability.IMPLICIT)
        comparability = VarComparabilityImplicit.unknown;
      else
        comparability = VarComparabilityNone.it;
    }

    public VarDefinition (String name, VarKind kind, ProglangType type) {
      this.state = null;
      this.name = name;
      this.kind = kind;
      this.rep_type = type;
      this.declared_type = type;
      comparability = VarComparabilityNone.it;
    }

    /**
     * Parse a var-kind record.  Scanner should be pointing at the variable
     * kind.
     */
    public void parse_var_kind (Scanner scanner) throws DeclError {
      kind = parse_enum_val (scanner, VarKind.class, "variable kind");

      if ((kind == VarKind.FIELD) || (kind == VarKind.FUNCTION)) {
        relative_name = need (scanner, "relative name");
      }
      need_eol (scanner);
    }

    /** Parses the enclosing-var record **/
    public void parse_enclosing_var (Scanner scanner) throws DeclError {
      enclosing_var = need (scanner, "enclosing variable name");
      need_eol(scanner);
    }

    /** Parses the reference-type record **/
    public void parse_reference_type (Scanner scanner) throws DeclError {
      ref_type = parse_enum_val (scanner, RefType.class, "reference type");
      need_eol (scanner);
    }

    /** Parses the array record **/
    public void parse_array (Scanner scanner) throws DeclError {
      /*@Interned*/ String arr_str = need (scanner, "array dimensions");
      if (arr_str == "0")       // interned
        arr_dims = 0;
      else if (arr_str == "1")  // interned
        arr_dims = 1;
      else
        decl_error (state, "%s found where 0 or 1 expected", arr_str);
    }

    /** Parses the function-args record **/
    public void parse_function_args (Scanner scanner) throws DeclError {

      function_args = new ArrayList<String>();
      while (scanner.hasNext()) {
        function_args.add (unescape_decl (scanner.next()).intern());
      }
    }

    public void parse_rep_type (Scanner scanner) throws DeclError {
      /*@Interned*/ String rep_type_str = need (scanner, "rep type");
      need_eol (scanner);
      rep_type = ProglangType.rep_parse (rep_type_str);
    }

    public void parse_dec_type (Scanner scanner) throws DeclError {
      /*@Interned*/ String declared_type_str = need (scanner, "declaration type");
      need_eol (scanner);
      declared_type = ProglangType.parse (declared_type_str);
    }

    /** Parse the flags record.  Multiple flags can be specified **/
    public void parse_flags (Scanner scanner) throws DeclError {

      flags.add (parse_enum_val (scanner, VarFlags.class, "Flag"));
      while (scanner.hasNext())
        flags.add (parse_enum_val (scanner, VarFlags.class, "Flag"));
      // System.out.printf ("flags for %s are %s%n", name, flags);
    }

    /**
     * Parse the langauge specific flags record.  Multiple flags can
     * be specified **/
    public void parse_lang_flags (Scanner scanner) throws DeclError {

      lang_flags.add (parse_enum_val (scanner, LangFlags.class,
                                      "Language Specific Flag"));
      while (scanner.hasNext())
        lang_flags.add (parse_enum_val (scanner, LangFlags.class,
                                        "Language Specific Flag"));
    }

    /** Parses a comparability record **/
    public void parse_comparability (Scanner scanner) throws DeclError {
      /*@Interned*/ String comparability_str = need (scanner, "comparability");
      need_eol (scanner);
      comparability = VarComparability.parse (state.varcomp_format,
                                            comparability_str, declared_type);
    }

    /** Parse a parent ppt record **/
    public void parse_parent (Scanner scanner,
                       List<ParentRelation> ppt_parents) throws DeclError {

     parent_ppt = need (scanner, "parent ppt");
     parent_relation_id = Integer.parseInt (need (scanner, "parent id"));
     boolean found = false;
     for (ParentRelation pr : ppt_parents) {
       if ((pr.parent_ppt_name == parent_ppt) && (pr.id ==parent_relation_id)){
         found = true;
         break;
       }
     }
     if (!found) {
       decl_error (state, "specified parent ppt '%s[%d]' for variable '%s' "
                   + "is not a parent to this ppt", parent_ppt,
                   parent_relation_id, name);
     }
     if (scanner.hasNext())
       parent_variable = need (scanner, "parent variable");
     need_eol (scanner);
    }

    /** Parse a constant record **/
    public void parse_constant (Scanner scanner) throws DeclError {
      /*@Interned*/ String constant_str = need (scanner, "constant value");
      need_eol (scanner);
      static_constant_value = rep_type.parse_value (constant_str);
    }

    /**
     * Helper function, returns the next string token unescaped and
     * interned.  Throw a DeclError if there is no next token
     */
    public /*@Interned*/ String need (Scanner scanner, String description) throws DeclError {
      return (FileIO.need (state, scanner, description));
    }

    /** Throws a DeclError if the scanner is not at end of line */
    public void need_eol (Scanner scanner) throws DeclError {
      FileIO.need_eol (state, scanner);
    }

    /**
     * Looks up the next token as a member of enum_class.  A DeclError
     * is thrown if there is no token or if it is not valid member of
     * the class.  Enums are presumed to be in in upper case
     */
    public <E extends Enum<E>> E parse_enum_val (Scanner scanner,
           Class<E> enum_class, String descr) throws DeclError {
      return FileIO.parse_enum_val (state, scanner, enum_class, descr);
    }
  }

  /**
   * Helper function, returns the next string token unescaped and
   * interned.  Throw a DeclError if there is no next token
   */
  public static /*@Interned*/ String need (ParseState state, Scanner scanner,
                             String description) throws DeclError {
    if (!scanner.hasNext())
      decl_error (state, "end-of-line found where %s expected", description);
    return unescape_decl (scanner.next()).intern();
  }

  /** Throws a DeclError if the scanner is not at end of line */
  public static void need_eol (ParseState state, Scanner scanner)
    throws DeclError {
    if (scanner.hasNext())
      decl_error (state, "'%s' found where end-of-line expected",
                  scanner.next());
  }

  /**
   * Looks up the next token as a member of enum_class.  A DeclError
   * is thrown if there is no token or if it is not valid member of
   * the class.  Enums are presumed to be in in upper case
   */
  public static <E extends Enum<E>> E parse_enum_val (ParseState state,
         Scanner scanner, Class<E> enum_class, String descr) throws DeclError {

    /*@Interned*/ String str = need (state, scanner, descr);
    try {
      E e = Enum.valueOf (enum_class, str.toUpperCase());
      return (e);
    } catch (Exception exception) {
      E[] all = enum_class.getEnumConstants();
      String msg = "";
      for (E e : all) {
        if (msg != "")          // "interned": initialization-checking pattern
          msg += ", ";
        msg += String.format ("'%s'", e.name().toLowerCase());
      }
      decl_error (state, "'%s' found where %s expected", str, msg);
      return (null);
    }
  }

  private static void decl_error (ParseState state, String format,
                                  Object... args) throws DeclError {
    throw DeclError.detail (state, format, args);
  }

  /** Returns whether the line is the start of a ppt declaration **/
  private static boolean is_declaration_header (/*@Interned*/ String line) {
    if (new_decl_format)
      return (line.startsWith ("ppt "));
    else
      return (line == declaration_header);
  }


}
