package com.dcall.core.configuration.app.service.builtin.publish;

import com.dcall.core.configuration.app.constant.GitConstant;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.app.service.fingerprint.FingerPrintService;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import com.dcall.core.configuration.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class BuiltInPublishServiceImpl extends AbstractCommand implements BuiltInPublishService {
    final static Logger LOG = LoggerFactory.getLogger(BuiltInPublishServiceImpl.class);

    @Override
    public byte[] execute(final String... params) throws Exception {
        return publish(params);
    }

    @Override
    public byte[] execute() throws Exception {
        return publishCertificate();
    }

    @Override
    public byte[] publish(final String... params) throws Exception {
        final String cmd = params[0];
        final String[] args = params != null && params.length > 1 ? Arrays.copyOfRange(params, 1, params.length) : null;

        switch (cmd) {
            case UserConstant.WORKSPACE : return publishWorkspace();
            default : break;
        }
        return null;
    }

    private byte[]  publishWorkspace() {
        final StringBuilder sb = new StringBuilder();

        try {
            runtimeContext.serviceContext().serviceProvider().messageServiceProvider().fileTransferService().publishWorkspace(runtimeContext);
            sb.append("> publish workspace order [path = ").append(ResourceUtils.localProperties().getProperty(GitConstant.SYS_GIT_REPOSITORY)).append("]");
        }
        catch (Exception e) {
            sb.append("Failed to publish workspace : ").append(e.getMessage());
        }

        return sb.toString().getBytes();
    }

    private byte[]  publishCertificate() throws Exception {
        final StringBuilder sb = new StringBuilder();

        final FingerPrintService fingerPrintService = getRuntimeContext().serviceContext().serviceProvider().messageServiceProvider().fingerPrintService();
        fingerPrintService.publishPublicUserCertificate(getRuntimeContext().userContext());

        sb.append("> certificate ").append(((AbstractCipherResource<String>) getRuntimeContext().userContext().getCertificate()).getPath()).append(" published");

        return sb.toString().getBytes();
    }
}
