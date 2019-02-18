package cn.le55.zookeeper.barrier;

import org.apache.curator.framework.CuratorFramework;

import java.util.concurrent.TimeUnit;

/**
 * Created by kelly.li on 17/11/19.
 */
public class DistributedBarrier {

    private org.apache.curator.framework.recipes.barriers.DistributedBarrier distributedBarrier;

    public DistributedBarrier(CuratorFramework curatorFramework, String barrierPath) {
        distributedBarrier = new org.apache.curator.framework.recipes.barriers.DistributedBarrier(curatorFramework, barrierPath);
    }

    public void setBarrier() throws Exception {
        distributedBarrier.setBarrier();
    }

    //堵塞,在removeBarrier调用后执行
    public void waitOnBarrier() throws Exception {
        distributedBarrier.waitOnBarrier();
    }

    public void waitOnBarrier(long maxWait, TimeUnit unit) throws Exception {
        distributedBarrier.waitOnBarrier(maxWait, unit);
    }

    public void removeBarrier() throws Exception {
        distributedBarrier.removeBarrier();
    }


}
