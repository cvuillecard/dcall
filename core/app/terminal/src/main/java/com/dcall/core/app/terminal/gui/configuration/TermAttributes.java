package com.dcall.core.app.terminal.gui.configuration;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TermAttributes {
    private static final Logger LOG = LoggerFactory.getLogger(TermAttributes.class);

    // WINDOW
    public static final String FRAME_TITLE = "DCall terminal";
    public static final int DEF_FRAME_NB_COLS = 80;
    public static final int DEF_FRAME_NB_ROWS = 24;
    public static int FRAME_NB_COLS = DEF_FRAME_NB_COLS;
    public static int FRAME_NB_ROWS = DEF_FRAME_NB_ROWS;
    public static int MARGIN = 1;
    public static int MARGIN_LEFT = 1;
    public static int MARGIN_RIGHT = 1;
    public static int MARGIN_TOP = 2;

    // HEADER
    public static final TextColor HEADER_BACKGROUND = new TextColor.RGB(230, 230, 0); // YELLOW
    public static final TextColor HEADER_FOREGROUND = new TextColor.RGB(0, 0, 0); // BLACK
    public static final SGR[] HEADER_STYLE = new SGR[] { SGR.BOLD };
    public static final String HEADER_TITLE = "DCall (More you do, more it works..)";

    // PROMPT
    public static final TextColor PROMPT_BACKGROUND = TextColor.ANSI.BLACK;
    public static final TextColor PROMPT_FOREGROUND = TextColor.ANSI.CYAN;
    public static final SGR[] PROMPT_STYLE = new SGR[] { SGR.BOLD };
    public static final String PROMPT = "DCall > ";

    // INPUT
    public static final int DEF_INPUT_NB_LINE = 255;
    public static final TextColor INPUT_BACKGROUND = TextColor.ANSI.BLACK;
    public static final TextColor INPUT_FOREGROUND = TextColor.ANSI.WHITE;
    public static final SGR[] INPUT_STYLE = new SGR[] { SGR.BOLD };

    // SCROLL
    public static final int SCROLL_PADDING_UP = 1;
    public static final int SCROLL_PADDING_DOWN = -1;

    // UTILS
    public static int getMaxLineWidth() {
        return getTotalLineWidth() - 1;
    }

    public static int getTotalLineWidth() {
        return FRAME_NB_COLS - getMarginWidth();
    }

    public static int getMarginWidth() {
        return MARGIN_LEFT + MARGIN_RIGHT;
    }
//
//    public static int screenPosX(final int x) {
//        return MARGIN + x;
//    }
//
//    public static int entryPosX(final int x) {
//        return x - MARGIN;
//    }
//
//    public static int screenPosY(final int y) {
//        return MARGIN_TOP + y;
//    }
//
//    public static int entryPosY(final int y) {
//        return y - MARGIN_TOP;
//    }

    public static int getPromptStartIdx() {
        return MARGIN_LEFT + TermAttributes.PROMPT.length();
    }

    public static boolean onFirstLinePos(final int x, final int y) {
        return y == 0 && x == PROMPT.length();
    }
}
