package com.dcall.core.configuration.app.service.filetransfer;

import com.dcall.core.configuration.app.constant.GitConstant;
import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.filetransfer.FileTransferContext;
import com.dcall.core.configuration.app.entity.filetransfer.FileTransfer;
import com.dcall.core.configuration.app.entity.filetransfer.FileTransferBean;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.app.exception.TechnicalException;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.git.GitService;
import com.dcall.core.configuration.app.service.hash.HashFileService;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.service.task.filetransfer.workspace.WorkspaceTransferTaskServiceImpl;
import com.dcall.core.configuration.app.verticle.filetransfer.FileTransferConsumerVerticle;
import com.dcall.core.configuration.generic.cluster.hazelcast.HazelcastCluster;
import com.dcall.core.configuration.generic.cluster.vertx.uri.VertxURIConfig;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.PathUtils;
import com.dcall.core.configuration.utils.SerializationUtils;
import com.dcall.core.configuration.utils.URIUtils;
import com.dcall.core.configuration.utils.constant.FileType;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Map;

public class FileTransferServiceImpl implements FileTransferService {
    private static final Logger LOG = LoggerFactory.getLogger(FileTransferServiceImpl.class);
    private final MessageService messageService;

    public FileTransferServiceImpl(final MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void publishWorkspace(final RuntimeContext runtimeContext) {
        runtimeContext.serviceContext().serviceProvider().taskServiceProvider().workspaceTransferService().run();
//        new WorkspaceTransferTaskServiceImpl(runtimeContext).run();
    }

    @Override
    public void storeWorkspaceTransferContext(final RuntimeContext runtimeContext, final FileTransferContext fileTransferContext) throws Exception {
        final HashFileService hashFileService = runtimeContext.serviceContext().serviceProvider().hashServiceProvider().hashFileService();
        final Map<String, FileTransfer<String>> fileTransfers = fileTransferContext.getFileTransfers();
        final String hostDir = runtimeContext.serviceContext().serviceProvider().environService().getHostFilesDirectory(runtimeContext);
        final FileUtils fileUtils = FileUtils.getInstance();
        final PathUtils pathUtils = PathUtils.getInstance();
        String destDir = null;

        for (Map.Entry<String, FileTransfer<String>> entry : fileTransfers.entrySet()) {
            if (destDir == null)
                destDir = hashFileService.getHashPath(hostDir, runtimeContext.userContext().getUserHash().getMd5Salt(), entry.getValue().getId());
            final int idx = entry.getValue().getParentPath().indexOf(GitConstant.GIT_FILENAME);
            final String parent = pathUtils.getPlatformPath(entry.getValue().getParentPath().substring(idx, entry.getValue().getParentPath().length()));
            final String filePath = fileUtils.getFilePath(fileUtils.getFilePath(destDir, parent), entry.getValue().getFileName());

            fileUtils.createFile(filePath, entry.getValue().getBytes());
        }

        final String currGitDirPath = fileUtils.getFilePath(destDir, GitConstant.GIT_FILENAME);
        final String newGitDirPath = hashFileService.getHashPath(destDir, runtimeContext.userContext().getUserHash().getMd5Salt(), GitConstant.GIT_FILENAME);
        final File gitRepo = new File(currGitDirPath);

        if (gitRepo.exists()) {
            Files.move(gitRepo.toPath(), new File(newGitDirPath).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            LOG.debug("User git repository '" + gitRepo.getAbsolutePath() + "' replaced with : " + newGitDirPath);
        }
        else
            throw new TechnicalException("Git repository '" + gitRepo.getAbsolutePath() + "' doesn't exist.");
    }
}
