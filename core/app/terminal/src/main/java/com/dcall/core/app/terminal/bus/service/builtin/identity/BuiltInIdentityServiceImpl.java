package com.dcall.core.app.terminal.bus.service.builtin.identity;

import com.dcall.core.configuration.generic.entity.identity.Identity;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;

import java.util.Arrays;
import java.util.Properties;

public class BuiltInIdentityServiceImpl extends AbstractCommand implements BuiltInIdentityService {
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String SEPARATOR = "=";

    @Override
    public byte[] execute(final String... params) {
        return identity(params);
    }

    @Override
    public byte[] execute() {
        return getUserProperties();
    }

    @Override
    public Identity getUserIdentity() {
        return getContext().userContext().getIdentity();
    }

    @Override
    public byte[] getUserProperties(final String... keys) {
        final StringBuilder sb = new StringBuilder();
        final Properties props = getUserIdentity().getProperties();

        if (keys != null && keys.length > 0)
            Arrays.stream(keys).filter(k -> props.getProperty(k) != null).forEach(k -> sb.append(entryToString(k, props.get(k).toString())));
        else
            props.keySet().forEach(k -> sb.append(entryToString(k.toString(), props.get(k).toString())));

        return sb.toString().getBytes();
    }

    @Override
    public byte[] setUserProperties(final String... args) {
        if (args == null || args.length == 0)
            return usageSet().getBytes();

        final Identity identity = getUserIdentity();
        final Properties properties = identity.getProperties();
        final StringBuilder sb = new StringBuilder();

        Arrays.stream(args).forEach(v -> {
            final String[] entry = v.split(SEPARATOR);
            if (entry.length >= 2) {
                properties.setProperty(entry[0], entry[1]);
                sb.append(entryToString(entry[0], entry[1]));
            }
        });

        getContext().serviceContext().serviceProvider().hashServiceProvider().identityService().updateUserIdentity(identity);

        final String msg = sb.toString();
        return msg.length() > 0 ? msg.getBytes() : usageSet().getBytes();
    }

    @Override
    public byte[] identity(final String... params) {
        final String cmd = params[0];
        final String[] args = params != null && params.length > 1 ? Arrays.copyOfRange(params, 1, params.length) : null;

        if (cmd.equals(GET))
            return getUserProperties(args);
        else if (cmd.equals(SET))
            return setUserProperties(args);

        return null;
    }

    @Override
    public String entryToString(final String k, final String v) {
        return k + ' ' + SEPARATOR + ' ' + v + '\n';
    }

    @Override
    public String usageSet() {
        return "identity set <key=value>";
    }
}
