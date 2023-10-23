package ee.ut.dendroloj;

import java.util.concurrent.atomic.AtomicLong;

class IdHelper {

    private static final AtomicLong edgeIdCounter = new AtomicLong(0);

    public static String getNodeId(Object object) {
        if (object instanceof Number || object instanceof String) {
            // For primitives and strings use value
            return object.toString();
        } else {
            // For classes use reference identity
            // TODO: This value is actually not guaranteed to be unique. Figure out a way to guarantee uniqueness.
            return Integer.toHexString(System.identityHashCode(object));
        }
    }

    public static String getNewEdgeId() {
        return Long.toHexString(edgeIdCounter.getAndIncrement());
    }

}
