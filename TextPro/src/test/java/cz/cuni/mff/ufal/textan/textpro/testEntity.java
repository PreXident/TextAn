package cz.cuni.mff.ufal.textan.textpro;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.textpro.ITextPro;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import cz.cuni.mff.ufal.textan.textpro.data.EntityInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

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
    }
}
