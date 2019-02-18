package cn.le55.zookeeper;


import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class zkApp {
    public static void main2(String[] args) throws Exception {

        ZkClient zkClient = new DefaultZkClient("10.1.62.23:2181");


       // String path2 = "/service2/orderdata/1.0.0/"+System.currentTimeMillis();

        //zkClient.createEphemeral(path2,"test");



        String path = "/";

    //    zkClient.close();


        TreeCacheListener treeCacheListener= (curatorFramework, treeCacheEvent) -> {
            ChildData data = treeCacheEvent.getData();


            switch (treeCacheEvent.getType()) {
                case CONNECTION_SUSPENDED:
                    System.out.println("CONNECTION_SUSPENDED");
                    break;
                case CONNECTION_RECONNECTED:
                    System.out.println("CONNECTION_RECONNECTED");
                    break;
                case CONNECTION_LOST:
                    System.out.println("CONNECTION_LOST");
                    break;
                case INITIALIZED:
                    System.out.println("INITIALIZED : " + data);
                    break;
                case NODE_ADDED:
                    System.out.println("NODE_ADDED : " + data.getPath() + "  数据:" + new String(data.getData()));
                    break;
                case NODE_REMOVED:
                    System.out.println("NODE_REMOVED : " + data.getPath());
                    break;
                case NODE_UPDATED:
                    System.out.println("NODE_UPDATED : " + data.getPath() + "  数据:" + new String(data.getData()));
                    break;

                default:
                    break;
            }
        };
        zkClient.addTreeCacheListener(path, treeCacheListener).start();

        System.in.read();

        System.out.println("Hello World!");
    }
}
