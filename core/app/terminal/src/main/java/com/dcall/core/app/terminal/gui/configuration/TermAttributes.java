package com.dcall.core.app.terminal.gui.configuration;

import com.dcall.core.configuration.app.exception.TechnicalException;
import com.googlecode.lanterna.TextColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
    public static int MIN_SIZE_COEF = 2;
    public static int DEFAULT_SCROLL_PADDING = 4;
    public static int DEFAULT_BLINKING_DURATION_MS = 500;
    public static int DEFAULT_FONT_SIZE = 14;
    public static int DEFAULT_FONT_STYLE = Font.PLAIN;
    //    public static String DEFAULT_FONT_FAMILY = "Courier New";
    //    public static String DEFAULT_FONT_FAMILY = "Monospaced";
    public static String DEFAULT_FONT_FAMILY = "DejaVu Sans Mono";
    public static Font DEFAULT_FONT_POLICY = new Font(TermAttributes.DEFAULT_FONT_FAMILY, TermAttributes.DEFAULT_FONT_STYLE, TermAttributes.DEFAULT_FONT_SIZE);


    // HEADER
    public static final TextColor HEADER_BACKGROUND = new TextColor.RGB(230, 230, 0); // YELLOW
    public static final TextColor HEADER_FOREGROUND = new TextColor.RGB(0, 0, 0); // BLACK
    public static final String HEADER_TITLE = "DCall (More you do, more it works..)";
    public static final String USER_CREATE_WAIT = "PLEASE WAIT A MOMENT FOR USER CREATION...";

    // PROMPT
    public static final TextColor PROMPT_BACKGROUND = TextColor.ANSI.BLACK;
    public static final TextColor PROMPT_FOREGROUND = TextColor.ANSI.CYAN;
    public static final String PROMPT = "DCall ~ ";

    // INPUT
    public static final int DEF_INPUT_NB_LINE = 255;
    public static final TextColor INPUT_BACKGROUND = TextColor.ANSI.BLACK;
    public static final TextColor INPUT_FOREGROUND = TextColor.ANSI.WHITE;

    // UTILS
    public static String getPrompt() {
        try {
            return InetAddress.getLocalHost().getHostName() + "@" + PROMPT;
        } catch (UnknownHostException e) {
            new TechnicalException(e).log();
        }

        return PROMPT;
    }

    public static int getMinScreenWidth() {
        return DEF_FRAME_NB_COLS / MIN_SIZE_COEF;
    }

    public static int getMinScreenHeight() {
        return DEF_FRAME_NB_ROWS / MIN_SIZE_COEF;
    }

    public static int getMaxLineWidth() {
        return getTotalLineWidth() - 1;
    }

    public static int getTotalLineWidth() {
        return FRAME_NB_COLS - getMarginWidth();
    }

    public static int getMarginWidth() {
        return MARGIN_LEFT + MARGIN_RIGHT;
    }

    public static int getPromptStartIdx() {
        return MARGIN_LEFT + TermAttributes.getPrompt().length();
    }

    public static boolean onFirstLinePos(final int x, final int y) {
        return y == 0 && x == getPrompt().length();
    }

    public static int getNbLines(int length) {
        if (length > 0) {
            final int nbLines = (length / getTotalLineWidth()) + (length % getTotalLineWidth() > 0 ? 1 : 0);
            return nbLines == 0 ? 1 : nbLines;
        }

        return 0;
    }

    public static int getScrollPadding() {
        return FRAME_NB_ROWS / DEFAULT_SCROLL_PADDING;
    }
}
