/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.graph.pathfinding;

import cz.cuni.mff.ufal.textan.data.tables.InRelationTable;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vaclav Pernicka
 */
public class FloydWarshallPathFinder implements IPathFinder {

    @Override
    public List<ObjectTable> findShortestPath(ObjectTable obj1, ObjectTable obj2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createdNewObject(ObjectTable newObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createdNewJoin(JoinedObjectsTable newJoin) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createdNewInRelation(InRelationTable newInRelation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private interface IDistanceMatrix {
        void createNewObject(ObjectTable object);

        void createdNewJoin(JoinedObjectsTable newJoin);

        void createdNewInRelation(InRelationTable newInRelation);

        Cell get(int idx1, int idx2);

    }

    private class Cell {
        long objectIdBetween;
        int pathLength;

        public Cell(long objectIdBetween, int pathLength) {
            this.objectIdBetween = objectIdBetween;
            this.pathLength = pathLength;
        }

        public long getObjectIdBetween() {
            return objectIdBetween;
        }

        public void setObjectIdBetween(long objectIdBetween) {
            this.objectIdBetween = objectIdBetween;
        }

        public int getPathLength() {
            return pathLength;
        }

        public void setPathLength(int pathLength) {
            this.pathLength = pathLength;
        }

    }

    private class DistanceMatrixInMemory implements IDistanceMatrix {
        final Map<ObjectTable, Integer> objectToIndex;
        final ArrayList<ArrayList<Cell>> distances;

        public DistanceMatrixInMemory() {
            distances = new ArrayList<>();
            objectToIndex = new HashMap<>();
        }

        public void createNewObject(ObjectTable object) {
            int newIndex = distances.size();
            objectToIndex.put(object, newIndex);
            distances.add(new ArrayList<>(newIndex));
            for (int i = 0; i < newIndex; i++) {
                distances.get(newIndex).add(new Cell(-1, Integer.MAX_VALUE));
            }
            distances.get(newIndex).add(new Cell(object.getId(), 0));
        }

        @Override
        public void createdNewJoin(JoinedObjectsTable newJoin) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void createdNewInRelation(InRelationTable newInRelation) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Cell get(int idx1, int idx2) {
            return distances.get(idx1).get(idx2);
        }

    }


}
