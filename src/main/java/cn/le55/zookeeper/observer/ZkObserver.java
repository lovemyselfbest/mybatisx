package cn.le55.zookeeper.observer;

import cn.le55.zookeeper.watcher.TreeCacheWatcher;
import com.google.common.eventbus.EventBus;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.CloseableUtils;

/**
 * Created by kelly.li on 17/11/17.
 */
public class ZkObserver implements TreeCacheListener {

    TreeCache treeCache;
    TreeCacheWatcher treeCacheWatcher;
    EventBus eventBus;

    public ZkObserver(TreeCacheWatcher treeCacheWatcher) {
        eventBus = new EventBus();
        this.treeCacheWatcher = treeCacheWatcher;
        treeCacheWatcher.addListener(this);
    }

    public void registerEventListener(Object listener) {
        eventBus.register(listener);
    }

    public void start() throws Exception {
        treeCacheWatcher.start();
    }


    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        ChildData data = treeCacheEvent.getData();
        switch (treeCacheEvent.getType()) {
            case NODE_ADDED:
                eventBus.post(new NodeAddedEvent(treeCacheEvent));
                break;
            case NODE_REMOVED:
                eventBus.post(new NodeRemovedEvent(treeCacheEvent));
                break;
            case NODE_UPDATED:
                eventBus.post(new NodeUpdatedEvent(treeCacheEvent));
                break;
            default:
                break;
        }
    }


    public void close() {
        CloseableUtils.closeQuietly(treeCache);
    }


}
