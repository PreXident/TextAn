package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import cz.cuni.mff.ufal.textan.textpro.data.EntityInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Tamhd
 */
public class TestEntity {
    //TODO: in this directory must be only JUnit or NGUnit tests!
    @Test
    public void TestEntityInfo() {
        Entity e = new Entity("Emily", 0, 0, 1);
        List<ObjectTable> objects = new ArrayList<ObjectTable>();
        EntityInfo eInfo = new EntityInfo(e,null,null,null);  
        assertEquals("1 entity to match", 2, 1+1); // TODO: test the entity info
    }
}
