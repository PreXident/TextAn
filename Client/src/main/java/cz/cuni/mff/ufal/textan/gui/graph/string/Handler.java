package cz.cuni.mff.ufal.textan.gui.graph.string;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles 'string' URL protocol.
 * @see http://stackoverflow.com/questions/24704515/in-javafx-8-can-i-provide-a-stylesheet-from-a-string
 */
public class Handler extends URLStreamHandler {

    /** Map of registered strings. */
    private static final Map<String, String> contents = new HashMap<>();

    /**
     * Registers content under url.
     * @param url url to register under
     * @param content string to register
     */
    public static void registerString(final String url, final String content) {
        contents.put(url, content);
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        final String path = u.getPath();
        final String content = contents.get(path);
        if (content == null) {
            throw new FileNotFoundException(String.format("No content found for \"%s\"", path));
        }
        return new StringURLConnection(u, content);
    }

    /**
     * Wraps string into URLCOnnection.
     */
    private class StringURLConnection extends URLConnection {

        /** Wrapped string. */
        private final String string;

        /**
         * Creates connection wrapping the string.
         * @param url the specified URL
         * @param string wrapped string
         */
        public StringURLConnection(final URL url, final String string){
            super(url);
            this.string = string;
        }

        @Override
        public void connect() throws IOException {
            //nothing
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
        }
    }
}
