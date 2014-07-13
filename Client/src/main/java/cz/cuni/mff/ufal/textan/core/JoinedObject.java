package cz.cuni.mff.ufal.textan.core;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Client side representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.JoinedObject}.
 */
public class JoinedObject {

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

    /**
     * Test constructor.
     */
    //TODO remove testing constructor
    public JoinedObject() {
        root = new Object(-1, new ObjectType(-1, "XXX"), Arrays.asList("xxx", "zzz"));
        children = Arrays.asList(new Object(-1, new ObjectType(-1, "QQQ"), Arrays.asList("aaa", "bbb")),
                new Object(-1, new ObjectType(-1, "WWW"), Arrays.asList("ccc", "ddd")));
    }
}
