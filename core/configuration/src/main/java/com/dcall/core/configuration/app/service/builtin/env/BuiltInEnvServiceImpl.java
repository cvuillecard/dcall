package com.dcall.core.configuration.app.service.builtin.env;

import com.dcall.core.configuration.generic.entity.environ.Environ;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;

import java.util.Arrays;

public class BuiltInEnvServiceImpl extends AbstractCommand implements BuiltInEnvService {
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String SEPARATOR = "=";

    @Override
    public byte[] execute(final String... params) {
        return env(params);
    }

    @Override
    public byte[] execute() {
        return getUserEnv(null);
    }

    @Override
    public byte[] getUserEnv(final String... keys) {
        final StringBuilder sb = new StringBuilder();
        final Environ environ = getContext().userContext().getEnviron();

        if (keys != null && keys.length > 0)
            Arrays.stream(keys).filter(k -> environ.getEnv().get(k) != null).forEach(k -> sb.append(entryToString(k, environ.getEnv().get(k).toString())));
        else
            environ.getEnv().keySet().forEach(k -> sb.append(entryToString(k.toString(), environ.getEnv().get(k).toString())));

        return sb.toString().getBytes();
    }

    @Override
    public byte[] setUserEnv(final String... args) {
        if (args == null || args.length == 0)
            return usageSet().getBytes();

        final StringBuilder sb = new StringBuilder();
        final Environ environ = getContext().userContext().getEnviron();

        Arrays.stream(args).forEach(v -> {
            final String[] entry = v.split(SEPARATOR);
            if (entry.length >= 2) {
                environ.getEnv().put(entry[0], entry[1]);
                sb.append(entryToString(entry[0], entry[1]));
            }
        });

//        getContext().serviceContext().serviceProvider().environService().updateUserEnviron(getContext());

        commit("User env updated");

        final String msg = sb.toString();
        return msg.length() > 0 ? msg.getBytes() : usageSet().getBytes();
    }

    @Override
    public byte[] env(final String... params) {
        final String cmd = params[0];
        final String[] args = params != null && params.length > 1 ? Arrays.copyOfRange(params, 1, params.length) : null;

        if (cmd.equals(GET))
            return getUserEnv(args);
        else if (cmd.equals(SET))
            return setUserEnv(args);

        return null;
    }

    @Override
    public String entryToString(final String k, final String v) {
        return k + ' ' + SEPARATOR + ' ' + v + '\n';
    }

    @Override
    public String usageSet() {
        return "env set <key=value>";
    }
}
