package cn.le55.zookeeper.observer;


import cn.le55.zookeeper.ZkClient;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by kelly.li on 17/11/18.
 */
public class NodeEvent {

    private List<String> nodes;
    private String path;
    private String endpoint;
    private byte[] data;
    private Stat stat;

    public NodeEvent(TreeCacheEvent treeCacheEvent) {
        ChildData childData = treeCacheEvent.getData();
        this.nodes = ZkClient.PATH_SPLITTER.splitToList(childData.getPath());
        if (nodes.size() > 0) {
            this.endpoint = nodes.get(nodes.size() - 1);
        }
        this.data = childData.getData();
        this.stat = childData.getStat();
        this.path = childData.getPath();
    }

    public int getLayer() {
        return nodes.size();
    }

    public String getPath() {
        return path;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public byte[] getData() {
        return data;
    }

    public Stat getStat() {
        return stat;
    }
}
