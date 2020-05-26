package com.dcall.core.app.terminal.gui.service.credential.window;

import com.dcall.core.app.terminal.gui.configuration.CredentialFields;
import com.dcall.core.configuration.generic.vto.UserVto;
import com.dcall.core.configuration.generic.entity.user.User;
import com.dcall.core.configuration.generic.entity.user.UserBean;
import com.dcall.core.configuration.utils.StringUtils;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

import java.util.Arrays;

public final class UserCredentialDrawer {
    private final String _PANEL_TITLE = "User configuration";
    private final int _TEXTBOX_SIZE = 30;
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
    private final UserVto userVto;
    private final User user;

    public UserCredentialDrawer(final Screen screen, final UserVto user) {
        this.screen = screen;
        this.userVto = user;

        this.user = user.getUser() != null ? user.getUser() : new UserBean();
    }

    public UserCredentialDrawer build() {
        buildPanel().buildForm().buildLayout();

        return this;
    }

    private UserCredentialDrawer buildPanel() {
        panel.setLayoutManager(new GridLayout(_LAYOUT_NB_COLS));
        return this;
    }

    private Label setStyleLabel(final Label label, final String value) {

        if (userVto.getUser() == null)
            return label;

        label.addStyle(SGR.BOLD);

        label.setForegroundColor(StringUtils.isEmpty(value) ? TextColor.ANSI.WHITE : TextColor.ANSI.BLACK);
        label.setBackgroundColor(StringUtils.isEmpty(value) ? TextColor.ANSI.RED : null);

        return label;
    }

    private UserCredentialDrawer buildForm() {
        emptyLine();
        panel.addComponent(setStyleLabel(new Label(CredentialFields.NAME), user.getName()));
        initTextValue(name, user.getName());
        panel.addComponent(name);

        emptyLine();
        panel.addComponent(setStyleLabel(new Label(CredentialFields.SURNAME), user.getSurname()));
        initTextValue(surname, user.getSurname());
        panel.addComponent(surname);

        emptyLine();
        panel.addComponent(setStyleLabel(new Label(CredentialFields.EMAIL), user.getEmail()));
        initTextValue(email, user.getEmail());
        panel.addComponent(email);

        emptyLine();
        panel.addComponent(setStyleLabel(new Label(CredentialFields.LOGIN), user.getLogin()));
        initTextValue(login, user.getLogin());
        panel.addComponent(login);

        emptyLine();
        panel.addComponent(setStyleLabel(new Label(CredentialFields.PASSWORD), user.getPassword()));
        initTextValue(password, user.getPassword());
        panel.addComponent(password);

        emptyLine();
//        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));

        this.buildAction();
        return this;
    }

    private void initTextValue(final TextBox textBox, final String value) {
       textBox.setText(!StringUtils.isEmpty(value) ? value : "");
       textBox.setPreferredSize(new TerminalSize(_TEXTBOX_SIZE, 1));
    }

    private void emptyLine() {
        panel.addComponent( new EmptySpace());
        panel.addComponent( new EmptySpace());
    }

    private void buildAction() {
        final LayoutData layoutData = GridLayout.createLayoutData(
                GridLayout.Alignment.END,
                GridLayout.Alignment.CENTER);

        panel.addComponent(new Button("Cancel", () -> {
            userVto.setUserExists(true);
            gui.removeWindow(window);
        }).setLayoutData(layoutData));

        panel.addComponent(new Button("Ok", () -> {
            userVto.setUser(setIdentity());
            gui.removeWindow(window);
        }).setLayoutData(layoutData));
    }

    // build
    private void buildLayout() {
        window.setComponent(panel);
        window.setHints(Arrays.asList(Window.Hint.CENTERED));

        gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
        gui.addWindowAndWait(window);
    }

    // setters
    private User setIdentity() {
        user.setName(name.getText().trim());
        user.setSurname(surname.getText().trim());
        user.setLogin(login.getText().trim());
        user.setEmail(email.getText().trim());
        user.setPassword(password.getText().trim());

        return user;
    }
}
