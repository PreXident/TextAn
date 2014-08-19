package cz.cuni.mff.ufal.textan.core;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Client side representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.JoinedObject}.
 */
public class JoinedObject implements Serializable {

    /** Root object. */
    public final Object root;

    /** Children objects. */
    public final List<Object> children;

    /**
     * Only constructor.
     * @param joinedObject blue print
     */
    public JoinedObject(cz.cuni.mff.ufal.textan.commons.models.JoinedObject joinedObject) {
        root = new Object(joinedObject.getRootObject());
        children = joinedObject.getChildObjects().stream()
                .map(Object::new)
                .collect(Collectors.toList());
    }
}
