package com.dcall.core.app.terminal.gui.service.credential.window;

import com.dcall.core.configuration.app.constant.GitConstant;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.constant.LoginOption;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.generic.entity.user.User;
import com.dcall.core.configuration.generic.entity.user.UserBean;
import com.dcall.core.configuration.utils.ResourceUtils;
import com.dcall.core.configuration.utils.StringUtils;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

import java.io.File;
import java.util.Arrays;

public final class UserCredentialDrawer {
    private String _PANEL_TITLE = "User configuration";
    private final int _TEXTBOX_SIZE = 30;
    private final int _LAYOUT_NB_COLS = 2;
    private WindowBasedTextGUI gui;
    private boolean firstDisplay = true;
    private LoginOption loginOption = LoginOption.LOGIN;
    private final Window window = new BasicWindow(_PANEL_TITLE);
    private final Panel panel = new Panel();
    private final Screen screen;
    private final TextBox name = new TextBox();
    private final TextBox surname = new TextBox();
    private final TextBox email = new TextBox();
    private final TextBox login = new TextBox();
    private final TextBox password = new TextBox().setMask('*');
    private final UserContext userContext;
    private User user;

    public UserCredentialDrawer(final Screen screen, final UserContext userContext) {
        this.screen = screen;
        this.userContext = userContext;

        if (userContext.getUser() == null)
            userContext.setUser(new UserBean());
        else
            firstDisplay = false;

        this.user = userContext.getUser();
    }

    public LoginOption build(final LoginOption option) {
        this.loginOption = option;

        buildPanel().buildForm().buildLayout();

        return loginOption;
    }

    private UserCredentialDrawer buildPanel() {
        panel.setLayoutManager(new GridLayout(_LAYOUT_NB_COLS));
        return this;
    }

    private Label setStyleLabel(final Label label, final String value) {
        if (firstDisplay)
            return label;

        label.addStyle(SGR.BOLD);

        label.setForegroundColor(StringUtils.isEmpty(value) ? TextColor.ANSI.WHITE : TextColor.ANSI.BLACK);
        label.setBackgroundColor(StringUtils.isEmpty(value) ? TextColor.ANSI.RED : null);

        return label;
    }

    private UserCredentialDrawer buildForm() {
        return loginOption.equals(LoginOption.LOGIN) ? buildFormLogin() : buildFormCreate();
    }

    private UserCredentialDrawer buildFormCreate() {
        emptyLine();
        panel.addComponent(setStyleLabel(new Label(UserConstant.NAME), user.getName()));
        initTextValue(name, user.getName());
        panel.addComponent(name);

        emptyLine();
        panel.addComponent(setStyleLabel(new Label(UserConstant.SURNAME), user.getSurname()));
        initTextValue(surname, user.getSurname());
        panel.addComponent(surname);

        emptyLine();
        panel.addComponent(setStyleLabel(new Label(UserConstant.EMAIL), user.getEmail()));
        initTextValue(email, user.getEmail());
        panel.addComponent(email);

        emptyLine();
        panel.addComponent(setStyleLabel(new Label(UserConstant.LOGIN), user.getLogin()));
        initTextValue(login, user.getLogin());
        panel.addComponent(login);

        emptyLine();
        panel.addComponent(setStyleLabel(new Label(UserConstant.PASSWORD), user.getPassword()));
        initTextValue(password, user.getPassword());
        panel.addComponent(password);

        emptyLine();

        this.buildCreateAction();
        return this;
    }

    private UserCredentialDrawer buildFormLogin() {
        emptyLine();
        panel.addComponent(setStyleLabel(new Label(UserConstant.EMAIL), user.getEmail()));
        initTextValue(email, user.getEmail());
        panel.addComponent(email);

        emptyLine();
        panel.addComponent(setStyleLabel(new Label(UserConstant.PASSWORD), user.getPassword()));
        initTextValue(password, user.getPassword());
        panel.addComponent(password);

        emptyLine();

        this.buildLoginAction();
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

    private void buildLoginAction() {
        final LayoutData layoutData = GridLayout.createLayoutData(
                GridLayout.Alignment.END,
                GridLayout.Alignment.CENTER);

        panel.addComponent(new Button("Log me !", () -> {
            fillUser();
            gui.removeWindow(window);
        }).setLayoutData(layoutData));

        panel.addComponent(new Button("New User", () -> {
            resetUser();
            gui.removeWindow(window);
        }).setLayoutData(layoutData));
    }

    private void resetUser() {
        userContext.setUser(null);
        loginOption = LoginOption.NEW_USER;
    }

    private void buildCreateAction() {
        final LayoutData layoutData = GridLayout.createLayoutData(
                GridLayout.Alignment.END,
                GridLayout.Alignment.CENTER);

        panel.addComponent(new Button("Cancel", () -> {
            resetUser();
            loginOption = LoginOption.LOGIN;
            gui.removeWindow(window);
        }).setLayoutData(layoutData));

        panel.addComponent(new Button("Ok", () -> {
            fillUser();
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
    private User fillUser() {
        return user
                .setName(name.getText().trim())
                .setSurname(surname.getText().trim())
                .setLogin(login.getText().trim())
                .setEmail(email.getText().trim())
                .setPassword(password.getText().trim())
                .setWorkspace(ResourceUtils.localProperties().getProperty(GitConstant.SYS_GIT_REPOSITORY) + File.separator + UserConstant.WORKSPACE);
    }
}
