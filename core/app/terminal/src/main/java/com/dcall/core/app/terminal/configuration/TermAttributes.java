package com.dcall.core.app.terminal.configuration;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TermAttributes {
    private static final Logger LOG = LoggerFactory.getLogger(TermAttributes.class);

    // WINDOW
    public static final String FRAME_TITLE = "DCall terminal";
    public static final int FRAME_NB_COLS = 80;
    public static final int FRAME_NB_ROWS = 24;

    // HEADER
    public static final TextColor HEADER_BACKGROUND = new TextColor.RGB(230, 230, 0); // YELLOW
    public static final TextColor HEADER_FOREGROUND = new TextColor.RGB(0, 0, 0); // BLACK
    public static final SGR[] HEADER_STYLE = new SGR[] { SGR.BOLD };
    public static final String HEADER_TITLE = "DCall (More you do, more it works..)";

    // PROMPT
    public static final TextColor PROMPT_BACKGROUND = TextColor.ANSI.BLACK;
    public static final TextColor PROMPT_FOREGROUND = TextColor.ANSI.CYAN;
    public static final SGR[] PROMPT_STYLE = new SGR[] { SGR.BOLD };
    public static final String PROMPT = " DCall >";

    // INPUT
    public static final TextColor INPUT_BACKGROUND = TextColor.ANSI.BLACK;
    public static final TextColor INPUT_FOREGROUND = TextColor.ANSI.WHITE;
    public static final SGR[] INPUT_STYLE = new SGR[] { SGR.BOLD };

    // SCROLL
    public static final int SCROLL_PADDING_UP = 1;
    public static final int SCROLL_PADDING_DOWN = -1;
}
