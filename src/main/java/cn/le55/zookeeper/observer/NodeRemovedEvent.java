package cn.le55.zookeeper.observer;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

/**
 * Created by kelly.li on 17/11/18.
 */
public class NodeRemovedEvent extends  NodeEvent {

    public NodeRemovedEvent(TreeCacheEvent treeCacheEvent) {
        super(treeCacheEvent);
    }
}
