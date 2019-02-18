package cn.le55.zookeeper.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * Created by kelly-lee on 2017/11/21.
 */
public class InterProcessReadWriteLock {

    private org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock interProcessReadWriteLock;

    public InterProcessReadWriteLock(CuratorFramework curatorFramework, String path) {
        interProcessReadWriteLock = new org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock(curatorFramework, path);
    }

    public InterProcessMutex readLock() {
        return interProcessReadWriteLock.readLock();
    }

    public InterProcessMutex writeLock() {
        return interProcessReadWriteLock.writeLock();
    }
    
}
