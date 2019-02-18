package cn.le55.zookeeper;


import cn.le55.zookeeper.leader.LeaderLatchSelector;
import cn.le55.zookeeper.leader.LeaderSelector;
import cn.le55.zookeeper.lock.InterProcessMutexLock;
import cn.le55.zookeeper.observer.EventResolver;
import cn.le55.zookeeper.observer.ZkObserver;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import cn.le55.zookeeper.watcher.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by kelly.li on 17/11/14.
 */
// session timeout 默认1分钟
// connect timeout 默认15秒
//异步
//授权
//限制
public class DefaultZkClient implements ZkClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultZkClient.class);

    CuratorFramework curatorFramework;

    private ConcurrentMap<String, NodeCacheWatcher> nodeCacheWatchers = Maps.newConcurrentMap();
    private ConcurrentMap<String, PathChildrenCacheWatcher> pathChildrenCacheWatchers = Maps.newConcurrentMap();
    private ConcurrentMap<String, TreeCacheWatcher> treeCacheWatchers = Maps.newConcurrentMap();
    private ConcurrentMap<String, LeaderLatchSelector> leaderLatchSeletors = Maps.newConcurrentMap();
    private ConcurrentMap<String, ZkObserver> zkObservers = Maps.newConcurrentMap();
    private ConcurrentMap<String, LeaderSelector> leaderSelectors = Maps.newConcurrentMap();
    private ConcurrentMap<String, LeaderLatchSelector> leaderLatchSelectors = Maps.newConcurrentMap();
    private ConcurrentMap<String, InterProcessMutexLock> interProcessMutexLocks = Maps.newConcurrentMap();

    public DefaultZkClient(String connectString) {
        curatorFramework = CuratorFrameworkFactory.newClient(connectString, RETRY_INFINITY);
        start();
    }

    public DefaultZkClient(String connectString, String namespace) {
        curatorFramework = CuratorFrameworkFactory.builder().connectString(connectString).namespace(namespace).retryPolicy(RETRY_INFINITY).build();
        start();
    }

    public DefaultZkClient(String connectString, String namespace, String username, String password) {
        String auth = Joiner.on(":").skipNulls().join(username, password);
        curatorFramework = CuratorFrameworkFactory.builder().connectString(connectString).namespace(namespace)
                .authorization(SCHEME_DIGEST, auth.getBytes()).aclProvider(CREATOR_ALL_ACL_PROVIDER).retryPolicy(RETRY_INFINITY).build();
        start();
    }

    @Override
    public void start() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        addConnectionStateListener(new ConnectionStateListener() {
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (connectionState == ConnectionState.CONNECTED) {
                    countDownLatch.countDown();
                }
            }
        });
        ///异步,可以根据ConnectionStateListener得到连接状态
        curatorFramework.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.warn("await curatorFramework start interrupted", e);
        }
    }

    @Override
    public void create(String path) throws Exception {
        create(path, CreateMode.PERSISTENT, DEFAULT_DATA);
    }

    @Override
    public void createEphemeral(String path) throws Exception {
        create(path, CreateMode.EPHEMERAL, DEFAULT_DATA);
    }
    @Override
    public void createEphemeral(String path,String data) throws Exception {
        create(path, CreateMode.EPHEMERAL, data.getBytes());
    }
    @Override
    public void create(String path, CreateMode createMode, byte[] data) throws Exception {
        try {
            curatorFramework.create().creatingParentsIfNeeded().withMode(createMode).forPath(path, data);
        } catch (KeeperException.NodeExistsException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    @Override
    public Stat setData(String path, byte[] data) throws Exception {
        return setData(path, data, DEFAULT_VERSION);
    }

    @Override
    public Stat setData(String path, byte[] data, int version) throws Exception {
        try {
            return curatorFramework.setData().withVersion(version).forPath(path, data);
        } catch (KeeperException.NoNodeException e) {
            LOGGER.error(e.getMessage());
            return null;
        } catch (KeeperException.BadVersionException e) {
            LOGGER.error(e.getMessage());
            return null;
        } catch (KeeperException.NoAuthException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Override
    public void delele(String path) throws Exception {
        delele(path, DEFAULT_VERSION);
    }

    @Override
    public void delele(String path, int version) throws Exception {
        try {
            curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().withVersion(version).forPath(path);
        } catch (KeeperException.NoNodeException e) {
            LOGGER.error(e.getMessage());
        } catch (KeeperException.BadVersionException e) {
            LOGGER.error(e.getMessage());
        } catch (KeeperException.NoAuthException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public byte[] getData(String path) throws Exception {
        try {
            return curatorFramework.getData().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            LOGGER.error(e.getMessage());
            return null;
        } catch (KeeperException.NoAuthException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Override
    public String getStringData(String path) throws Exception {
        byte[] data = getData(path);
        return data == null ? EMPTY_STRING : new String(getData(path));
    }

    @Override
    public List<String> getChildren(String path) throws Exception {
        try {
            return curatorFramework.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            LOGGER.error(e.getMessage());
            return Collections.emptyList();
        } catch (KeeperException.NoAuthException e) {
            LOGGER.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public boolean checkExist(String path) throws Exception {
        return stat(path) != null;
    }

    @Override
    public Stat stat(String path) throws Exception {
        return curatorFramework.checkExists().forPath(path);
    }

    @Override
    public void addConnectionStateListener(ConnectionStateListener connectionStateListener) {
        curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
    }

    @Override
    public NodeCacheWatcher addNodeCacheListener(String path, NodeCacheListener nodeCacheListener) throws Exception {
        NodeCacheWatcher nodeCacheWatcher = nodeCacheWatchers.getOrDefault(path, new NodeCacheWatcher(curatorFramework, path));
        nodeCacheWatchers.putIfAbsent(path, nodeCacheWatcher);
        nodeCacheWatcher.addListener(new org.apache.curator.framework.recipes.cache.NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                nodeCacheListener.nodeChanged(curatorFramework, new NodeCacheEvent(nodeCacheWatcher.getCurrentData()));
            }
        });
        return nodeCacheWatcher;

    }

    @Override
    public PathChildrenCacheWatcher addPathChildrenCacheListener(String path, PathChildrenCacheListener pathChildrenCacheListener) throws Exception {
        PathChildrenCacheWatcher pathChildrenCacheWatcher = pathChildrenCacheWatchers.getOrDefault(path, new PathChildrenCacheWatcher(curatorFramework, path));
        pathChildrenCacheWatchers.putIfAbsent(path, pathChildrenCacheWatcher);
        pathChildrenCacheWatcher.addListener(pathChildrenCacheListener);
        return pathChildrenCacheWatcher;
    }

    @Override
    public TreeCacheWatcher addTreeCacheListener(String path, TreeCacheListener treeCacheListener) throws Exception {
        TreeCacheWatcher treeCacheWatcher = treeCacheWatchers.getOrDefault(path, new TreeCacheWatcher(curatorFramework, path));
        treeCacheWatchers.putIfAbsent(path, treeCacheWatcher);
        treeCacheWatcher.addListener(treeCacheListener);

        return treeCacheWatcher;
    }

    @Override
    public LeaderLatchSelector addLeaderLatchListener(String path, String id, LeaderLatchListener leaderLatchListener) throws Exception {
        LeaderLatchSelector leaderLatchSelector = leaderLatchSeletors.getOrDefault(path, new LeaderLatchSelector(curatorFramework, path, id));
        leaderLatchSeletors.putIfAbsent(path, leaderLatchSelector);
        leaderLatchSelector.addListener(leaderLatchListener);
        return leaderLatchSelector;
    }

    @Override
    public LeaderSelector addLeaderSelectorListener(String path, String id) throws Exception {
        LeaderSelector leaderSelector = leaderSelectors.getOrDefault(path, new LeaderSelector(curatorFramework, path, id));
        leaderSelectors.putIfAbsent(path, leaderSelector);
        return leaderSelector;
    }

    public InterProcessMutexLock acquire(String path) throws Exception {
        InterProcessMutexLock interProcessMutexLock = interProcessMutexLocks.getOrDefault(path, new InterProcessMutexLock(curatorFramework, path));
        interProcessMutexLocks.putIfAbsent(path, interProcessMutexLock);
        interProcessMutexLock.acquire();
        return interProcessMutexLock;
    }


    public LeaderLatchSelector getLeaderLatchSelector(String path) {
        return leaderLatchSelectors.get(path);
    }

    public CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }


    @Override
    public ZkObserver addObserver(String path, EventResolver eventResolver) throws Exception {
        TreeCacheWatcher treeCacheWatcher = treeCacheWatchers.getOrDefault(path, new TreeCacheWatcher(curatorFramework, path));
        ZkObserver zkObserver = zkObservers.getOrDefault(path, new ZkObserver(treeCacheWatcher));
        zkObserver.registerEventListener(eventResolver);
        return zkObserver;
    }

    @Override
    public void close() {
        CloseableUtils.closeQuietly(curatorFramework);
    }
}
