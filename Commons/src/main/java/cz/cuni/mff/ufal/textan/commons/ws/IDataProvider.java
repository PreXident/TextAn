package cz.cuni.mff.ufal.textan.commons.ws;

import cz.cuni.mff.ufal.textan.commons.models.ObjectType;
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
     * Returns all objects which are currently defined in the system
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

    /*
    @WebMethod
    public ... getRelatedObjects(@WebParam(name = "objectId") int objectId);
    */
}
