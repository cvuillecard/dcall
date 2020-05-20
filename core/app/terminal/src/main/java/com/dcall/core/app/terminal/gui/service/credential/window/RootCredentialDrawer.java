package com.dcall.core.app.terminal.gui.service.credential.window;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

import java.util.Arrays;

public final class RootCredentialDrawer {
    private final String _PANEL_TITLE = "Root configuration";
    private final int _LAYOUT_NB_COLS = 2;
    private WindowBasedTextGUI gui;
    private final Window window = new BasicWindow(_PANEL_TITLE);
    private final Panel panel = new Panel();
    private final Screen screen;
    private final TextBox name = new TextBox();
    private final TextBox surname = new TextBox();
    private final TextBox email = new TextBox();
    private final TextBox login = new TextBox();
    private final TextBox password = new TextBox().setMask('*');

    public RootCredentialDrawer(final Screen screen) {
        this.screen = screen;
    }

    public void build() {
        buildPanel().buildForm().buildLayout();
    }

    private RootCredentialDrawer buildPanel() {
        panel.setLayoutManager(new GridLayout(_LAYOUT_NB_COLS));
        return this;
    }

    private RootCredentialDrawer buildForm() {
        emptyLine();
        panel.addComponent(new Label("name"));
        panel.addComponent(name);

        emptyLine();
        panel.addComponent(new Label("surname"));
        panel.addComponent(surname);

        emptyLine();
        panel.addComponent(new Label("email"));
        panel.addComponent(email);

        emptyLine();
        panel.addComponent(new Label("login"));
        panel.addComponent(login);

        emptyLine();
        panel.addComponent(new Label("password"));
        panel.addComponent(password);

        emptyLine();
//        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));

        this.buildAction();
        return this;
    }

    private void emptyLine() {
        panel.addComponent( new EmptySpace());
        panel.addComponent( new EmptySpace());
    }

    private void buildAction() {
        panel.addComponent(new Button("Skip", () -> {
            gui.removeWindow(window);
        }));

        panel.addComponent(new Button("Create", () -> {
            gui.removeWindow(window);
        }));
    }

    private void buildLayout() {
        window.setComponent(panel);
        window.setHints(Arrays.asList(Window.Hint.CENTERED));

        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
        gui.addWindowAndWait(window);
    }

}
