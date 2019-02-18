package cn.le55.zookeeper.watcher;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.*;

/**
 * Created by kelly-lee on 2017/11/21.
 */
public class NodeCacheEvent {

    private ChildData childData;
    private TreeCacheEvent.Type type;

    public NodeCacheEvent(ChildData childData) {
        this.childData = childData;
        if (childData == null) {
            type = NODE_REMOVED;
        } else {
            type = childData.getStat().getVersion() == 0 ? NODE_ADDED : NODE_UPDATED;
        }
    }

    public ChildData getData() {
        return childData;
    }


    public TreeCacheEvent.Type getType() {
        return this.type;
    }

    public static enum Type {
        NODE_ADDED,
        NODE_UPDATED,
        NODE_REMOVED;

        private Type() {
        }
    }
}
