package cn.le55.zookeeper.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.RevocationListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by kelly-lee on 2017/11/17.
 */
//可重入共享锁—Shared Reentrant Lock
//可重用的，你可以在一个线程中多次调用acquire(),在线程拥有锁时它总是返回true。
//你不应该在多个线程中用同一个InterProcessMutex
public class InterProcessMutexLock {

    private InterProcessMutex interProcessMutex;

    public InterProcessMutexLock(CuratorFramework curatorFramework, String path) {
        interProcessMutex = new InterProcessMutex(curatorFramework, path);
    }

    public void acquire() throws Exception {
        interProcessMutex.acquire();
    }

    public boolean acquire(long time, TimeUnit timeUnit) throws Exception {
        return interProcessMutex.acquire(time, timeUnit);
    }

    public void release() throws Exception {
        interProcessMutex.release();
    }
    //将锁设为可撤销的. 当别的进程或线程想让你释放锁时Listener会被调用
    public void makeRevocable(RevocationListener<InterProcessMutex> revocationListener){
        interProcessMutex.makeRevocable(revocationListener);
    }

}
