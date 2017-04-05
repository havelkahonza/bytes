package com.avast.bytes.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {
    private final ByteBuffer bb;

    public ByteBufferInputStream(ByteBuffer bb) {
        this.bb = bb;
    }

    @Override
    public int available() {
        return bb.remaining();
    }

    @Override
    public int read() throws IOException {
        if (!bb.hasRemaining())
            return -1;
        return bb.get() & 0xFF; // Make sure the value is in [0..255]
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        if (!bb.hasRemaining())
            return -1;
        len = Math.min(len, bb.remaining());
        bb.get(bytes, off, len);
        return len;
    }
}
