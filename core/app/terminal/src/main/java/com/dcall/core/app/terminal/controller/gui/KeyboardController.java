package com.dcall.core.app.terminal.controller.gui;

import com.dcall.core.app.terminal.constant.KeyboardAction;
import com.googlecode.lanterna.input.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class KeyboardController {
    private static final Logger LOG = LoggerFactory.getLogger(KeyboardController.class);
    private static KeyStroke keyPressed;
    private static final Map<KeyboardAction, Runnable> controlKeys = new HashMap<>(); // IOHandler::methods -> inputHandler / outputHandler
}
