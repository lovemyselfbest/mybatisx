package cn.le55.zookeeper.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;

/**
 * Created by kelly.li on 17/11/18.
 */
public class LeaderSelector extends LeaderSelectorListenerAdapter {

    private final org.apache.curator.framework.recipes.leader.LeaderSelector leaderSelector;

    public LeaderSelector(CuratorFramework curatorFramework, String leaderPath, String id) {
        leaderSelector = new org.apache.curator.framework.recipes.leader.LeaderSelector(curatorFramework, leaderPath, this);
        leaderSelector.setId(id);
        //保此实例在释放领导权后还可能获得领导权
        leaderSelector.autoRequeue();
    }


    public void start() {
        leaderSelector.start();
    }

    @Override
    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        System.out.println(leaderSelector.getId() + " own leader");
        Thread.sleep(1000 * 5);
        System.out.println(leaderSelector.getId() + " lost leader");
        //leaderSelector.close();
       // curatorFramework.close();
    }

    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        if (newState == ConnectionState.SUSPENDED || newState == ConnectionState.LOST) {
            System.out.println(leaderSelector.getId() + "lost");
        }
    }

    public void close() {
        leaderSelector.close();
    }
}
