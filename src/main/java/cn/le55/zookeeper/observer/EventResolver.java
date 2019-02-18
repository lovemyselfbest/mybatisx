package cn.le55.zookeeper.observer;

import com.google.common.eventbus.Subscribe;

/**
 * Created by kelly.li on 17/11/18.
 */
public interface EventResolver {

    @Subscribe
    public void handle(NodeAddedEvent event);

    @Subscribe
    public void handle(NodeRemovedEvent event);


}
