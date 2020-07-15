package com.dcall.core.configuration.app.verticle.filetransfer;

import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.filetransfer.FileTransferContext;
import com.dcall.core.configuration.app.context.fingerprint.FingerPrintContext;
import com.dcall.core.configuration.app.context.transfer.TransferContext;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import com.dcall.core.configuration.app.entity.filetransfer.FileTransfer;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.app.service.filetransfer.FileTransferService;
import com.dcall.core.configuration.generic.cluster.hazelcast.HazelcastCluster;
import com.dcall.core.configuration.generic.cluster.vertx.uri.VertxURIConfig;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.SerializationUtils;
import com.dcall.core.configuration.utils.URIUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class  FileTransferConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(FileTransferConsumerVerticle.class);
    @Autowired private RuntimeContext runtimeContext;

    private VertxURIContext uriContext() {
        final VertxURIContext uriContext = new VertxURIContext();

        return uriContext.setBaseLocalAppUri(this.getClass().getPackage().getName())
                .setBaseRemoteAppUri(uriContext.getBaseLocalAppUri())
                .setLocalConsumerUri(this.getClass().getName())
                .setRemoteConsumerUri(uriContext.getLocalConsumerUri());
    }

    @Override
    public void start() {
        final VertxURIContext uriContext = uriContext();
        final FingerPrintContext fingerPrintContext = runtimeContext.clusterContext().fingerPrintContext();
        final String privateUri = URIUtils.getUri(uriContext.getLocalConsumerUri(), HazelcastCluster.getLocalUuid());
        final String completeWorkspaceUri = URIUtils.getUri(privateUri, URIUtils.getUri(VertxURIConfig.COMPLETE_DOMAIN, UserConstant.WORKSPACE));

        // final MessageConsumer<Object> publicConsumer = vertx.eventBus().consumer(uriContext.getLocalConsumerUri());
        final MessageConsumer<Object> privateConsumer = vertx.eventBus().consumer(privateUri);
        final MessageConsumer<Object> completeConsumer = vertx.eventBus().consumer(completeWorkspaceUri);

        privateConsumer.handler(handler -> handlePrivateMessage(fingerPrintContext, handler));
        completeConsumer.handler(handler -> handleCompleteMessage(fingerPrintContext, handler));
    }

    private void handleCompleteMessage(final FingerPrintContext fingerPrintContext, final Message<Object> handler) {
        vertx.executeBlocking(future -> {
            final com.dcall.core.configuration.app.entity.message.Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
            final FingerPrint<String> fromFingerPrint = fingerPrintContext.getFingerprints().get(msg.getId());

            if (!msg.getId().equals(HazelcastCluster.getLocalUuid()) && fromFingerPrint != null) {
                final TransferContext transferContext = runtimeContext.dataContext().transferContext();
                final FileTransferService fileTransferService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().fileTransferService();
                final byte[] bytes = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService().decryptMessage(runtimeContext, msg);
                final FileTransfer<String> fileTransfer = SerializationUtils.deserialize(bytes);

                if (transferContext.getFileTransfersContext().get(fileTransfer.getId()) != null) {
                    fileTransferService.storeWorkspaceTransferContext(runtimeContext, transferContext.getFileTransfersContext().get(fileTransfer.getId()));
                    transferContext.getFileTransfersContext().remove(fileTransfer.getId());
                }
            }
            future.complete();
        }, res -> {
            if (res.succeeded())
                handler.reply("> SUCCESS");
            else
                handler.reply(res.cause());
        });
    }

    private void handlePrivateMessage(final FingerPrintContext fingerPrintContext, final Message<Object> handler) {
        vertx.executeBlocking(future -> {

            try {
                final com.dcall.core.configuration.app.entity.message.Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
                final FingerPrint<String> fromFingerPrint = fingerPrintContext.getFingerprints().get(msg.getId());

                if (!msg.getId().equals(HazelcastCluster.getLocalUuid()) && fromFingerPrint != null) {
                    final TransferContext transferContext = runtimeContext.dataContext().transferContext();
                    final byte[] bytes = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService().decryptMessage(runtimeContext, msg);
                    final FileTransfer<String> fileTransfer = SerializationUtils.deserialize(bytes);
                    final String cacheId = FileUtils.getInstance().getFilePath(fileTransfer.getParentPath(), fileTransfer.getFileName());

                    if (transferContext.getFileTransfersContext().get(fileTransfer.getId()) == null) {
                        final FileTransferContext fileTransferContext = new FileTransferContext();
                        fileTransferContext.getFileTransfers().put(cacheId, fileTransfer);
                        transferContext.getFileTransfersContext().put(fileTransfer.getId(), fileTransferContext);
                    } else {
                        final FileTransferContext fileTransferContext = transferContext.getFileTransfersContext().get(fileTransfer.getId());
                        if (fileTransferContext.getFileTransfers().get(cacheId) == null)
                            fileTransferContext.getFileTransfers().put(cacheId, fileTransfer);
                        else {
                            final FileTransfer<String> cacheFileTransfer = fileTransferContext.getFileTransfers().get(cacheId);
                            final ByteArrayOutputStream os = new ByteArrayOutputStream();
                            os.write(cacheFileTransfer.getBytes());
                            os.write(fileTransfer.getBytes());

                            cacheFileTransfer.setBytes(os.toByteArray());
                            os.close();
                        }
                    }
                    LOG.debug("Received : " + fileTransfer.toString());
                }
                future.complete();
            }
            catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }, res -> {
            if (res.succeeded())
                handler.reply("> SUCCESS");
            else
                handler.reply(res.cause());
        });
    }

}
