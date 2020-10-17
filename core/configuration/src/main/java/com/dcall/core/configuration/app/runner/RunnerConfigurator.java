package com.dcall.core.configuration.app.runner;

import com.dcall.core.configuration.app.constant.ClusterConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public final class RunnerConfigurator {
    private static final Logger LOG = LoggerFactory.getLogger(RunnerConfigurator.class);

    private final int HOST = 0;
    private final int PORT = 1;
    private final int GROUP = 2;
    private final int PEERS = 3;
    private final int DEFAULT_MIN_AC = 6;

    private String name;
    private String host;
    private Integer port;
    private String groupName;
    private String groupPassword;
    private Set<String> peers = new HashSet<>();

    public RunnerConfigurator(final String name) {
        this.name = name;
    }

    private final String[] DEFAULT_OPTIONS = new String[] {
      "-host", "-port", "-group", "-peers"
    };

    public RunnerConfigurator defaultValidateArgs(final String[] argv) {
        if (argv.length < DEFAULT_MIN_AC)
            throw new IllegalArgumentException(usage());

        return this;
    }

    public RunnerConfigurator validateArgs(final Predicate<Integer> cond, final String[] args) {
        if (!cond.test(args.length))
            throw new IllegalArgumentException(usage());

        return this;
    }

    public RunnerConfigurator parseOptions(final String args[]) {
        boolean parse = true;

        for(int i = 0; parse && i < args.length; i++) {
            if (parse = parseHost(i, args) || parsePort(i, args) || parseGroup(i, args) || parsePeers(i, args))
                i++;
        }

        return this;
    }

    private boolean parseHost(final int idx, final String[] args) {
        if (args[idx].equalsIgnoreCase(DEFAULT_OPTIONS[HOST])) {
            checkNextArgument(idx, args, HOST, "option requires to be followed by the host adress");
            this.host = args[idx + 1];

            return true;
        }
        return false;
    }

    private boolean parsePort(final int idx, final String[] args) {
        if (args[idx].equalsIgnoreCase(DEFAULT_OPTIONS[PORT])) {
            checkNextArgument(idx, args, PORT, "option requires to be followed by an available port");
            this.port = Integer.valueOf(args[idx + 1]);

            return true;
        }
        return false;
    }

    private boolean parseGroup(final int idx, final String[] args) {
        if (args[idx].equalsIgnoreCase(DEFAULT_OPTIONS[GROUP])) {
            final String[] group = args[idx + 1].split(":");

            this.groupName = group[0];
            this.groupPassword = group[1];

            return true;
        }
        return false;
    }

    private boolean parsePeers(final int idx, final String[] args) {
        if (args[idx].equalsIgnoreCase(DEFAULT_OPTIONS[PEERS])) {
            checkNextArgument(idx, args, PEERS, "options requires at least one peer argument (ex : -peers <host:port>)");

            peers.addAll(Arrays.asList(args).subList(idx + 1, args.length));

            return false;
        }
        return true;
    }

    private void checkNextArgument(final int idx, final String[] args, final int option, final String msgError) {
        if (idx + 1 == args.length)
            throw new IllegalArgumentException(DEFAULT_OPTIONS[option] + " " + msgError.trim());
    }

    protected String usage() {
        return "[" + this.name + "] <options> " + "[ -host <address> -port <number> -group <groupName:groupPassword> -peers <host1:port1> <host2:port2> <host3:port3> <...> ]";
    }

    // GETTERS
    public String getHost() { return this.host; }
    public Integer getPort() { return this.port; }
    public Set<String> getPeers() { return this.peers; }
    public String getGroupName() { return this.groupName == null ? ClusterConstant.GROUP_GATE : this.groupName; }
    @Deprecated // going to be deleted because not used by hazelcast
    public String getGroupPassword() { return this.groupPassword == null ? ClusterConstant.PUB_PWD : this.groupPassword; }
}
