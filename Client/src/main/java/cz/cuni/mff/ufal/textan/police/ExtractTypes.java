package cz.cuni.mff.ufal.textan.police;

import static cz.cuni.mff.ufal.textan.police.Policer.IO_ERROR;
import static cz.cuni.mff.ufal.textan.police.Policer.SAX_ERROR;
import static cz.cuni.mff.ufal.textan.police.Policer.XML_ERROR;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Extract types from given xml files.
 * Expects xml input, prints attribute "type" from elements "Entity"
 */
public class ExtractTypes extends ReportsCommand {

    /** Command's name. */
    static public final String NAME = "extract";

    @Override
    public int executeCommand(final Options options) throws Exception {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final SAXParser parser = factory.newSAXParser();
            final TypeExtractor extractor = new TypeExtractor();
            reportsIterator.accept(is -> parser.parse(is, extractor));
            for (String type : extractor.getTypes()) {
                System.out.println(type);
            }
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
     * Finds attributes "type" of elements "Entity".
     */
    protected static class TypeExtractor extends DefaultHandler {

        /** Types extracted from Entities. */
        final protected Set<String> types = new HashSet<>();

        @Override
        public void startElement(final String uri, final String localName,
                final String qName,  final Attributes attributes)
                throws SAXException {
            if ("Entity".equals(localName)) {
                types.add(attributes.getValue("type"));
            }
        }

        /**
         * Returns set of extracted types.
         * @return set of extracted types
         */
        public Set<String> getTypes() {
            return types;
        }
    }
}
