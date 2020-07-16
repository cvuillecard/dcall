package com.dcall.core.configuration.app.service.builtin.publish;

import com.dcall.core.configuration.app.constant.GitConstant;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.app.service.filetransfer.FileTransferService;
import com.dcall.core.configuration.app.service.fingerprint.FingerPrintService;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import com.dcall.core.configuration.utils.ResourceUtils;

import java.util.Arrays;

public final class BuiltInPublishServiceImpl extends AbstractCommand implements BuiltInPublishService {

    @Override
    public byte[] execute(final String... params) throws Exception {
        return publish(params);
    }

    @Override
    public byte[] execute() throws Exception {
        return publishCertificate();
    }

    @Override
    public byte[] publish(final String... params) {
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
            final FileTransferService fileTransferService = getContext().serviceContext().serviceProvider().messageServiceProvider().fileTransferService();
            fileTransferService.publishWorkspace(getContext());

            sb.append("> " + ResourceUtils.localProperties().getProperty(GitConstant.SYS_GIT_REPOSITORY) + " published");
        }
        catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString().getBytes();
    }

    private byte[]  publishCertificate() {
        final StringBuilder sb = new StringBuilder();

        final FingerPrintService fingerPrintService = getContext().serviceContext().serviceProvider().messageServiceProvider().fingerPrintService();
        fingerPrintService.publishPublicUserCertificate(getContext().userContext());

        sb.append("> certificate " + ((AbstractCipherResource<String>)getContext().userContext().getCertificate()).getPath() + " published");

        return sb.toString().getBytes();
    }
}
