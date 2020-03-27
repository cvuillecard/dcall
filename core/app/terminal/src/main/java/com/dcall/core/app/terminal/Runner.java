package com.dcall.core.app.terminal;

import com.dcall.core.app.terminal.vertx.InputConsumerVerticle;
import com.dcall.core.app.terminal.vertx.TerminalApplicationVerticle;
import com.dcall.core.configuration.runner.RunnerConfigurator;
import com.dcall.core.configuration.vertx.VertxApplication;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Runner {
    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

    public static void main(final String[] args) {
        final RunnerConfigurator configurator = new RunnerConfigurator(Runner.class.getPackage().getName())
                .defaultValidateArgs(args)
                .parseOptions(args);

        VertxApplication.init(configurator.getHost(), configurator.getPort());
        VertxApplication.startOnCluster(
                false,
                configurator.getPeers().toArray(new String[configurator.getPeers().size()]),
                new InputConsumerVerticle(),
                new TerminalApplicationVerticle()
        );
    }

    public static void initTerminal() {
        Thread lanterna = new Thread(new Runnable() {
            public void run() {
                try {
                    Terminal terminal = new DefaultTerminalFactory().createTerminal();
                    Screen screen = new TerminalScreen(terminal);
                    screen.startScreen();
                    Panel panel = new Panel();
                    panel.setLayoutManager(new GridLayout(2));
                    panel.addComponent(new Label("login"));
                    panel.addComponent(new TextBox());
                    panel.addComponent(new Label("password"));
                    panel.addComponent(new TextBox().setMask('*'));
                    panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
                    panel.addComponent(new Button("Submit"));
                    Window window = new BasicWindow("Authenticate");
                    window.setComponent(panel);
                    window.setHints(Arrays.asList(Window.Hint.CENTERED));
                    WindowBasedTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
                    gui.addWindowAndWait(window);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "test");
        lanterna.start();
    }
}