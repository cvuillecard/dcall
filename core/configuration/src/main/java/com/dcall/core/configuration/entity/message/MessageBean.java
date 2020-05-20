package com.dcall.core.configuration.entity.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageBean implements Message<String> {
    private String id;
    private byte[] message;
    private int length;

    public MessageBean() {}

//    @JsonCreator
    public MessageBean(@JsonProperty final String id, @JsonProperty final byte[] message, @JsonProperty final int length) {
        this.id = id;
        this.message = message;
        this.length = length;
    }

    // GETTERS
    @Override public String getId() { return this.id; }
    @Override public byte[] getMessage() { return message; }
    @Override public int getLength() { return length; }

    // SETTERS
    @Override public Message<String> setId(final String id) { this.id = id; return this; }
    @Override public Message<String> setMessage(final byte[] message) { this.message = message; return this; }
    @Override public Message<String> setLength(final int length) { this.length = length; return this; }
}
