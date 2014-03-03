package cz.cuni.mff.ufal.textan.server.ws;

import cz.cuni.mff.ufal.textan.commons.models.Graph;
import cz.cuni.mff.ufal.textan.commons.models.Object;
import cz.cuni.mff.ufal.textan.commons.models.ObjectType;
import cz.cuni.mff.ufal.textan.commons.models.Relation;
import cz.cuni.mff.ufal.textan.commons.models.RelationType;
import cz.cuni.mff.ufal.textan.commons.ws.IDataProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.jws.WebService;

/**
 * For now only mocking database access.
 */
@WebService(endpointInterface = "cz.cuni.mff.ufal.textan.commons.ws.IDataProvider", serviceName = "DataProvider")
public class DataProvider implements IDataProvider {

    private final List<ObjectType> objectTypes = Arrays.asList(
            new ObjectType(0, "Person"),
            new ObjectType(1, "Gun")
    );
    private final List<Object> objects =  Arrays.asList(
            new Object(0, objectTypes.get(0), new ArrayList<>(Arrays.asList("Pepa"))),
            new Object(1, objectTypes.get(1), new ArrayList<>(Arrays.asList("flinta"))),
            new Object(2, objectTypes.get(0), new ArrayList<>(Arrays.asList("Franta")))
    );

    private final List<RelationType> relationTypes = Arrays.asList(
            new RelationType(0, "vlastnit"),
            new RelationType(0, "zabit")
    );

    private final ArrayList<Relation> relations = new ArrayList<>();

    public DataProvider() {
        Relation relation = new Relation(0, relationTypes.get(0));
        relation.getObjectInRelationIds().add(0);
        relation.getObjectInRelationIds().add(1);
        relations.add(relation);

        relation = new Relation(1, relationTypes.get(1));
        relation.getObjectInRelationIds().add(0);
        relation.getObjectInRelationIds().add(1);
        relation.getObjectInRelationIds().add(2);
        relations.add(relation);
    }

    @Override
    public ObjectType[] getObjectTypes() {
        return objectTypes.toArray(new ObjectType[objectTypes.size()]);
    }

    @Override
    public Object getObject(int objectId) {
        return objects.get(objectId);
    }

    @Override
    public Object[] getObjects() {
        return objects.toArray(new Object[objects.size()]);
    }

    @Override
    public Object[] getObjectsByType(ObjectType objectType) {
        return getObjectsByTypeId(objectType.getId());
    }

    @Override
    public Object[] getObjectsByTypeId(int objectTypeId) {
        return (Object[]) objects.stream().filter(e -> e.getType().getId() == objectTypeId).toArray();
    }

    @Override
    public RelationType[] getRelationTypes() {
        return relationTypes.toArray(new RelationType[relationTypes.size()]);
    }

    @Override
    public Relation[] getRelations() {
        return relations.toArray(new Relation[relations.size()]);
    }

    @Override
    public Relation[] getRelationsByType(RelationType relationType) {
        return (Relation[]) relations.stream().filter(e -> e.getType().equals(relationType)).toArray();
    }

    @Override
    public Graph getGraph(Object center, int distance) {
        return getGraphByID(center.getId(), distance);
    }

    @Override
    public Graph getGraphByID(int centerId, int distance) {
        return new Graph(objects, relations);
    }

    @Override
    public Graph getRelatedObjects(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Graph getRelatedObjectsByID(int objectId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Graph getPath(Object from, Object to) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Graph getPathByID(int fromId, int toId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object mergeObjects(int object1Id, int object2Id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean splitObject(int objectId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
