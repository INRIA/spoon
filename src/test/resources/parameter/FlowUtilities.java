/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.text;


import com.ibm.icu.text.BreakIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.TextUtilities;

/**
 * Utility class for FlowFigures.
 * @author hudsonr
 * @since 3.4
 */
public class FlowUtilities 
{

interface LookAhead {
	int getWidth();
}

/**
 * a singleton default instance
 */
public static FlowUtilities INSTANCE = new FlowUtilities();

private static final BreakIterator INTERNAL_LINE_BREAK = BreakIterator.getLineInstance();
private static TextLayout layout;

static final BreakIterator LINE_BREAK = BreakIterator.getLineInstance();

static boolean canBreakAfter(char c) {
	boolean result = Character.isWhitespace(c) || c == '-';
	if (!result && (c < 'a' || c > 'z')) {
		// chinese characters and such would be caught in here
		// LINE_BREAK is used here because INTERNAL_LINE_BREAK might be in use
		LINE_BREAK.setText(c + "a"); //$NON-NLS-1$
		result = LINE_BREAK.isBoundary(1);
	}
	return result;
}

private static int findFirstDelimeter(String string) {
	int macNL = string.indexOf('\r');
	int unixNL = string.indexOf('\n');
	
	if (macNL == -1)
		macNL = Integer.MAX_VALUE;
	if (unixNL == -1)
		unixNL = Integer.MAX_VALUE;

	return Math.min(macNL, unixNL);
}

/**
 * Gets the average character width.
 * 
 * @param fragment the supplied TextFragmentBox to use for calculation.
 *                 if the length is 0 or if the width is or below 0,
 *                 the average character width is taken from standard 
 *                 font metrics.
 * @param font     the font to use in case the TextFragmentBox conditions 
 *                 above are true.
 * @return         the average character width
 */
protected float getAverageCharWidth(TextFragmentBox fragment, Font font) {
    if (fragment.getWidth() > 0 && fragment.length != 0)
        return fragment.getWidth() / (float)fragment.length;
    return FigureUtilities.getFontMetrics(font).getAverageCharWidth();
}

static int getBorderAscent(InlineFlow owner) {
	if (owner.getBorder() instanceof FlowBorder) {
		FlowBorder border = (FlowBorder)owner.getBorder();
		return border.getInsets(owner).top;
	}
	return 0;
}

static int getBorderAscentWithMargin(InlineFlow owner) {
	if (owner.getBorder() instanceof FlowBorder) {
		FlowBorder border = (FlowBorder)owner.getBorder();
		return border.getTopMargin() + border.getInsets(owner).top;
	}
	return 0;
}

static int getBorderDescent(InlineFlow owner) {
	if (owner.getBorder() instanceof FlowBorder) {
		FlowBorder border = (FlowBorder)owner.getBorder();
		return border.getInsets(owner).bottom;
	}
	return 0;
}

static int getBorderDescentWithMargin(InlineFlow owner) {
	if (owner.getBorder() instanceof FlowBorder) {
		FlowBorder border = (FlowBorder)owner.getBorder();
		return border.getBottomMargin() + border.getInsets(owner).bottom;
	}
	return 0;
}

/**
 * Provides a TextLayout that can be used by the Draw2d text package for Bidi.  This 
 * TextLayout should not be disposed by clients.  The provided TextLayout's orientation
 * will be LTR.
 * 
 * @return an SWT TextLayout that can be used for Bidi
 * @since 3.1
 */
static TextLayout getTextLayout() {
	if (layout == null)
		layout = new TextLayout(Display.getDefault());
	layout.setOrientation(SWT.LEFT_TO_RIGHT);
	return layout;
}

/**
 * @param frag
 * @param string
 * @param font
 * @since 3.1
 */
private static void initBidi(TextFragmentBox frag, String string, Font font) {
	if (frag.requiresBidi()) {
		TextLayout textLayout = getTextLayout();
		textLayout.setFont(font);
		//$TODO need to insert overrides in front of string.
		textLayout.setText(string);
	}
}

private int measureString(TextFragmentBox frag, String string, int guess, Font font) {
    if (frag.requiresBidi()) {
        // The text and/or could have changed if the lookAhead was invoked.  This will
        // happen at most once.
        return getTextLayoutBounds(string, font, 0, guess - 1).width;
    } else
        return getTextUtilities().getStringExtents(string.substring(0, guess), font).width;
}

/**
 * Sets up the fragment width based using the font and string passed in.
 * 
 * @param fragment
 *            the text fragment whose width will be set
 * @param font
 *            the font to be used in the calculation
 * @param string
 *            the string to be used in the calculation
 */
final protected void setupFragment(TextFragmentBox fragment, Font font, String string) {
    if (fragment.getWidth() == -1 || fragment.isTruncated()) {
        int width;
        if (string.length() == 0 || fragment.length == 0)
            width = 0;
        else if (fragment.requiresBidi()) {
            width = getTextLayoutBounds(string, font, 0, fragment.length - 1).width;
        } else
            width = getTextUtilities().getStringExtents(string.substring(0, fragment.length), font).width;
        if (fragment.isTruncated())
            width += getEllipsisWidth(font);
        fragment.setWidth(width);
    }
}

/**
 * Sets up a fragment and returns the number of characters consumed from the given
 * String. An average character width can be provided as a hint for faster calculation. 
 * If a fragment's bidi level is set, a TextLayout will be used to calculate the width.
 * 
 * @param frag the TextFragmentBox
 * @param string the String
 * @param font the Font used for measuring
 * @param context the flow context
 * @param wrapping the word wrap style
 * @return the number of characters that will fit in the given space; can be 0 (eg., when
 * the first character of the given string is a newline)
 */
final protected int wrapFragmentInContext(TextFragmentBox frag, String string,
		FlowContext context, LookAhead lookahead, Font font, int wrapping) {
	frag.setTruncated(false);
	int strLen = string.length();
	if (strLen == 0) {
		frag.setWidth(-1);
		frag.length = 0;
		setupFragment(frag, font, string);
		context.addToCurrentLine(frag);
		return 0;
	}
	
	INTERNAL_LINE_BREAK.setText(string);

	initBidi(frag, string, font);
	float avgCharWidth = getAverageCharWidth(frag, font);
	frag.setWidth(-1);
	
	/*
	 * Setup initial boundaries within the string.
	 */
	int absoluteMin = 0;
	int max, min = 1;
	if (wrapping == ParagraphTextLayout.WORD_WRAP_HARD) {
		absoluteMin = INTERNAL_LINE_BREAK.next();
		while (absoluteMin > 0 && Character.isWhitespace(string.charAt(absoluteMin - 1)))
			absoluteMin--;
		min = Math.max(absoluteMin, 1);
	}
	int firstDelimiter = findFirstDelimeter(string);
	if (firstDelimiter == 0)
		min = max = 0;
	else
		max = Math.min(strLen, firstDelimiter) + 1;

	
	int availableWidth = context.getRemainingLineWidth();
	int guess = 0, guessSize = 0;
	
	while (true) {
		if ((max - min) <= 1) {
			if (min == absoluteMin
					&& context.isCurrentLineOccupied() 
					&& !context.getContinueOnSameLine()
					&& availableWidth < measureString(frag, string, min, font)
						+ ((min == strLen && lookahead != null) ? lookahead.getWidth() : 0)
			) {
				context.endLine();
				availableWidth = context.getRemainingLineWidth();
				max = Math.min(strLen, firstDelimiter) + 1;
				if ((max - min) <= 1)
					break;
			} else
				break;
		}
		// Pick a new guess size
		// New guess is the last guess plus the missing width in pixels
		// divided by the average character size in pixels
		guess += 0.5f + (availableWidth - guessSize) / avgCharWidth;

		if (guess >= max) guess = max - 1;
		if (guess <= min) guess = min + 1;

		guessSize = measureString(frag, string, guess, font);
		
		if (guess == strLen
				&& lookahead != null
				&& !canBreakAfter(string.charAt(strLen - 1))
				&& guessSize + lookahead.getWidth() > availableWidth) {
			max = guess;
			continue;
		}

        if (guessSize <= availableWidth) {
            min = guess;
            frag.setWidth(guessSize);
            if (guessSize == availableWidth)
                max = guess + 1;
        } else
            max = guess;
    }
    
    int result = min;
    boolean continueOnLine = false;
    if (min == strLen) {
        //Everything fits
        if (string.charAt(strLen - 1) == ' ') {
            if (frag.getWidth() == -1) {
                frag.length = result;
                frag.setWidth(measureString(frag, string, result, font));
            }
            if (lookahead.getWidth() > availableWidth - frag.getWidth()) {
                frag.length = result - 1;
                frag.setWidth(-1);
            } else
                frag.length = result;
        } else {
            continueOnLine = !canBreakAfter(string.charAt(strLen - 1));
            frag.length = result;
        }
    } else if (min == firstDelimiter) {
        //move result past the delimiter
        frag.length = result;
        if (string.charAt(min) == '\r') {
            result++;
            if (++min < strLen && string.charAt(min) == '\n')
                result++;
        } else if (string.charAt(min) == '\n')
            result++;
    } else if (string.charAt(min) == ' '
            || canBreakAfter(string.charAt(min - 1))
            || INTERNAL_LINE_BREAK.isBoundary(min)) {
        frag.length = min;
        if (string.charAt(min) == ' ')
            result++;
        else if (string.charAt(min - 1) == ' ') {
            frag.length--;
            frag.setWidth(-1);
        }
    } else out: {
        // In the middle of an unbreakable offset
        result = INTERNAL_LINE_BREAK.preceding(min);
        if (result == 0) {
            switch (wrapping) {
                case ParagraphTextLayout.WORD_WRAP_TRUNCATE :
					int truncatedWidth = availableWidth - getEllipsisWidth(font);
					if (truncatedWidth > 0) {
						//$TODO this is very slow.  It should be using avgCharWidth to go faster
						while (min > 0) {
							guessSize = measureString(frag, string, min, font);
							if (guessSize <= truncatedWidth)
								break;
							min--;
						}
						frag.length = min;
					} else
						frag.length = 0;
					frag.setTruncated(true);
                    result = INTERNAL_LINE_BREAK.following(max - 1);
                    break out;

                default:
                    result = min;
                    break;
            }
        }
        frag.length = result;
        if (string.charAt(result - 1) == ' ')
            frag.length--;
        frag.setWidth(-1);
    }
    
    setupFragment(frag, font, string);
    context.addToCurrentLine(frag);
    context.setContinueOnSameLine(continueOnLine);
    return result;
}

/**
 * @see TextLayout#getBounds()
 */
protected Rectangle getTextLayoutBounds(String s, Font f, int start, int end) {
    TextLayout textLayout = getTextLayout();
    textLayout.setFont(f);
    textLayout.setText(s);
    return textLayout.getBounds(start, end);
}

/**
 * Returns an instance of a <code>TextUtililities</code> class on which
 * text calculations can be performed. Clients may override to customize.
 * 
 * @return the <code>TextUtililities</code> instance
 * @since 3.4
 */
protected TextUtilities getTextUtilities() {
    return TextUtilities.INSTANCE;
}

/**
 * Gets the ellipsis width.
 * 
 * @param font
 *            the font to be used in the calculation
 * @return the width of the ellipsis
 * @since 3.4
 */
private int getEllipsisWidth(Font font) {
    return getTextUtilities().getStringExtents(TextFlow.ELLIPSIS, font).width;
}
}
