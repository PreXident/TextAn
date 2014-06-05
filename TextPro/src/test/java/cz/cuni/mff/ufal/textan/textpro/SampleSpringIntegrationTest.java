package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.textpro.configs.TextProConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Simple integration test with Spring
 * @author Petr Fanta
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TextProConfig.class}, loader = AnnotationConfigContextLoader.class)
public class SampleSpringIntegrationTest {

    @Autowired
    IAliasOccurrenceTableDAO aliasOccurrenceTableDAO;

    @Autowired
    IAliasTableDAO aliasTableDAO;

    @Autowired
    IJoinedObjectsTableDAO joinedObjectsTableDAO;

    @Autowired
    IObjectTableDAO objectTableDAO;

    @Autowired
    IObjectTypeTableDAO objectTypeTableDAO;

    @Autowired
    IRelationOccurrenceTableDAO relationOccurrenceTableDAO;

    @Autowired
    IRelationTableDAO relationTableDAO;

    @Autowired
    IRelationTypeTableDAO typeTableDAO;


    /**
     * This is really stupid test(because it is always passed), but it shows how you can get spring beans in tests.
     */
    @Test
    public void testDaoAvailability(){

        Assert.assertNotNull(aliasOccurrenceTableDAO);
        Assert.assertNotNull(aliasTableDAO);
        Assert.assertNotNull(joinedObjectsTableDAO);
        Assert.assertNotNull(objectTableDAO);
        Assert.assertNotNull(objectTypeTableDAO);
        Assert.assertNotNull(relationOccurrenceTableDAO);
        Assert.assertNotNull(relationTableDAO);
        Assert.assertNotNull(typeTableDAO);
    }
    
    
}
