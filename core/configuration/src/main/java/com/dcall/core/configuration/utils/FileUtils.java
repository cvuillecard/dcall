package com.dcall.core.configuration.utils;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;

public final class FileUtils {

    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private static final int _BUFFER_SIZE = 8192;

    public static byte[] readAllBytes(final InputStream inputStream) throws IOException {
        try (SeekableByteChannel sbc = new SeekableInMemoryByteChannel(IOUtils.toByteArray(inputStream));
//        try (SeekableByteChannel sbc = Files.newByteChannel(path);
             InputStream in = Channels.newInputStream(sbc)) {
            long size = sbc.size();
            if (size > (long) MAX_BUFFER_SIZE)
                throw new OutOfMemoryError("Required array size too large");

            return read(in, (int)size);
        }
    }

    public static byte[] read(final InputStream cin, final int initialSize) throws IOException {
        int capacity = initialSize;
        byte[] buf = new byte[capacity];
        int nread = 0;
        int n;
        for (;;) {
            // read to EOF which may read more or less than initialSize (eg: file
            // is truncated while we are reading)
            while ((n = cin.read(buf, nread, capacity - nread)) > 0)
                nread += n;

            // if last call to source.read() returned -1, we are done
            // otherwise, try to read one more byte; if that failed we're done too
            if (n < 0 || (n = cin.read()) < 0)
                break;

            // one more byte was read; need to allocate a larger buffer
            if (capacity <= MAX_BUFFER_SIZE - capacity) {
                capacity = Math.max(capacity << 1, _BUFFER_SIZE);
            } else {
                if (capacity == MAX_BUFFER_SIZE)
                    throw new OutOfMemoryError("Required array size too large");
                capacity = MAX_BUFFER_SIZE;
            }
            buf = Arrays.copyOf(buf, capacity);
            buf[nread++] = (byte)n;
        }
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
    }
}
