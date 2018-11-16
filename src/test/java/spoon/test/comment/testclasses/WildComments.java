package spoon.test.comment.testclasses;

public class WildComments {
	/*
	 * each string literal below is a pair of a comment and a string value.
	 * The string literal value defines expected value of `CtComment#getContent()` of it's comment
	 */
	String[] comments = new String[]{
			///* test single line comments with nested * and */ and **/
			"/* test single line comments with nested * and */ and **/",
			//* starts with *
			"* starts with *",
			///* starts with /*
			"/* starts with /*",
			//*/ starts with */
			"*/ starts with */",
			// */ starts with space and */
			"*/ starts with space and */",
			/// starts with /
			"/ starts with /",
			//// starts with //
			"// starts with //",

			/* test wild multiline comments */
			"test wild multiline comments",
			/*/ starts with /*/
			"/ starts with /",
			/* / starts with space and /*/
			"/ starts with space and /",
			/*// starts with //*/
			"// starts with //",
			/* // starts with space and // */
			"// starts with space and //",
			/*/* starts with /* */
			"/* starts with /*",
			/*
			 * /* second line starts with /* */
			"/* second line starts with /*",
			/*
			 * /* second line starts with /* and ends with /*
			 * /* */
			"/* second line starts with /* and ends with /*\n/*",
			/*
			 * 
			 * 
			 * trim all empty lines?
			 * 
			 * 
			 */
			"trim all empty lines?",
			/*
			 * 
			 * 
			 * trim all empty lines,
			 * but not in the middle
			 * 
			 * 
			 * of the comment!
			 *  this line keeps prefix and trailing space 
			 * 	but it was not last line, whose trailing space is ignored 
			 * 
			 */
			"trim all empty lines,\nbut not in the middle\n\n\nof the comment!\n this line keeps prefix and trailing space \n	but it was not last line, whose trailing space is ignored",
			/*
			 // 1 second line starts with // */
			"// 1 second line starts with //",
			/*
			 * // 2 second line starts with // */
			"// 2 second line starts with //",
			/*
			 * // 3 second line starts with // 
			 */
			"// 3 second line starts with //",
			/*
			 * // 4 second line starts with // 
			 * */
			"// 4 second line starts with //",
			/**/
			"",
			/***/
			"",
			/****/
			"*",
			/*****/
			"**",
			/* */
			"",
			/** */
			"",
			/** **/
			"*",
			/*
			 */
			"",
			/*
			 * 
			 */
			"",
			/*
			  
			 */
			"",

			/** test wild javadoc comments */
			"test wild javadoc comments",
			/** / starts with space and /*/
			"/ starts with space and /",
			/** // starts with space and //*/
			"// starts with space and //",
			/** /* starts with space and /* */
			"/* starts with space and /*",

			/*** starts and ends with 3 * ***/
			"* starts and ends with 3 * **",
			/**** starts and ends with 4 * ****/
			"** starts and ends with 4 * ***",

			/* these comments should not cause 'Unexpected next line after last line' exception */
			"these comments should not cause 'Unexpected next line after last line' exception",
			/*/
			slash and comment
			*/
			"/\nslash and comment",
			/*
			comment and slash
			/*/
			"comment and slash\n/",
			/*/
			slash and comment and slash
			/*/
			"/\nslash and comment and slash\n/",
			//*/
			"*/",
			/*/*/
			"/"
	};
}
 