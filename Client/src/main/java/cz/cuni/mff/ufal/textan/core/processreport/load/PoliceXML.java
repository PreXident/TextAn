package cz.cuni.mff.ufal.textan.core.processreport.load;

import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Extracts reports from Police XML.
 */
public class PoliceXML implements IImporter {

    /** Importer id. */
    static private final String ID = "PoliceXML";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String extractText(byte[] data) {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final SAXParser parser = factory.newSAXParser();
            final Extractor extractor = new Extractor();
            parser.parse(new ByteArrayInputStream(data), extractor);
            return extractor.getPlainText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Simple SAX content handler for extracting content of PlainText.
     */
    protected static class Extractor extends DefaultHandler {

        /** Contetnt of PlainText. */
        final protected StringBuilder string = new StringBuilder();

        /** Indicator whether the parsing is in PlainText element. */
        protected boolean inPlainText = false;

        @Override
        public void startElement(final String uri, final String localName,
                final String qName,  final Attributes attributes)
                throws SAXException {
            if ("PlainText".equals(localName)) {
                inPlainText = true;
            }
        }

        @Override
        public void endElement(final String uri, final String localName,
                final String qName) throws SAXException {
            if ("PlainText".equals(localName)) {
                inPlainText = false;
            }
        }

        @Override
        public void characters(final char ch[], final int start,
                final int length) throws SAXException {
            if (inPlainText) {
                string.append(ch, start, length);
            }
        }

        /**
         * Returns content of the PlainText element.
         * @return content of the PlainText element
         */
        public String getPlainText() {
            return string.toString();
        }
    }
}
