package com.dcall.core.app.terminal.bus.service.builtin.snapshot;

import com.dcall.core.configuration.app.constant.GitMessage;
import com.dcall.core.configuration.app.provider.version.VersionServiceProvider;
import com.dcall.core.configuration.generic.entity.repository.GitRepository;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import com.dcall.core.configuration.utils.StringUtils;
import org.eclipse.jgit.lib.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class BuiltInSnapshotServiceImpl extends AbstractCommand implements BuiltInSnapshotService {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInSnapshotServiceImpl.class);

    @Override
    public byte[] execute(final String... params) {
        return snapshot(StringUtils.listToString(Arrays.asList(params)));
    }

    @Override
    public byte[] execute() {
        return snapshot(null);
    }

    @Override
    public byte[] snapshot(final String msg) {
        final GitRepository repository = getContext().systemContext().getRepository();
        final VersionServiceProvider provider = getContext().serviceContext().serviceProvider().versionServiceProvider();

        if (repository != null) {
            final String defaultMsg = commitMessage("Snapshot");
            final String commitMsg = msg != null && !msg.isEmpty() ?  defaultMsg + " - " + msg : defaultMsg;
            final String revMsg =  provider.gitService().commitSystemRepository(getContext(), repository, commitMsg).getFullMessage();

            return (provider.gitService().getRefHash(repository, Constants.HEAD) + " - " + revMsg).getBytes();
        }

        return null;
    }
}
