package com.dcall.core.configuration.app.service.filetransfer;

import com.dcall.core.configuration.app.constant.GitConstant;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.filetransfer.FileTransferContext;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.entity.filetransfer.FileTransfer;
import com.dcall.core.configuration.app.entity.filetransfer.FileTransferBean;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.exception.FunctionalException;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.git.GitService;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.verticle.filetransfer.FileTransferConsumerVerticle;
import com.dcall.core.configuration.generic.cluster.vertx.uri.VertxURIConfig;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.PathUtils;
import com.dcall.core.configuration.utils.SerializationUtils;
import com.dcall.core.configuration.utils.URIUtils;
import com.dcall.core.configuration.utils.constant.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Map;

public class FileTransferServiceImpl implements FileTransferService {
    private static final Logger LOG = LoggerFactory.getLogger(FileTransferServiceImpl.class);
    private final MessageService messageService;

    public FileTransferServiceImpl(final MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void publishWorkspace(final RuntimeContext runtimeContext) {
        try {
            final EnvironService environService = runtimeContext.serviceContext().serviceProvider().environService();
            final FingerPrint<String> nextFingerPrint = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().fingerPrintService().nextFingerPrint(runtimeContext.clusterContext().fingerPrintContext());
            final boolean hostFileMode = environService.getHostFilesMode(runtimeContext);

            if (hostFileMode) {
                final String publicId = environService.getPublicId(runtimeContext);
                final FileTransfer<String> fileTransfer = new FileTransferBean().setId(publicId);
                final GitService gitService = runtimeContext.serviceContext().serviceProvider().versionServiceProvider().gitService();

                sendFileRecursively(runtimeContext, fileTransfer, gitService.getSystemRepository(), GitConstant.GIT_FILENAME, nextFingerPrint);
                completeFileTransfer(runtimeContext, nextFingerPrint);
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public void sendFileRecursively(final RuntimeContext runtimeContext, final FileTransfer<String> fileTransfer, final String parentPath, final String fileName, final FingerPrint<String> fingerPrint) {
        try {
            final FileUtils fileUtils = FileUtils.getInstance();
            final String filePath = fileUtils.getFilePath(parentPath, fileName);
            final File file = new File(filePath);

            fileTransfer.setParentPath(parentPath).setFileName(fileName);

            if (file.isDirectory())
                Arrays.stream(file.listFiles()).forEach(f -> sendFileRecursively(runtimeContext, fileTransfer, filePath, f.getName(), fingerPrint));
            else {
                final FileInputStream is = new FileInputStream(file);
                fileTransfer.setBytes(fileUtils.readAllBytes(is)).setFileType(FileType.FILE);
                is.close();
                sendFileTransfer(runtimeContext, fileTransfer, fingerPrint);
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public void sendFileTransfer(final RuntimeContext runtimeContext, final FileTransfer<String> fileTransfer, final FingerPrint<String> fingerPrint) {
        final String uri = URIUtils.getUri(FileTransferConsumerVerticle.class.getName(), fingerPrint.getId());

        messageService.sendEncryptedChunk(runtimeContext, uri, SerializationUtils.serialize(fileTransfer), fingerPrint);
    }

    @Override
    public void completeFileTransfer(final RuntimeContext runtimeContext, final FingerPrint<String> fingerPrint) {
        final String publicId = runtimeContext.serviceContext().serviceProvider().environService().getPublicId(runtimeContext);
        final FileTransfer<String> fileTransfer = new FileTransferBean().setId(publicId);
        final String uri = URIUtils.getUri(URIUtils.getUri(FileTransferConsumerVerticle.class.getName(), fingerPrint.getId()), URIUtils.getUri(VertxURIConfig.COMPLETE_DOMAIN, UserConstant.WORKSPACE));

        messageService.sendEncryptedChunk(runtimeContext, uri, SerializationUtils.serialize(fileTransfer), fingerPrint);
    }

    @Override
    public void storeWorkspaceTransferContext(final RuntimeContext runtimeContext, final FileTransferContext fileTransferContext) {
        final Map<String, FileTransfer<String>> fileTransfers = fileTransferContext.getFileTransfers();
        final String hostDir = runtimeContext.serviceContext().serviceProvider().environService().getHostFilesDirectory(runtimeContext);
        final FileUtils fileUtils = FileUtils.getInstance();
        final PathUtils pathUtils = PathUtils.getInstance();

        for (Map.Entry<String, FileTransfer<String>> entry : fileTransfers.entrySet()) {
            final String destDir = runtimeContext.serviceContext().serviceProvider().hashServiceProvider().hashFileService().getHashPath(hostDir, runtimeContext.userContext().getUserHash().getMd5Salt(), entry.getValue().getId());
            final int idx = entry.getValue().getParentPath().indexOf(GitConstant.GIT_FILENAME);
            final String parent = pathUtils.getPlatformPath(entry.getValue().getParentPath().substring(idx, entry.getValue().getParentPath().length()));
            final String filePath = fileUtils.getFilePath(fileUtils.getFilePath(destDir, parent), entry.getValue().getFileName());

            fileUtils.createFile(filePath, entry.getValue().getBytes());
        }
    }
}
