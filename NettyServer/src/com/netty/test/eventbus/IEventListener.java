package com.netty.test.eventbus;

/**
 * EventBus事件监听接口
 * @author lucher
 *
 */
public interface IEventListener {

    public void onEvent(String message) ;
    
}