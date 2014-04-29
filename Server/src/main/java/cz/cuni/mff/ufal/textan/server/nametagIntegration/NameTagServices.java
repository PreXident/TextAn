package cz.cuni.mff.ufal.textan.server.nametagIntegration;

import cz.cuni.mff.ufal.nametag.*;
import cz.cuni.mff.ufal.textan.server.models.Entity;

import java.util.*;

/**
 * Created by Vlcak on 29. 4. 2014.
 */
public class NameTagServices {
    Tokenizer tokenizer;
    Ner ner;

    public NameTagServices(String model) {
        ner = Ner.load(model);
        tokenizer = ner.newTokenizer();
    }


    public List<Entity> TagText(String input)
    {
        Forms forms = new Forms();
        TokenRanges tokens = new TokenRanges();
        NamedEntities entities = new NamedEntities();
        ArrayList<NamedEntity> sortedEntities = new ArrayList<NamedEntity>();
        Scanner reader = new Scanner(input);
        List<Entity> entitiesList = new ArrayList<Entity>();
        Stack<NamedEntity> openEntities = new Stack<NamedEntity>();
        boolean not_eof = true;
        while(not_eof)

        {
            StringBuilder textBuilder = new StringBuilder();
            String line;

            // Read block
            while ((not_eof = reader.hasNextLine()) && !(line = reader.nextLine()).isEmpty()) {
                textBuilder.append(line);
                textBuilder.append('\n');
            }
            if (not_eof) textBuilder.append('\n');

            // Tokenize and recognize
            String text = textBuilder.toString();
            tokenizer.setText(text);
            int unprinted = 0;
            while (tokenizer.nextSentence(forms, tokens)) {
                ner.recognize(forms, entities);
                sortEntities(entities, sortedEntities);

                for (int i = 0, e = 0; i < entities.size(); i++) {
                    TokenRange token = tokens.get(i);
                    //int token_start = (int) token.getStart();
                    //int token_end = (int) token.getStart() + (int) token.getLength();
                    //if (unprinted < token_start) System.out.print(encodeEntities(text.substring(unprinted, token_start)));

                    for (; e < sortedEntities.size() && sortedEntities.get(e).getStart() == i; e++) {
                        String ent = sortedEntities.get(e).getType();
                        System.out.printf("<ne type=\"%s\">", ent);
                        openEntities.push(sortedEntities.get(e));
                    }
                    // pridat zjisteni id entity
                    //Entity ent = new Entity(text.substring(token_start, token_end), token_start, token_end, 0);
                    //entitiesList.add(ent);

                    while (!openEntities.empty() && (openEntities.peek().getStart() + openEntities.peek().getLength() - 1) == i) {
                        NamedEntity ending = openEntities.peek();
                        int entity_start = (int) tokens.get((int) (i - ending.getLength() + 1)).getStart();
                        int entity_end = (int) (tokens.get(i).getStart() + tokens.get(i).getLength());
                        entitiesList.add(new Entity(encodeEntities(text.substring(entity_start, entity_end)), entity_start, entity_end, 0));
                        openEntities.pop();
                    }

                        /*
                        // Close entities that end sooned than current entity
                        while (!openEntities.empty() && openEntities.peek() < entity_start) {
                            if (unprinted < openEntities.peek()) System.out.print(encodeEntities(text.substring(unprinted, openEntities.peek())));
                            unprinted = openEntities.pop();
                            System.out.print("</ne>");
                        }

                        // Print text just before the entity, open it and add end to the stack
                        if (unprinted < entity_start) System.out.print(encodeEntities(text.substring(unprinted, entity_start)));
                        unprinted = entity_start;
                        System.out.printf("<ne type=\"%s\">", entity.getType());
                        openEntities.push(entity_end);
                        */
                }
        /*
                    // Close unclosed entities
                    while (!openEntities.empty()) {
                        if (unprinted < openEntities.peek()) System.out.print(encodeEntities(text.substring(unprinted, openEntities.peek())));
                        unprinted = openEntities.pop();
                        System.out.print("</ne>");
                    }
                    */
            }
        }

        return entitiesList;
    }

    private void sortEntities(NamedEntities entities, ArrayList<NamedEntity> sortedEntities) {
        class NamedEntitiesComparator implements Comparator<NamedEntity> {
            public int compare(NamedEntity a, NamedEntity b) {
                if (a.getStart() < b.getStart()) return -1;
                if (a.getStart() > b.getStart()) return 1;
                if (a.getLength() > b.getLength()) return -1;
                if (a.getLength() < b.getLength()) return 1;
                return 0;
            }
        }
        NamedEntitiesComparator comparator = new NamedEntitiesComparator();

        sortedEntities.clear();
        for (int i = 0; i < entities.size(); i++)
            sortedEntities.add(entities.get(i));
        Collections.sort(sortedEntities, comparator);
    }

    private String encodeEntities(String text) {
        return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
    }
}
