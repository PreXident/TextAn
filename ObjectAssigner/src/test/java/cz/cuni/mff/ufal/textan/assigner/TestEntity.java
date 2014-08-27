package cz.cuni.mff.ufal.textan.assigner;

import cz.cuni.mff.ufal.textan.assigner.data.Entity;
import cz.cuni.mff.ufal.textan.assigner.data.EntityInfo;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Simple test testing nothing???
 * TODO fix!
 * @author Tamhd
 */
public class TestEntity {

    @Test
    public void TestEntityInfo() {
        Entity e = new Entity("Emily", 1);
        List<ObjectTable> objects = new ArrayList<>();
        EntityInfo eInfo = new EntityInfo(e, Collections.emptyList());
        assertEquals("1 entity to match", 2, 1+1); // TODO: test the entity info
    }
}
