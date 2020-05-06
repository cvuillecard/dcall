package com.dcall.core.app.processor.bean.local.service.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalHelpCommandServiceImpl implements LocalHelpCommandService {
    private static final Logger LOG = LoggerFactory.getLogger(LocalHelpCommandServiceImpl.class);

    private final String HELP_FILE = "help/help.help";

    @Override
    public byte[] usage() {
        byte[] datas;
        try {
            final Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource(HELP_FILE).toURI());
            datas = Files.readAllBytes(path);
        } catch (URISyntaxException | IOException e) {
            LOG.error(e.getMessage());
            datas = e.getMessage().getBytes();
        }

        return datas;
    }

    @Override
    public byte[] execute(final String... params) {
        return this.usage();
    }
}
