package com.avast.bytes.jdk;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FileBackedBytesTest {

    private final Path testFile = Paths.get(getClass().getClassLoader().getResource("loremipsum.txt").toURI());

    public FileBackedBytesTest() throws URISyntaxException {
    }

    @Test
    public void testReadsAllBytes() throws IOException {
        final byte[] allBytes = Files.readAllBytes(testFile);

        final FileBackedBytes fileBackedBytes = FileBackedBytes.from(testFile);

        assertArrayEquals(allBytes, fileBackedBytes.toByteArray());
        assertArrayEquals(allBytes, fileBackedBytes.toByteArray()); // repeated intentionally

        final byte[] isBytes = IOUtils.readFully(fileBackedBytes.newInputStream(), allBytes.length);

        assertArrayEquals(allBytes, isBytes);
    }
    @Test
    public void testGetSize() throws IOException {
        final byte[] allBytes = Files.readAllBytes(testFile);

        final FileBackedBytes fileBackedBytes = FileBackedBytes.from(testFile);

        assertEquals(allBytes.length, fileBackedBytes.size());

        assertArrayEquals(allBytes, fileBackedBytes.toByteArray());
        assertEquals(allBytes.length, fileBackedBytes.size());
    }

    @Test
    public void testRepeatedSlicing() throws IOException {
        final byte[] allBytes = Files.readAllBytes(testFile);

        final Random random = new Random();
        for (int i = 0; i <= 100; i++) {
            int startIndex = random.nextInt(5000);
            int endIndex = random.nextInt(5000) + 5000;

            final byte[] rangeBytes = Arrays.copyOfRange(allBytes, startIndex, endIndex);

            final FileBackedBytes fileBackedBytes = FileBackedBytes.from(testFile).view(startIndex, endIndex);

            assertArrayEquals(rangeBytes, fileBackedBytes.toByteArray());
        }
    }
}
