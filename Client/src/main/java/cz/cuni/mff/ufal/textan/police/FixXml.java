package cz.cuni.mff.ufal.textan.police;

import com.beust.jcommander.Parameter;
import static cz.cuni.mff.ufal.textan.police.Policer.IO_ERROR;
import static cz.cuni.mff.ufal.textan.police.Policer.SAX_ERROR;
import static cz.cuni.mff.ufal.textan.police.Policer.XML_ERROR;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Fixes &amp; in utf-8 xml files.
 * Not very efficient nor clever...
 * All SAXParseException with "'&amp;'" in message interpreted as
 * "The entity name must immediately follow the '&amp;' in the entity reference."
 */
public class FixXml extends FilesCommand {

    /** Command's name. */
    static public final String NAME = "fix";

    /** Output directory. */
    @Parameter(
            description = "output directory",
            required = true,
            names = { "-o", "/O", "--output-dir" })
    public String outputDir = null;

    @Override
    public int executeCommand(Options options) throws Exception {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final SAXParser parser = factory.newSAXParser();
            final DefaultHandler extractor = new DefaultHandler();
            final Path outputPath = Paths.get(outputDir);
            Files.createDirectories(outputPath);
            reportsIterator.accept(p -> {
                boolean ok = false;
                boolean error = false;
                try (BufferedReader fileReader = Files.newBufferedReader(p)) {
                    InputSource inputSource = new InputSource(fileReader);
                    String fixed = "";
                    while (!ok) {
                        try {
                            parser.parse(inputSource, extractor);
                            ok = true;
                        } catch (SAXParseException e) {
                            if (!e.getMessage().contains("'&'")) {
                                throw e;
                            }
                            error = true;
                            if (fixed.isEmpty()) { //first fixing, read input to string
                                fixed = new String(Files.readAllBytes(p));
                            }
                            fixed = fixXml(fixed, e.getLineNumber(), e.getColumnNumber() - 1);
                            inputSource = new InputSource(new StringReader(fixed));
                        }
                    }
                    if (error) {
                        final Path output = outputPath.resolve(p.getFileName());
                        Files.write(output, fixed.getBytes());
                    }
                }
            });
            return 0;
        } catch (SAXException e) {
            e.printStackTrace();
            return XML_ERROR;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return SAX_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            return IO_ERROR;
        }
    }

    /**
     * Relaces '&amp;' on given position to '&amp;amp;'.
     * @param string input xml
     * @param errLineNumber error line
     * @param errColumnNumber error column
     * @return
     * @throws IOException if any IO error occurs
     */
    protected String fixXml(final String string, final int errLineNumber,
            final int errColumnNumber) throws IOException {
        final String newLine = System.getProperty("line.separator");
        try (BufferedReader reader = new BufferedReader(new StringReader(string))) {
            StringBuilder builder = new StringBuilder(string.length() + 4);
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                ++lineNumber;
                if (lineNumber != errLineNumber) {
                    builder.append(line);
                    builder.append(newLine);
                } else {
                    builder.append(line.substring(0, errColumnNumber));
                    builder.append("amp;");
                    builder.append(line.substring(errColumnNumber));
                    builder.append(newLine);
                }
            }
            return builder.toString();
        }
    }
}
