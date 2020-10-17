package com.dcall.core.app.processor.vertx.command;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.service.builtin.BuiltInService;
import com.dcall.core.configuration.app.service.builtin.BuiltInServiceImpl;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.generic.cluster.vertx.AbstractContextVerticle;
import com.dcall.core.configuration.generic.parser.Parser;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.impl.BuiltInOperandSolver;
import com.dcall.core.configuration.generic.parser.expression.operator.solver.impl.BuiltInOperatorSolver;
import com.dcall.core.configuration.utils.HelpUtils;
import com.dcall.core.configuration.utils.URIUtils;
import com.dcall.core.configuration.generic.cluster.hazelcast.HazelcastCluster;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class CommandProcessorConsumerVerticle extends AbstractContextVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(CommandProcessorConsumerVerticle.class);

    private final BuiltInService builtInService = new BuiltInServiceImpl();

    @Autowired
    public CommandProcessorConsumerVerticle(final RuntimeContext runtimeContext) {
        super(runtimeContext);
    }

    private void execute(final Message<Object> handler, final com.dcall.core.configuration.app.entity.message.Message<String> msg) {
        if (handler != null) {
            try {
                handleLocalCommand(handler, msg);
            } catch (Exception e) {
                handleError(handler, e.getMessage(), msg);
            }
        }
    }

    private void handleLocalCommand(final Message<Object> handler, final com.dcall.core.configuration.app.entity.message.Message<String> sender) {
        vertx.executeBlocking(future -> {
            try {
                final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService();

                final com.dcall.core.configuration.app.entity.message.Message<String> resp = new MessageBean(HazelcastCluster.getLocalUuid(), null, 0);
                final byte[] result = builtInService.setRuntimeContext(runtimeContext).run(new String(messageService.decryptMessage(runtimeContext, sender)));

                messageService.sendEncryptedChunk(runtimeContext, vertx, uriContext.getRemoteConsumerUri(), sender, result, resp);
            }
            catch (Exception e) {
                handleError(handler, e.getMessage() != null ? e.getMessage() : e.toString(), sender);
                LOG.error(e.getMessage());
            }
            finally {
                future.complete();
            }
        }, res -> {
            if (res.succeeded()) {
                try {
                    handler.reply(uriContext.getLocalConsumerUri() + " > SUCCESS");
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
            } else {
                LOG.debug(uriContext.getLocalConsumerUri() + " > FAILURE");
                handler.reply(res.cause());
            }
        });
    }

    private void handleError(final Message<Object> handler, final String msgError, final com.dcall.core.configuration.app.entity.message.Message<String> sender) {
        try {
            final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService();
            final String error = "Failed to execute '" + new String(sender.getMessage()) + "' - ERROR : " + msgError;
            final byte[] bytes = error.getBytes();
            final com.dcall.core.configuration.app.entity.message.Message<String> resp = new MessageBean(HazelcastCluster.getLocalUuid(), bytes, bytes.length);

            LOG.error(msgError);

            handler.fail(-1, msgError);

            messageService.sendEncryptedChunk(runtimeContext, vertx, uriContext.getRemoteConsumerUri(), sender, bytes, resp);
        }
        catch (Exception e) {
            handler.fail(-1, e.getMessage());
            LOG.error(e.getMessage());
        }
    }

    private void configure() {
        configurebuiltInService();
        runtimeContext.serviceContext().serviceProvider().userServiceProvider().userService().configureSystemUser(runtimeContext);
    }

    private void configurebuiltInService() {
        this.builtInService.setRuntimeContext(this.runtimeContext).setHelp(HelpUtils.getHelpPath(HelpUtils.HELP));
        this.builtInService.setParser(new Parser(new BuiltInOperatorSolver(runtimeContext), new BuiltInOperandSolver()));
    }

    @Override
    protected void setUriContext() {
        uriContext.setBaseRemoteAppUri(uriContext.getLocalUri("terminal.vertx"));

        uriContext.setLocalConsumerUri(CommandProcessorConsumerVerticle.class.getName());
        uriContext.setRemoteConsumerUri(uriContext.getRemoteUri("InputConsumerVerticle"));
    }

    @Override
    public void start() {
        configure();
        final MessageConsumer<Object> consumer = vertx.eventBus().consumer(URIUtils.getUri(uriContext.getLocalConsumerUri(), HazelcastCluster.getLocalUuid()));

        consumer.handler(handler -> {
            final com.dcall.core.configuration.app.entity.message.Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
            LOG.info(uriContext.getLocalConsumerUri() + " > received from : " + msg.getId() + " body : " + handler.body().toString());
            execute(handler, msg);
        });
    }
}
