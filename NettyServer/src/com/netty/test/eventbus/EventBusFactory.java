package com.netty.test.eventbus;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.netty.test.event.BaseEvent;

/**
 * 事件总线工厂 将EventBus封装为单例模式使用 来自:http://www.cnblogs.com/jack2013/p/5162838.html
 * 经过一些简单的修改
 * 
 * @author Jack
 * @modified lucher
 */
public class EventBusFactory {

	private volatile static EventBusFactory INSTANCE;

	/**
	 * 保存已经注册的监听器，防止监听器重复注册
	 */
	private Map<String, Class<? extends IEventListener>> registerListenerContainers = Maps.newConcurrentMap();

	private EventBusFactory() {
	}

	public static EventBusFactory build() {
		if (INSTANCE == null) {
			synchronized (EventBusFactory.class) {
				if (INSTANCE == null) {
					INSTANCE = new EventBusFactory();
				}
			}
		}
		return INSTANCE;
	}

	private final EventBus EVENTBUS = new EventBus();

	/**
	 * 事件转发
	 * 
	 * @param event
	 */
	public void postEvent(BaseEvent event) {
		EVENTBUS.post(event);
	}

	/**
	 * 事件转发
	 * 
	 * @param event
	 */
	public void post(Object object) {
		EVENTBUS.post(object);
	}

	/**
	 * 监听器注册
	 * 
	 * @param clazz
	 */
	public void register(Class<? extends IEventListener> clazz) {
		String clazzName = clazz.getSimpleName();
		if (registerListenerContainers.containsKey(clazzName)) {
			return;
		}
		try {
			registerListenerContainers.put(clazzName, clazz);
			Object obj = registerListenerContainers.get(clazzName).newInstance();
			EVENTBUS.register(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消监听注册
	 * 
	 * @param clazz
	 */
	public void unregister(Class<? extends IEventListener> clazz) {
		String clazzName = clazz.getSimpleName();
		if (registerListenerContainers.containsKey(clazzName)) {
			try {
				Object obj = registerListenerContainers.get(clazzName);
				EVENTBUS.unregister(obj);
				registerListenerContainers.remove(clazzName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
