package com.dcall.core.configuration.bo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public interface Message<ID> extends Entity<ID> {
    @JsonGetter("message") byte[] getMessage();
    @JsonGetter("length") int getLength();

    @JsonSetter("message") Message setMessage(final byte[] message);
    @JsonSetter("length") Message setLength(final int length);
}
