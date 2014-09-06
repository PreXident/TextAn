package cz.cuni.mff.ufal.textan.police;

import com.beust.jcommander.Parameter;
import cz.cuni.mff.ufal.textan.core.processreport.load.PoliceXML;
import static cz.cuni.mff.ufal.textan.police.Policer.IO_ERROR;
import static cz.cuni.mff.ufal.textan.police.Policer.SAX_ERROR;
import static cz.cuni.mff.ufal.textan.police.Policer.XML_ERROR;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Prepares training data from given xml files.
 */
public class PrepareTraining extends ReportsCommand {

    /** Whitespace characters. Not in output. */
    static final Set<Character> whitespaces = new HashSet<>(Arrays.asList(' ', '\t', '\r', '\n', '\f'));

    /** Characters on separate lines. */
    static final Set<Character> loners = new HashSet<>(Arrays.asList('[', ']', '{', '}', '(', ')', '.', ',', ';', ':', '!', '?', '"', '\''));

    /** Strings marking possible end of sentence. */
    static final Set<String> sentenceEndStrings = new HashSet<>(Arrays.asList(".", "?", "!"));

    /** String representation of whitespaces. */
    static final Set<String> whitespaceStrings;

    /** String representation of loners. */
    static final Set<String> lonerStrings;

    static {
        whitespaceStrings = whitespaces.stream()
                .map(c -> Character.toString(c))
                .collect(Collectors.toCollection(HashSet::new));
        whitespaceStrings.add("");
        lonerStrings = loners.stream()
                .map(c -> Character.toString(c))
                .collect(Collectors.toCollection(HashSet::new));
    }

    /** Command's name. */
    static public final String NAME = "training";

    /** Id of the person type. */
    @Parameter(
            description = "id of the person type",
            required = true,
            names = { "-p", "/P", "--person-id" })
    public String personId;

    @Override
    public int executeCommand(Options options) throws Exception {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final SAXParser parser = factory.newSAXParser();
            final DataTrainerExtractor extractor = new DataTrainerExtractor();
            reportsIterator.accept(is -> parser.parse(is, extractor));
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

    //rozrezavat na vety - tecka bily znak, velka pismenko a zbytek nevelky? ale stejne problem s vysazenymi jmeny
    //brat jen osoby, jmeno do ',nar.', matchovat jednotliva slova zpravy a jmeno
    //
    /**
     * Creates training data from xml.
     * Tries to find persons' names in the text by simple methods.
     */
    protected class DataTrainerExtractor extends PoliceXML.Extractor {

        /** Set of names in the document. */
        final protected Set<String> names = new HashSet<>();

        /** Indicator whether the parsing is in Person element. */
        protected boolean inPerson = false;

        /** Name of person being currently parsed. */
        protected StringBuilder personName = new StringBuilder();

        @Override
        public void startDocument() {
            super.startDocument();
            inPerson = false;
            names.clear();
        }

        /**
         * Splits text into tokens.
         * Characters in whitespaces and loners are separate tokens.
         * @param text text to tokenize
         * @return list of tokens
         */
        protected List<String> splitTokens(final String text) {
            final List<String> result = new ArrayList<>();
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length(); ++i) {
                final char ch = text.charAt(i);
                if (whitespaces.contains(ch) || loners.contains(ch)) {
                    if (builder.length() > 0) {
                        result.add(builder.toString());
                        builder.setLength(0);
                    }
                    result.add(Character.toString(ch));
                } else {
                    builder.append(ch);
                }
            }
            return result;
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            String previousPerson = null; //previous person, for I/B decision
            //create name part -> name mapping
            final Map<String, String> persons = new HashMap<>();
            for (String name : names) {
                final String[] parts = name.split(" ");
                for (String p : parts) {
                    persons.put(p.toUpperCase(), name);
                }
            }
            //create tokens
            final List<String> tokens = splitTokens(getPlainText().trim());
            //iterate tokens
            for (int i = 0; i < tokens.size(); ++i) {
                final String token = tokens.get(i);
                if (whitespaceStrings.contains(token)) {
                    continue; //ignore whitespaces
                }
                if (lonerStrings.contains(token)) { //loners are not names
                    System.out.println(token + "\t_");
                    previousPerson = null;
                    if (sentenceEndStrings.contains(token)
                            && (i + 1 >= tokens.size()
                                || Character.isUpperCase(tokens.get(i + 1).charAt(0)))) {
                        System.out.println(); //empty line to indicate sentence end
                    }
                } else {
                    final String upper = token.toUpperCase();
                    //find name containing the token
                    final Optional<String> match = persons.entrySet().stream()
                            .filter(e -> e.getKey().equals(upper))
                            .map(Entry::getValue)
                            .findFirst();
                    if (match.isPresent()) {
                        final String person = match.get();
                        //decide I/B
                        final String type = previousPerson == null || person.equals(previousPerson)
                                ? "I"
                                : "B";
                        System.out.println(token + "\t" + type + "-" + personId);
                        previousPerson = person;
                    } else {
                        System.out.println(token + "\t_"); //no person found
                    }
                }
            }
        }

        @Override
        public void startElement(final String uri, final String localName,
                final String qName,  final Attributes attributes)
                throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Entity".equals(localName) && "Person".equals(attributes.getValue("type"))) {
                inPerson = true;
                personName.setLength(0);
            }
        }

        @Override
        public void endElement(final String uri, final String localName,
                final String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("Entity".equals(localName)) {
                inPerson = false;
                final String name = personName.toString();
                final int index = name.indexOf(',');
                if (index != -1) {
                    names.add(name.substring(0, index).toUpperCase());
                } else {
                    names.add(name.toUpperCase());
                }
            }
        }

        @Override
        public void characters(final char ch[], final int start,
                final int length) throws SAXException {
            super.characters(ch, start, length);
            if (inPerson) {
                personName.append(ch, start, length);
            }
        }
    }
}
