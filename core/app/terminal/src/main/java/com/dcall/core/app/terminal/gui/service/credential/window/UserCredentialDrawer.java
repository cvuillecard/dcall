package com.dcall.core.app.terminal.gui.service.credential.window;

import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.configuration.credential.CredentialInfo;
import com.dcall.core.configuration.entity.identity.Identity;
import com.dcall.core.configuration.entity.identity.IdentityBean;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

import java.util.Arrays;

public final class UserCredentialDrawer {
    private final String _PANEL_TITLE = "User configuration";
    private final int _LAYOUT_NB_COLS = 2;
    private WindowBasedTextGUI gui;
    private final Window window = new BasicWindow(_PANEL_TITLE);
    private final Panel panel = new Panel();
    private final Screen screen;
    private final CredentialInfo credentials;
    private final TextBox name = new TextBox();
    private final TextBox surname = new TextBox();
    private final TextBox email = new TextBox();
    private final TextBox login = new TextBox();
    private final TextBox password = new TextBox().setMask('*');
    private final Identity identity = new IdentityBean();

    public UserCredentialDrawer(final Screen screen, final CredentialInfo credentials) {
        this.screen = screen;
        this.credentials = credentials;
    }

    public UserCredentialDrawer build() {
        buildPanel().buildForm().buildLayout();

        return this;
    }

    private UserCredentialDrawer buildPanel() {
        panel.setLayoutManager(new GridLayout(_LAYOUT_NB_COLS));
        return this;
    }

    private UserCredentialDrawer buildForm() {
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

//        emptyLine();
//        panel.addComponent(new Label("password"));
//        panel.addComponent(password);

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
            credentials.setUser(true);
            gui.removeWindow(window);
        }));

        panel.addComponent(new Button("Create", () -> {
            credentials.setIdentity(setIdentity());
            gui.removeWindow(window);
        }));
    }

    // build
    private void buildLayout() {
        window.setComponent(panel);
        window.setHints(Arrays.asList(Window.Hint.CENTERED));

        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
        gui.addWindowAndWait(window);
    }

    // setters
    private Identity setIdentity() {
        identity.setName(name.getText().trim());
        identity.setSurname(surname.getText().trim());
        identity.setLogin(login.getText().trim());
        identity.setEmail(email.getText().trim());

        return identity;
    }
}
