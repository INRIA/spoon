package spoon.reflect.cu;

/**
 * This class defines textual source code fragments that can be attached to a
 * compilation unit in order to modify its source code by preserving its
 * original formatting. Source code fragements are ignored by default. A special
 * mode must be activated in order to take them into account (-f or --fragments
 * with command line). When this mode is activated, the programs model is not
 * used any more to generate the transformed code (unless a fragment uses it to
 * get its code).
 */
public class SourceCodeFragment {

	/**
	 * The position in the orginal source code where this fragment must be
	 * inserted. Default value is 0;
	 */
	public int position = 0;

	/**
	 * The length of the original source code that must be replaced by this
	 * fragment (starting at the position). Default value is 0 (pure insertion).
	 * Note that if replacementLength==code.length, then the fragment just
	 * replaces the existing code.
	 */
	public int replacementLength = 0;

	/**
	 * The value of the code, which will be printed as is within the orginal
	 * source code (at the specified position).
	 */
	public String code = "";

	/**
	 * Creates a new source code fragment with default values.
	 */
	public SourceCodeFragment() {
		super();
	}

	/**
	 * Creates a new source code fragment.
	 * 
	 * @param position
	 *            the position at which it must start in the original source
	 *            code
	 * @param code
	 *            the code as a string (will be pasted as is)
	 * @param replacementLength
	 *            the number of character of the original code that should be
	 *            replaced
	 */
	public SourceCodeFragment(int position, String code, int replacementLength) {
		super();
		this.position = position;
		this.code = code;
		this.replacementLength = replacementLength;
	}

}
