package com.dcall.core.app.processor.bean.local.service.command;

import com.dcall.core.configuration.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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
            datas = FileUtils.getInstance().readAllBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream(HELP_FILE));
        } catch (IOException e) {
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
