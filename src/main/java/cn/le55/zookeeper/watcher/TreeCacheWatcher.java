package cn.le55.zookeeper.watcher;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by kelly.li on 17/11/17.
 */
public class TreeCacheWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(TreeCacheWatcher.class);
    private CuratorFramework curatorFramework;
    private String path;
    private TreeCache treeCache;

    public TreeCacheWatcher(CuratorFramework curatorFramework, String path) {
        this.curatorFramework = curatorFramework;
        this.path = path;
        treeCache = new TreeCache(curatorFramework, path);
    }

    public void start() throws Exception {
        try {
            treeCache.start();
        } catch (Exception e) {
            LOGGER.warn("TreeCacheWatcher[{},{}] {}", new Object[]{curatorFramework.getZookeeperClient().getCurrentConnectionString(), path, e.getMessage()});
        }
    }

    public void addListener(TreeCacheListener treeCacheListener) {
        treeCache.getListenable().addListener(treeCacheListener);
    }

    public void removeListener(TreeCacheListener treeCacheListener) {
        treeCache.getListenable().removeListener(treeCacheListener);
    }

    public Map<String, ChildData> getCurrentChildren(String fullPath) {
        return treeCache.getCurrentChildren(fullPath);
    }

    public ChildData getCurrentData(String fullPath) {
        return treeCache.getCurrentData(fullPath);
    }

    public void close() {
        treeCache.close();
    }
}
