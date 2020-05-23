package com.dcall.core.configuration.app.service;

public interface GenericCommandService {
    byte[] usage();
    byte[] execute(final String... params);
}
