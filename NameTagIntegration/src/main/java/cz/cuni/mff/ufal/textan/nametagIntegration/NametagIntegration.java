package cz.cuni.mff.ufal.textan.nametagIntegration;

import cz.cuni.mff.ufal.nametag.*;
import cz.cuni.mff.ufal.textan.server.models.Document;
import cz.cuni.mff.ufal.textan.server.models.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * Created by Vlcak on 29.3.14.
 */
public class NameTagIntegration {
    private static String encodeEntities(String text) {
        return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
    }

    public List<Entity> ProcessDocument(Document document) {
        return ProcessDocument(document.getText());
    }

    public List<Entity> ProcessDocument(String input) {
        Forms forms = new Forms();
        TokenRanges tokens = new TokenRanges();
        NamedEntities entities = new NamedEntities();
        Scanner reader = new Scanner(input);
        Stack<Integer> openEntities = new Stack<Integer>();
        Ner ner = Ner.load("models/czech-cnec2.0-140304.ner");
        Tokenizer tokenizer = ner.newTokenizer();
        List<Entity> entitiesList = new ArrayList<Entity>();
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

            // Tokenize and recognize
            String text = textBuilder.toString();
            tokenizer.setText(text);
            int unprinted = 0;
            while (tokenizer.nextSentence(forms, tokens)) {
                ner.recognize(forms, entities);

                for (int i = 0; i < entities.size(); i++) {
                    NamedEntity entity = entities.get(i);
                    int entity_start = (int) tokens.get((int) entity.getStart()).getStart();
                    int entity_end = (int) (tokens.get((int) (entity.getStart() + entity.getLength() - 1)).getStart() + tokens.get((int) (entity.getStart() + entity.getLength() - 1)).getLength());
                    Entity e = new Entity("",entity_start,entity_end - entity_start,0);
                    /*
                    e.setPosition(entity_start);
                    e.setLength(entity_end - entity_start);
                    e.setValue(""); */
                    entitiesList.add(e);

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
            // Write rest of the text (should be just spaces)
            if (unprinted < text.length()) System.out.print(encodeEntities(text.substring(unprinted)));
        }
        return entitiesList;
    }
}
