package cn.le55.zookeeper.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

import java.util.concurrent.TimeUnit;

/**
 * Created by kelly-lee on 2017/11/21.
 */
//不可重入共享锁—Shared Lock
//不能在同一个线程中重入
public class InterProcessSemaphoreMutexLock {

    private InterProcessSemaphoreMutex interProcessSemaphoreMutex;

    public InterProcessSemaphoreMutexLock(CuratorFramework curatorFramework, String path) {
        interProcessSemaphoreMutex = new InterProcessSemaphoreMutex(curatorFramework, path);
    }

    public void acquire() throws Exception {
        interProcessSemaphoreMutex.acquire();
    }

    public void acquire(long time, TimeUnit timeUnit) throws Exception {
        interProcessSemaphoreMutex.acquire(time, timeUnit);
    }

    public void release() throws Exception {
        interProcessSemaphoreMutex.release();
    }

    public void isAcquiredInThisProcess() {
        interProcessSemaphoreMutex.isAcquiredInThisProcess();
    }
}
