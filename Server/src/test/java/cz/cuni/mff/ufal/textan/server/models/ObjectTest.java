package cz.cuni.mff.ufal.textan.server.models;

import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ObjectTest {

    @Test
    public void testFromObjectTable() {

        List<AliasTable> aliases = new ArrayList<>();
        aliases.add(new AliasTable(null, "alias"));
        aliases.add(new AliasTable(null, "alias"));

        ObjectTypeTable objectTypeTable = new ObjectTypeTable("Type");
        ObjectTable objectTable = new ObjectTable("", objectTypeTable);

        Object object = Object.fromObjectTable(objectTable, aliases);

        Assert.assertEquals("Distinct aliases", 1, object.getAliases().size());
    }
}