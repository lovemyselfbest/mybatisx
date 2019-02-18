package cn.le55.zookeeper.watcher;

import com.google.common.base.Function;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by kelly.li on 17/11/17.
 */
public class PathChildrenCacheWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeCacheWatcher.class);
    private CuratorFramework curatorFramework;
    private String path;
    private PathChildrenCache pathChildrenCache;

    public PathChildrenCacheWatcher(CuratorFramework curatorFramework, String path) {
        this.curatorFramework = curatorFramework;
        this.path = path;
        pathChildrenCache = new PathChildrenCache(curatorFramework, path, true);
    }

    public void start() {
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            LOGGER.warn("PathChildrenCache[{},{}] {}", new Object[]{curatorFramework.getZookeeperClient().getCurrentConnectionString(), path, e.getMessage()});
        }
    }

    public void addListener(PathChildrenCacheListener pathChildrenCacheListener) {
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
    }

    public void removeListener(PathChildrenCacheListener pathChildrenCacheListener) {
        pathChildrenCache.getListenable().removeListener(pathChildrenCacheListener);
    }

    public void forEachListener(Function<PathChildrenCacheListener, Void> function) {
        pathChildrenCache.getListenable().forEach(function);
    }

    public void rebuild() throws Exception {
        pathChildrenCache.rebuild();
    }

    public List<ChildData> getCurrentData() {
        return pathChildrenCache.getCurrentData();
    }

    public ChildData getCurrentData(String fullPath) {
        return pathChildrenCache.getCurrentData(fullPath);
    }

    public void close() {
        CloseableUtils.closeQuietly(pathChildrenCache);
    }
}
