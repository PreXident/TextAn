/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.graph.pathfinding;

import cz.cuni.mff.ufal.textan.data.tables.InRelationTable;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;

import java.util.List;

/**
 * @author Vaclav Pernicka
 */
public interface IPathFinder {
    List<ObjectTable> findShortestPath(ObjectTable obj1, ObjectTable obj2);

    void createdNewObject(ObjectTable newObject);

    void createdNewJoin(JoinedObjectsTable newJoin);

    void createdNewInRelation(InRelationTable newInRelation);
}
