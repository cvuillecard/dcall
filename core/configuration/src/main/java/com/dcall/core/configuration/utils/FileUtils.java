package com.dcall.core.configuration.utils;

import com.dcall.core.configuration.app.system.platform.Platform;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;

public final class FileUtils extends Platform {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    private final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private final int _BUFFER_SIZE = 8192;

    private FileUtils() {}

    private static class Holder {
        static final FileUtils INSTANCE = new FileUtils();
    }

    public static FileUtils getInstance() {
        return Holder.INSTANCE;
    }

    public byte[] readAllBytes(final InputStream inputStream) throws IOException {
        try (SeekableByteChannel sbc = new SeekableInMemoryByteChannel(IOUtils.toByteArray(inputStream));
             InputStream in = Channels.newInputStream(sbc)) {
            long size = sbc.size();
            if (size > (long) MAX_BUFFER_SIZE)
                throw new OutOfMemoryError("Required array size too large");

            return read(in, (int)size);
        }
    }

    public byte[] read(final InputStream cin, final int initialSize) throws IOException {
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

    public void createDirectory(final String path) {
        final File dir = new File(path);

        if (!dir.exists())
            dir.mkdirs();

        if (!dir.isDirectory()) {
            dir.delete();
            createDirectory(path);
        }
    }

    public void lockDelete(final String path) {
        final File file = new File(path);

        try {
            if (!file.exists())
                throw new FileNotFoundException("File not found : " + path);

            runCmd(
                    "ATTRIB +s +h +r " + path + "/s",
                    "chattr -R +i " + path,
                    "chflags -R -L uchg hidden uunlnk " + path);

        }
        catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        }
    }

    public void unlockDelete(final String path) {
        final File file = new File(path);

        try {
            if (!file.exists())
                throw new FileNotFoundException("File not found : " + path);

            runCmd(
                    "ATTRIB -s -h -r " + path + "/s",
                    "chattr -R -i " + path,
                    "chflags -R -L nouchg nohidden nouunlnk " + path);

        }
        catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        }
    }

    public void remove(final String path) {
        final File file = new File(path);

        try {
            if (!file.exists())
                throw new FileNotFoundException("File not found : " + path);

            if (file.isDirectory()) {
                Arrays.stream(file.listFiles()).forEach(f -> {
                    if (f.isDirectory())
                        remove(f.getAbsolutePath());
                    else
                        f.delete();
                });
            }

            file.delete();

            if (file.exists())
                throw new RuntimeException("Failed to delete : " + path);
        }
        catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        }
    }

    public String pwd() { return super.pwd(); }
}
