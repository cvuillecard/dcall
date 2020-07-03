package com.dcall.core.configuration.app.service.builtin.env;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.entity.environ.Environ;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuiltInEnvServiceImpl extends AbstractCommand implements BuiltInEnvService {
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String DEL = "del";
    private static final String SEPARATOR = "=";
    private static final List<String> lockProperties = Arrays.asList(
            EnvironConstant.USER_HOME, EnvironConstant.USER_WORKSPACE,
            EnvironConstant.USER_CONF, EnvironConstant.USER_IDENTITY_PROP,
            EnvironConstant.USER_CERT, EnvironConstant.COMMIT_MODE,
            EnvironConstant.INTERPRET_MODE, EnvironConstant.PUBLIC_ID
    );

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
            Arrays.stream(keys).filter(k -> environ.getProperties().get(k) != null).forEach(k -> sb.append(entryToString(k, environ.getProperties().get(k).toString())));
        else
            environ.getProperties().keySet().forEach(k -> sb.append(entryToString(k.toString(), environ.getProperties().get(k).toString())));

        return sb.toString().getBytes();
    }

    @Override
    public byte[] setUserEnv(final String... args) {
        if (args == null || args.length == 0)
            return usageSet().getBytes();

        final Environ environ = getContext().userContext().getEnviron();
        final StringBuilder sb = new StringBuilder();
        final List<String> updated = new ArrayList<>();

        Arrays.stream(args).forEach(v -> {
            final String[] entry = v.split(SEPARATOR);
            if (entry.length >= 2) {
                environ.getProperties().put(entry[0], entry[1]);
                sb.append(entryToString(entry[0], entry[1]));
                updated.add(entry[0]);
            }
        });

        if (updated.size() > 0) {
            getContext().serviceContext().serviceProvider().environService().updateEnviron(environ);
            commit("User env properties added or updated : " + updated.toString());
        }

        final String msg = sb.toString();
        return msg.length() > 0 ? msg.getBytes() : usageSet().getBytes();
    }

    byte[] delUserEnv(final String... args) {
        if (args == null || args.length == 0)
            return usageDel().getBytes();

        final Environ environ = getContext().userContext().getEnviron();
        final StringBuilder sb = new StringBuilder();
        final List<String> removed = new ArrayList<>();

        Arrays.stream(args)
                .filter(k -> environ.getProperties().getProperty(k) != null && !lockProperties.contains(k))
                .peek(k -> { sb.append("'" + k + "' has been removed.\n"); removed.add(k); } )
                .forEach(k -> environ.getProperties().remove(k));

        String ret = sb.toString();

        if (ret.length() > 0) {
            getContext().serviceContext().serviceProvider().environService().updateEnviron(environ);
            commit("User env properties deleted : " + removed.toString());
        }
        else
            ret = "No properties found for the given keys : " + Arrays.asList(args).toString();

        return ret.getBytes();
    }

    @Override
    public byte[] env(final String... params) {
        final String cmd = params[0];
        final String[] args = params != null && params.length > 1 ? Arrays.copyOfRange(params, 1, params.length) : null;

        switch (cmd) {
            case GET : return getUserEnv(args);
            case SET : return setUserEnv(args);
            case DEL : return delUserEnv(args);
            default : break;
        }

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

    @Override
    public String usageDel() {
        return "env del <key1> <key2> ...";
    }
}
