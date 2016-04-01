package cz.cuni.mff.ufal.textan.data.test.common;

import cz.cuni.mff.ufal.textan.data.repositories.dao.DocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * @author Vaclav Pernicka
 */
public class Utils {

    private static final String[] TABLE_CLASSES_IN_DELETE_ORDER =
            {"AliasOccurrenceTable", "AliasTable", "InRelationTable",
                    "JoinedObjectsTable", "RelationOccurrenceTable", "RelationTable",
                    "RelationTypeTable", "ObjectTable", "ObjectTypeTable", "DocumentTable"};
    private static final String[] TABLE_CLASSES_WITH_CONTENT_IN_DELETE_ORDER =
            {"DocumentTable",
                    "RelationTypeTable",
                    "ObjectTypeTable"};
    private static final String[] TABLE_CONTENTS_IN_DELETE_ORDER =
            {DocumentTable.PROPERTY_NAME_TEXT,
                    RelationTypeTable.PROPERTY_NAME_TYPE_NAME,
                    ObjectTypeTable.PROPERTY_NAME_TYPE_NAME};
    @Autowired
    private static DocumentTableDAO documentDAO;

    public static void clearAllTables(SessionFactory factory) {
        String[] queries = addToEach(TABLE_CLASSES_IN_DELETE_ORDER, "delete from ", "");
        doMultipleExecuteQueries(factory, queries);
    }

    public static void clearTestValues(SessionFactory factory) {
        // TODO
        String[] queries = Arrays.stream(TABLE_CLASSES_WITH_CONTENT_IN_DELETE_ORDER)
                .map(t -> {
                    int index = Arrays.asList(TABLE_CLASSES_WITH_CONTENT_IN_DELETE_ORDER).indexOf(t);
                    return "delete from " + t + " as tab" +
                            " where tab." + TABLE_CONTENTS_IN_DELETE_ORDER[index] + " like '%[TEST]%'";
                }).toArray(String[]::new);


        doMultipleExecuteQueries(factory, queries);
    }

    private static void doMultipleExecuteQueries(SessionFactory factory, String[] queries) {
        Session sess = factory.openSession();
        Transaction tx = sess.beginTransaction();
        // TODO not delete DB
        for (String query : queries) {
            sess.createQuery(query).executeUpdate();
        }
        tx.commit();
        sess.close();
    }

    private static String[] addToEach(String[] arr, String prefix, String postfix) {
        final int length = arr.length;
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            result[i] = prefix + arr[i] + postfix;
        }
        return result;
    }
}
