package com.dcall.core.configuration.app.service.builtin.identity;

import com.dcall.core.configuration.app.entity.identity.Identity;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class BuiltInIdentityServiceImpl extends AbstractCommand implements BuiltInIdentityService {
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String DEL = "del";
    private static final String SEPARATOR = "=";

    @Override
    public byte[] execute(final String... params) {
        return identity(params);
    }

    @Override
    public byte[] execute() {
        return getUserIdentity(null);
    }

    @Override
    public Identity getUserIdentity() {
        return getContext().userContext().getIdentity();
    }

    @Override
    public byte[] getUserIdentity(final String... keys) {
        final StringBuilder sb = new StringBuilder();
        final Properties props = getUserIdentity().getProperties();

        if (keys != null && keys.length > 0)
            Arrays.stream(keys).filter(k -> props.getProperty(k) != null).forEach(k -> sb.append(entryToString(k, props.get(k).toString())));
        else
            props.keySet().forEach(k -> sb.append(entryToString(k.toString(), props.get(k).toString())));

        return sb.toString().getBytes();
    }

    @Override
    public byte[] setUserIdentity(final String... args) {
        if (args == null || args.length == 0)
            return usageSet().getBytes();

        final Properties properties = getUserIdentity().getProperties();
        final StringBuilder sb = new StringBuilder();
        final List<String> updated = new ArrayList<>();

        Arrays.stream(args).forEach(v -> {
            final String[] entry = v.split(SEPARATOR);
            if (entry.length >= 2) {
                properties.setProperty(entry[0], entry[1]);
                sb.append(entryToString(entry[0], entry[1]));
                updated.add(entry[0]);
            }
        });

        if (updated.size() > 0) {
            getContext().serviceContext().serviceProvider().hashServiceProvider().identityService().updateUserIdentity(getUserIdentity());
            commit("Identity properties added or updated : " + updated.toString());
        }

        final String msg = sb.toString();
        return msg.length() > 0 ? msg.getBytes() : usageSet().getBytes();
    }

    byte[] delUserIdentity(final String... args) {
        if (args == null || args.length == 0)
            return usageDel().getBytes();
        final StringBuilder sb = new StringBuilder();
        final List<String> removed = new ArrayList<>();

        Arrays.stream(args)
                .filter(k -> getUserIdentity().getProperties().getProperty(k) != null)
                .peek(k -> { sb.append("'" + k + "' has been removed.\n"); removed.add(k); })
                .forEach(k -> getUserIdentity().getProperties().remove(k));

        String ret = sb.toString();

        if (ret.length() > 0) {
            getContext().serviceContext().serviceProvider().hashServiceProvider().identityService().updateUserIdentity(getUserIdentity());
            commit("Identity properties deleted : " + removed.toString());
        }
        else
            ret = "No properties found for the given keys : " + Arrays.asList(args).toString();

        return ret.getBytes();
    }

    @Override
    public byte[] identity(final String... params) {
        final String cmd = params[0];
        final String[] args = params != null && params.length > 1 ? Arrays.copyOfRange(params, 1, params.length) : null;

        switch (cmd) {
            case GET : return getUserIdentity(args);
            case SET : return setUserIdentity(args);
            case DEL : return delUserIdentity(args);
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
        return "identity set <key=value> <key2=value2> ...";
    }

    @Override
    public String usageDel() {
        return "identity del <key1> <key2> ...";
    }
}
