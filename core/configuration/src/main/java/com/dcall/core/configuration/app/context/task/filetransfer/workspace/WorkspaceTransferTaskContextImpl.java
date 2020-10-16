package com.dcall.core.configuration.app.context.task.filetransfer.workspace;

import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.entity.filetransfer.FileTransfer;
import com.dcall.core.configuration.app.entity.filetransfer.FileTransferBean;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.fingerprint.FingerPrintService;
import com.dcall.core.configuration.app.service.git.GitService;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.verticle.filetransfer.FileTransferConsumerVerticle;
import com.dcall.core.configuration.generic.cluster.vertx.uri.VertxURIConfig;
import com.dcall.core.configuration.app.context.task.AbstractTaskContext;
import com.dcall.core.configuration.utils.URIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class WorkspaceTransferTaskContextImpl extends AbstractTaskContext implements WorkspaceTransferTaskContext {
    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceTransferTaskContextImpl.class);

    private EnvironService environService;
    private FingerPrintService fingerPrintService;
    private MessageService messageService;
    private String publicId;
    private FileTransfer<String> fileTransfer;
    private GitService gitService;

    public WorkspaceTransferTaskContextImpl(final RuntimeContext runtimeContext) { super(runtimeContext); }

    // main
    @Override
    public WorkspaceTransferTaskContextImpl init() {
        try {
            environService = getRuntimeContext().serviceContext().serviceProvider().environService();
            fingerPrintService = getRuntimeContext().serviceContext().serviceProvider().messageServiceProvider().fingerPrintService();
            messageService = getRuntimeContext().serviceContext().serviceProvider().messageServiceProvider().messageService();

            if (!environService.getHostFilesMode(getRuntimeContext()))
                throw new IllegalStateException("env.allow_host_files = false -> must be true to send files");

            fingerPrint = fingerPrintService.nextFingerPrint(getRuntimeContext().clusterContext().fingerPrintContext());
            publicId = environService.getPublicId(getRuntimeContext());
            fileTransfer = new FileTransferBean().setId(publicId);
            gitService = getRuntimeContext().serviceContext().serviceProvider().versionServiceProvider().gitService();

            this.setRunURI(URIUtils.getUri(FileTransferConsumerVerticle.class.getName(), fingerPrint.getId()));
            this.setCompleteURI(URIUtils.getUri(URIUtils.getUri(FileTransferConsumerVerticle.class.getName(), fingerPrint.getId()), URIUtils.getUri(VertxURIConfig.COMPLETE_DOMAIN, UserConstant.WORKSPACE)));
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return this;
    }

    // getters
    @Override public EnvironService getEnvironService() { return environService; }
    @Override public FingerPrintService getFingerPrintService() { return fingerPrintService; }
    @Override public MessageService getMessageService() { return messageService; }
    @Override public String getPublicId() { return publicId; }
    @Override public FileTransfer<String> getFileTransfer() { return fileTransfer; }
    @Override public GitService getGitService() { return gitService; }

    // setters
    @Override public WorkspaceTransferTaskContext setEnvironService(final EnvironService environService) { this.environService = environService; return this; }
    @Override public WorkspaceTransferTaskContext setFingerPrintService(final FingerPrintService fingerPrintService) { this.fingerPrintService = fingerPrintService; return this; }
    @Override public WorkspaceTransferTaskContext setMessageService(final MessageService messageService) { this.messageService = messageService; return this; }
    @Override public WorkspaceTransferTaskContext setPublicId(final String publicId) { this.publicId = publicId; return this; }
    @Override public WorkspaceTransferTaskContext setFileTransfer(final FileTransfer<String> fileTransfer) { this.fileTransfer = fileTransfer; return this; }
    @Override public WorkspaceTransferTaskContext setGitService(final GitService gitService) { this.gitService = gitService; return this; }

}
