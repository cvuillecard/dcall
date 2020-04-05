package com.dcall.core.configuration.service;

public interface GenericCommandService {
    byte[] usage();
    byte[] execute(final String... params);
}
