package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;

import java.util.List;

/**
 * A simple example of an implementation of the ITextPro interface as a Spring bean.
 * @author Petr Fanta
 * @see cz.cuni.mff.ufal.textan.textpro.ITextPro
 */
public class TextPro implements ITextPro {

    /** Provides access to AliasOccurrence table in database */
    IAliasOccurrenceTableDAO aliasOccurrenceTableDAO;

    /** Provides access to Alias table in database */
    IAliasTableDAO aliasTableDAO;

    /** Provides access to JoinedObjects table in database */
    IJoinedObjectsTableDAO joinedObjectsTableDAO;

    /** Provides access to ObjectTable table in database */
    IObjectTableDAO objectTableDAO;

    /** Provides access to ObjectType table in database */
    IObjectTypeTableDAO objectTypeTableDAO;

    /** Provides access to RelationOccurrence table in database */
    IRelationOccurrenceTableDAO relationOccurrenceTableDAO;

    /** Provides access to Relation table in database */
    IRelationTableDAO relationTableDAO;

    /** Provides access to RelationType table in database */
    IRelationTypeTableDAO typeTableDAO;


    /**
     * Instantiates a new TextPro.
     * Uses a constructor injection for an initialization of data access object ({@link cz.cuni.mff.ufal.textan.textpro.configs.TextProConfig#textPro()}
     *
     * @param aliasOccurrenceTableDAO the alias occurrence table DAO
     * @param typeTableDAO the type table DAO
     * @param aliasTableDAO the alias table DAO
     * @param joinedObjectsTableDAO the joined objects table DAO
     * @param objectTableDAO the object table DAO
     * @param objectTypeTableDAO the object type table DAO
     * @param relationOccurrenceTableDAO the relation occurrence table DAO
     * @param relationTableDAO the relation table DAO
     */
    public TextPro(
            IAliasOccurrenceTableDAO aliasOccurrenceTableDAO,
            IRelationTypeTableDAO typeTableDAO,
            IAliasTableDAO aliasTableDAO,
            IJoinedObjectsTableDAO joinedObjectsTableDAO,
            IObjectTableDAO objectTableDAO,
            IObjectTypeTableDAO objectTypeTableDAO,
            IRelationOccurrenceTableDAO relationOccurrenceTableDAO,
            IRelationTableDAO relationTableDAO) {

        this.aliasOccurrenceTableDAO = aliasOccurrenceTableDAO;
        this.typeTableDAO = typeTableDAO;
        this.aliasTableDAO = aliasTableDAO;
        this.joinedObjectsTableDAO = joinedObjectsTableDAO;
        this.objectTableDAO = objectTableDAO;
        this.objectTypeTableDAO = objectTypeTableDAO;
        this.relationOccurrenceTableDAO = relationOccurrenceTableDAO;
        this.relationTableDAO = relationTableDAO;
    }

    //TODO: implement or change interface method

    @Override
    public void init(List<String> objectlist) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Entity> recognizedEntity(String document) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Entity> SimpleRanking(String TestDir, String DataDir) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
