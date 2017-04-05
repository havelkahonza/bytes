package com.avast.bytes.jdk;

import com.avast.bytes.AbstractBytes;
import com.avast.bytes.Bytes;
import com.avast.bytes.internal.ByteBufferInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Implementation of {@link Bytes} backed by memory mapped file  in format of {@link ByteBuffer}.
 * <p>
 * You create a new instance either calling {@link #from(Path)}.
 * <p>
 * The implementation uses {@link MappedByteBuffer} for accessing the data.
 */
public final class FileBackedBytes extends AbstractBytes {

    private final ByteBuffer buffer;

    private FileBackedBytes(final ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int size() {
        return buffer.remaining();
    }

    @Override
    public byte byteAt(int index) {
        return buffer.get(buffer.position() + index);
    }

    @Override
    public byte[] toByteArray() {
        byte[] dest = new byte[size()];
        // create new read-only view so that we don't have to synchronize modifying the buffer's position
        toReadOnlyByteBuffer().get(dest);
        return dest;
    }

    @Override
    public ByteBuffer toReadOnlyByteBuffer() {
        return buffer.asReadOnlyBuffer();
    }

    @Override
    public String toString(Charset charset) {
        return getClass().getName() + "{ size = " + size() + " }";
    }

    @Override
    public InputStream newInputStream() {
        return new ByteBufferInputStream(toReadOnlyByteBuffer());
    }

    @Override
    public FileBackedBytes view(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(beginIndex));
        }
        if (endIndex > size()) {
            throw new IndexOutOfBoundsException(String.valueOf(endIndex));
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(subLen));
        }

        ByteBuffer slice = buffer.slice();
        int oldPos = slice.position();
        slice.position(oldPos + beginIndex);
        slice.limit(oldPos + endIndex);

        return new FileBackedBytes(slice);
    }

    public static FileBackedBytes from(Path file) throws IOException {
        final FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ);
        final MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, Files.size(file));
        fileChannel.close();

        return new FileBackedBytes(buffer);
    }
}
