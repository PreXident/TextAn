package cz.cuni.mff.ufal.textan.server.linguisticsIntegration;

import cz.cuni.mff.ufal.morphodita.*;

import cz.cuni.mff.ufal.textan.server.models.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vlcak on 5.6.2014.
 */
public class MorphoditaServices {

    Tagger tagger;
    private static final Logger LOG = LoggerFactory.getLogger(MorphoditaServices.class);

    public MorphoditaServices() {
        LOG.info("Loading tagger");
        tagger = Tagger.load("models/czech-131112-pos_only.tagger");
        if (tagger == null) {
            LOG.error("Cannot load tagger from file 'czech-131112-pos_only.tagger'");
        }

    }

    public List<Tag> TagText(String input) {
        List<Tag> taggedText = new LinkedList<>();
        Forms forms = new Forms();
        TaggedLemmas lemmas = new TaggedLemmas();
        TokenRanges tokens = new TokenRanges();
        java.util.Scanner reader = new java.util.Scanner(System.in);
        Tokenizer tokenizer = tagger.newTokenizer();
        if (tokenizer == null) {
            System.err.println("No tokenizer is defined for the supplied model!");
            System.exit(1);
        }

        boolean not_eof = true;
        while (not_eof) {
            StringBuilder textBuilder = new StringBuilder();
            String line;

            // Read block
            while ((not_eof = reader.hasNextLine()) && !(line = reader.nextLine()).isEmpty()) {
                textBuilder.append(line);
                textBuilder.append('\n');
            }
            if (not_eof) textBuilder.append('\n');

            // Tokenize and tag
            String text = textBuilder.toString();
            tokenizer.setText(text);
            int t = 0;
            while (tokenizer.nextSentence(forms, tokens)) {
                tagger.tag(forms, lemmas);

                for (int i = 0; i < lemmas.size(); i++) {
                    TaggedLemma lemma = lemmas.get(i);
                    TokenRange token = tokens.get(i);
                    int token_start = (int) token.getStart(), token_end = token_start + (int) token.getLength();
                    Tag tag = new Tag(encodeEntities(text.substring(token_start, token_end)), encodeEntities(lemma.getLemma()), encodeEntities(lemma.getTag()));
                    taggedText.add(tag);
                    LOG.debug("'" + tag.getValue() +"'" + "tagged as '" + tag.getTag() + "', lemma '" + tag.getLemma() + "'");
                    t = token_end;
                }
            }
            System.out.print(encodeEntities(text.substring(t)));
        }

        return taggedText;
    }

    private static String encodeEntities(String text) {
        return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
    }
}
