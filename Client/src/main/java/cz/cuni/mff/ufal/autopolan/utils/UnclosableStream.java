package cz.cuni.mff.ufal.autopolan.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple wrapper to prevent accidantal closing of System.in.
 */
public class UnclosableStream extends InputStream {

    /** Decorated stream. */
    private final InputStream stream;

    /**
     * Only constructor.
     * @param stream InputStream to decorate
     */
    public UnclosableStream(final InputStream stream) {
        this.stream = stream;
    }

    @Override
    public int available() throws IOException {
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        //nothing
    }

    @Override
    public synchronized void mark(final int readlimit) {
        stream.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return stream.markSupported();
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return stream.read(b);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return stream.read(b, off, len);
    }

    @Override
    public synchronized void reset() throws IOException {
        stream.reset();
    }

    @Override
    public long skip(final long n) throws IOException {
        return stream.skip(n);
    }
}
