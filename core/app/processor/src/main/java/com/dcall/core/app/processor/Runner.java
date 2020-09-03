package com.dcall.core.app.processor;

import com.dcall.core.app.processor.vertx.command.CommandProcessorConsumerVerticle;
import com.dcall.core.configuration.app.runner.RunnerConfigurator;
import com.dcall.core.configuration.app.verticle.filetransfer.FileTransferConsumerVerticle;
import com.dcall.core.configuration.app.verticle.fingerprint.FingerPrintConsumerVerticle;
import com.dcall.core.configuration.app.verticle.task.TaskExecutorVerticle;
import com.dcall.core.configuration.generic.cluster.vertx.VertxApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class Runner {
    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

    public static void main(final String[] args) {
        final RunnerConfigurator configurator = new RunnerConfigurator(Runner.class.getPackage().getName())
                .defaultValidateArgs(args)
                .parseOptions(args);

        VertxApplication.init(configurator.getHost(), configurator.getPort(), configurator.getGroupName(), configurator.getGroupPassword());
        VertxApplication.startOnCluster(
                true,
                configurator.getPeers().toArray(new String[configurator.getPeers().size()]),
                FingerPrintConsumerVerticle.class,
                FileTransferConsumerVerticle.class,
                CommandProcessorConsumerVerticle.class,
                TaskExecutorVerticle.class
        );
    }
}
