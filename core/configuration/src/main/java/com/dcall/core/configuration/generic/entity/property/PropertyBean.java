package com.dcall.core.configuration.generic.entity.property;

import com.dcall.core.configuration.generic.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.generic.entity.cipher.CipherAES;

import java.util.Properties;

public class PropertyBean extends AbstractCipherResource<String> implements Property {
    private Properties properties;

    public PropertyBean() { super(); }

    public PropertyBean(final String path, final CipherAES<String> cipherAES) { super(path, cipherAES); }

    public PropertyBean(final String path, final CipherAES<String> cipherAES, final Properties properties) {
        super(path, cipherAES);
        this.properties = properties;
    }

    public PropertyBean(final String id, final String path, final CipherAES<String> cipherAES, final Properties properties) {
        super(id, path, cipherAES);
        this.properties = properties;
    }

    // getter
    @Override public Properties getProperties() { return this.properties; }
    // setter
    @Override public Property setProperties(final Properties properties) { this.properties = properties; return this; }
}
