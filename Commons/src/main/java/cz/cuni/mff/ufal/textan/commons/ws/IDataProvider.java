package cz.cuni.mff.ufal.textan.commons.ws;

import cz.cuni.mff.ufal.textan.commons.models.*;
import cz.cuni.mff.ufal.textan.commons.models.Object;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * The interface for Web Service, which provides getting information from the system.
 * Created by Petr Fanta on 7.1.14.
 */
@WebService(name = "DataProvider")
public interface IDataProvider {

    /**
     * Returns all types of object which are currently defined in the system.
     * @return an array of {@link cz.cuni.mff.ufal.textan.commons.models.ObjectType}
     */
    @WebMethod
    public ObjectType[] getObjectTypes();

    /**
     * Returns object with given id.
     * @param objectId the id of object to getting information about the object
     * @return information about the object ({@link cz.cuni.mff.ufal.textan.commons.models.Object}) if the id is found, otherwise return null
     */
    @WebMethod
    public Object getObject(@WebParam(name = "objectId") int objectId);

    /**
     * Returns all objects which are currently defined in the system.
     * @return an array of {@link cz.cuni.mff.ufal.textan.commons.models.Object}
     */
    @WebMethod
    public Object[] getObjects();

    /**
     * Returns all objects of given type which are currently defined in the system.
     * @param objectType type of objects
     * @return an array of {@link cz.cuni.mff.ufal.textan.commons.models.Object} of given type
     */
    @WebMethod
    public Object[] getObjectsByType(@WebParam(name = "objectType") ObjectType objectType);

    /**
     * Returns all objects of given type which are currently defined in the system.
     * @param objectTypeId object type id
     * @return an array of {@link cz.cuni.mff.ufal.textan.commons.models.Object} of given type
     */
    @WebMethod
    public Object[] getObjectsByTypeId(@WebParam(name = "objectTypeId") int objectTypeId);

    /**
     * Returns all types of relation which are currently defined in the system.
     * @return an array of {@link cz.cuni.mff.ufal.textan.commons.models.RelationType}
     */
    @WebMethod
    public RelationType[] getRelationTypes();

    /**
     * Returns all relations which are currently defined in the system.
     * @return an array of {@link cz.cuni.mff.ufal.textan.commons.models.Relation}
     */
    @WebMethod
    public Relation[] getRelations();

    /**
     * Returns all relations of given type which are currently defined in the system.
     * @param relationType type of relations
     * @return an array of {@link cz.cuni.mff.ufal.textan.commons.models.Relation} of given type
     */
    @WebMethod
    public Relation[] getRelationsByType(@WebParam(name = "relationType") RelationType relationType);

    /**
     * Builds graph from objects and relations between them.
     * (It's recommended to use overloaded method which use object's ID)
     * @param center the object which will be used as a center node of graph
     * @param distance maximum length of a path from the center node to each other node in a graph
     * @return {@link cz.cuni.mff.ufal.textan.commons.models.Graph}
     */
    @WebMethod(operationName = "getGraphUsingObject") //operation name is only fix for method overloading in web services
    public Graph getGraph(@WebParam(name = "center") Object center, int distance);

    /**
     * Builds graph from objects and relations between them.
     * @param centerId id of the object which will be used as a center node of graph
     * @param distance maximum length of a path from the center node to each other node in a graph
     * @return {@link cz.cuni.mff.ufal.textan.commons.models.Graph}
     */
    @WebMethod
    public Graph getGraphByID(@WebParam(name = "center") int centerId, int distance);

    /**
     * Returns objects related with a given object (as a graph).
     * (It's recommended to use overloaded method which use object's ID)
     * @param object
     * @return {@link cz.cuni.mff.ufal.textan.commons.models.Graph}
     */
    @WebMethod(operationName = "getRelatedObjectsUsingObject") //operation name is only fix for method overloading in web services
    public Graph getRelatedObjects(@WebParam(name = "object") Object object);

    /**
     * Returns objects related with a given object (as a graph).
     * (It's recommended to use overloaded method which use object's ID)
     * @param objectId
     * @return {@link cz.cuni.mff.ufal.textan.commons.models.Graph}
     */
    @WebMethod
    public Graph getRelatedObjectsByID(@WebParam(name = "objectId") int objectId);

    /**
     * Returns a path between two objects in graph.
     * @param from start node of the path
     * @param to end node of the path
     * @return returns instance of {@link cz.cuni.mff.ufal.textan.commons.models.Graph} if path exist, otherwise returns null
     */
    @WebMethod
    public Graph getPath(@WebParam(name = "from") Object from, @WebParam(name = "to") Object to);

    /**
     * Returns a path between two objects in graph.
     * @param fromId start node of the path
     * @param toId end node of the path
     * @return returns instance of {@link cz.cuni.mff.ufal.textan.commons.models.Graph} if path exist, otherwise returns null
     */
    @WebMethod
    public Graph getPathByID(@WebParam(name = "from") int fromId, @WebParam(name = "to") int toId);

    /**
     * Merges two objects into one.
     * @param object1Id object to merge
     * @param object2Id object to merge
     * @return merged object
     */
    @WebMethod
    public Object mergeObjects(@WebParam(name = "object1Id") int object1Id, @WebParam(name = "object2Id") int object2Id); //TODO: merge list of objects?

    /**
     * Splits merged object.
     * @param objectId object to split
     * @return result of operation
     */
    @WebMethod
    public Boolean splitObject(@WebParam(name = "objectId") int objectId); //TODO: better return type...


    //TODO: add exception like "Id not exists"
    //TODO: repair overloading which is not supported in web service (unfortunately)
    //TODO: may be delete overloaded methods which use Objects
    //TODO: handle special cases (path not found etc.)
}
