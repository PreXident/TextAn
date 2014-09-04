package cz.cuni.mff.ufal.textan.server.models;

/**
 * A service layer representation of the Joined object.
 * @author Petr Fanta
 */
public class JoinedObject {

    private final Object root;
    private final Object child1;
    private final Object child2;

    /**
     * Instantiates a new JoinedObject
     * @param root the root object in the joint
     * @param child1 the coupled object
     * @param child2 the coupled object
     */
    public JoinedObject(Object root, Object child1, Object child2) {
        this.root = root;
        this.child1 = child1;
        this.child2 = child2;
    }

    /**
     * Gets the root object in a tree.
     * @return the root
     */
    public Object getRoot() {
        return root;
    }

    /**
     * Gets the first child.
     * @return the child
     */
    public Object getChild1() {
        return child1;
    }

    /**
     * Gets the second child.
     * @return the child
     */
    public Object getChild2() {
        return child2;
    }

    /**
     * Converts the instance to a {@link cz.cuni.mff.ufal.textan.commons.models.JoinedObject}
     * @return the {@link cz.cuni.mff.ufal.textan.commons.models.JoinedObject}
     */
    public cz.cuni.mff.ufal.textan.commons.models.JoinedObject toCommonsJoinedObject() {
        cz.cuni.mff.ufal.textan.commons.models.JoinedObject commonsJoinedObject = new cz.cuni.mff.ufal.textan.commons.models.JoinedObject();
        commonsJoinedObject.setRootObject(root.toCommonsObject());
        commonsJoinedObject.getChildObjects().add(child1.toCommonsObject());
        commonsJoinedObject.getChildObjects().add(child2.toCommonsObject());

        return commonsJoinedObject;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinedObject that = (JoinedObject) o;

        return ((that.root.equals(this.root)) &&
                ((that.child1.equals(this.child1) && that.child2.equals(this.child2)) ||
                        (that.child1.equals(this.child2) && that.child2.equals(this.child1))));
    }

    @Override
    public int hashCode() {
        int result = root != null ? root.hashCode() : 0;
        result = 31 * result + (child1 != null ? child1.hashCode() : 0);
        result = 31 * result + (child2 != null ? child2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JoinedObject{");
        sb.append("root=").append(root);
        sb.append(", child1=").append(child1);
        sb.append(", child2=").append(child2);
        sb.append('}');
        return sb.toString();
    }
}
