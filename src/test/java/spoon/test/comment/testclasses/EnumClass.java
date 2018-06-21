package spoon.test.comment.testclasses;

import spoon.SpoonException;
import spoon.pattern.internal.node.RootNode;

public enum EnumClass {
    /**
     * Throw {@link SpoonException} if a conflict happens, it is the default in most cases. But there are some standard Pattern builder algorithms (mainly these which deals with legacy Templates), which are using the other modes.
     */
    FAIL,
    /**
     * Get rid of old {@link RootNode} and use new {@link RootNode} instead
     */
    USE_NEW_NODE,
    /**
     * Keep old {@link RootNode} and ignore requests to add new {@link RootNode}
     */
    KEEP_OLD_NODE(),
    /**
     * Add new {@link RootNode} after existing nodes
     */
    APPEND
}
