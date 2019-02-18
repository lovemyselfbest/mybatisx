package cn.le55.zookeeper.watcher;

import org.apache.curator.framework.CuratorFramework;

/**
 * Created by kelly-lee on 2017/11/21.
 */
public interface NodeCacheListener {
    public void nodeChanged(CuratorFramework curatorFramework, NodeCacheEvent nodeCacheEvent);
}
