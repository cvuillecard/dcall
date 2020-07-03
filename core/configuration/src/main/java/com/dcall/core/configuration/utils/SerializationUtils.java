package com.dcall.core.configuration.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public final class SerializationUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SerializationUtils.class);

    public static final <T> byte[] serialize(final T object) {
        try {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(os);

            out.writeObject(object);

            byte[] bytes = os.toByteArray();

            os.close();
            out.close();

            return bytes;
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static final <T> void serialize(final T object, final String filePath) {
        try {
            final OutputStream os = new FileOutputStream(filePath);
            final ObjectOutput out = new ObjectOutputStream(os);

            out.writeObject(object);

            os.close();
            out.close();

        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    public static final <T> T deserialize(final byte[] bytes) {
        try {
            final InputStream is = new ByteArrayInputStream(bytes);
            final ObjectInputStream in = new ObjectInputStream(is);

            final T obj = (T) in.readObject();

            is.close();
            in.close();

            return obj;
        } catch (IOException | ClassNotFoundException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static final <T> T deserialize(final String filePath) {
        try {
            final InputStream is = new FileInputStream(filePath);
            final ObjectInputStream in = new ObjectInputStream(is);

            final T obj = (T) in.readObject();

            is.close();
            in.close();

            return obj;
        } catch (IOException | ClassNotFoundException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }
}
