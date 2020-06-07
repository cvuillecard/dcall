package com.dcall.core.app.terminal.bus.service.builtin.snapshot;

import com.dcall.core.configuration.app.constant.GitMessage;
import com.dcall.core.configuration.app.provider.version.VersionServiceProvider;
import com.dcall.core.configuration.generic.entity.repository.GitRepository;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuiltInSnapshotServiceImpl extends AbstractCommand implements BuiltInSnapshotService {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInSnapshotServiceImpl.class);

    @Override
    public byte[] execute(final String... params) {
        return new byte[0];
    }

    @Override
    public byte[] execute() {
        return commit();
    }

    public byte[] commit() {
        final GitRepository repository = getContext().systemContext().getRepository();
        final VersionServiceProvider provider = getContext().serviceContext().serviceProvider().versionServiceProvider();

        if (repository != null) {
            return provider
                    .gitService()
                    .commitSystemRepository(
                            getContext(),
                            repository,
                            GitMessage.getLocalSnapshotCommitMsg(this.getContext().userContext().getUser())
                    )
                    .getFullMessage()
                    .getBytes();
        }

        return null;
    }
}
