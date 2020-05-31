package com.dcall.core.configuration.generic.entity.certificate;

import com.dcall.core.configuration.generic.entity.Entity;

public class CertificateBean implements Certificate<String> {
    private String id;

    @Override
    public String getId() { return this.id; }

    @Override
    public Entity<String> setId(final String id) { this.id = id; return this; }
}
