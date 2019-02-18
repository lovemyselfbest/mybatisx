package cn.le55.zookeeper.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.utils.CloseableUtils;

import java.io.EOFException;

/**
 * Created by kelly.li on 17/11/16.
 */
public class LeaderLatchSelector {

    LeaderLatch leaderLatch;

    public LeaderLatchSelector(CuratorFramework curatorFramework, String path, String id) {
        this.leaderLatch = new LeaderLatch(curatorFramework, path, id);
    }

    public void start() throws Exception {
        leaderLatch.start();
    }

    public void addListener(LeaderLatchListener leaderLatchListener) {
        leaderLatch.addListener(leaderLatchListener);
    }

    public boolean hasLeadership() {
        return leaderLatch.hasLeadership();
    }

    public void await() throws EOFException, InterruptedException {
        leaderLatch.await();
    }

    public String getId() {
        return leaderLatch.getId();
    }

    public void close() {
        CloseableUtils.closeQuietly(leaderLatch);
    }
}
