package com.dcall.core.configuration.app.provider.message;

import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.provider.user.UserServiceProvider;
import com.dcall.core.configuration.app.provider.version.VersionServiceProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.filetransfer.FileTransferService;
import com.dcall.core.configuration.app.service.filetransfer.FileTransferServiceImpl;
import com.dcall.core.configuration.app.service.fingerprint.FingerPrintService;
import com.dcall.core.configuration.app.service.fingerprint.FingerPrintServiceImpl;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.service.message.MessageServiceImpl;

import java.io.Serializable;

public final class MessageServiceProvider implements Serializable {
    private final UserServiceProvider userServiceProvider;
    private final MessageService messageService;
    private final FingerPrintService fingerPrintService;
    private final FileTransferService fileTransferService;

    public MessageServiceProvider(final UserServiceProvider userServiceProvider) {
        this.userServiceProvider = userServiceProvider;
        this.messageService = new MessageServiceImpl();
        this.fingerPrintService = new FingerPrintServiceImpl(messageService);
        this.fileTransferService = new FileTransferServiceImpl(messageService);
    }

    // getters
    public UserServiceProvider userServiceProvider() { return this.userServiceProvider; }
    public MessageService messageService() { return this.messageService; }

    // utils
    public EnvironService environService() { return this.userServiceProvider.environService(); }
    public HashServiceProvider hashServiceProvider() { return this.environService().getHashServiceProvider(); }
    public VersionServiceProvider versionServiceProvider() { return this.userServiceProvider.getVersionServiceProvider(); }
    public FingerPrintService fingerPrintService() { return this.fingerPrintService; }
    public FileTransferService fileTransferService() { return this.fileTransferService; }

}
