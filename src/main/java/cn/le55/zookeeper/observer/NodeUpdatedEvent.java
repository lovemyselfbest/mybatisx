package cn.le55.zookeeper.observer;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

/**
 * Created by kelly.li on 17/11/18.
 */
public class NodeUpdatedEvent extends NodeEvent {

    public NodeUpdatedEvent(TreeCacheEvent treeCacheEvent) {
        super(treeCacheEvent);
    }
}
