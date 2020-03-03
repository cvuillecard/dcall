package com.dcall.core.app.client.terminal.bus.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public final class ClipBoard {
    private static final Logger LOG = LoggerFactory.getLogger(ClipBoard.class);
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static void setContent(final String s) {
        if (s != null)
            clipboard.setContents(new StringSelection(s), null);
    }

    public static String getContent() {
        final Transferable contents = clipboard.getContents(null);

        if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return contents.getTransferData(DataFlavor.stringFlavor).toString();
            } catch (UnsupportedFlavorException | IOException e) {
                LOG.error(ClipBoard.class.getName() + " > ERROR < " + e.getMessage());
            }
        }
        return null;
    }
}
