package cn.le55.zookeeper.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;

import java.util.Collection;

/**
 * Created by kelly-lee on 2017/11/21.
 */
public class InterProcessSemaphoreV2Lock {

    private InterProcessSemaphoreV2 interProcessSemaphoreV2;

    public InterProcessSemaphoreV2Lock(CuratorFramework curatorFramework, String path, int maxLeases) {
        interProcessSemaphoreV2 = new InterProcessSemaphoreV2(curatorFramework, path, maxLeases);
    }

    public Lease acquire() throws Exception {
        return interProcessSemaphoreV2.acquire();
    }

    public Collection<Lease> acquire(int qty) throws Exception {
        return interProcessSemaphoreV2.acquire(qty);
    }




}
