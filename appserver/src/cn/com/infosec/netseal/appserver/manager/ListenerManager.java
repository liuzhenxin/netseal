package cn.com.infosec.netseal.appserver.manager;

import java.util.ArrayList;
import java.util.List;

import cn.com.infosec.netseal.appserver.listener.ServerListener;

public class ListenerManager {
	private List<ServerListener> list = new ArrayList<ServerListener>();

	public List<ServerListener> getList() {
		return list;
	}

	public void setList(List<ServerListener> list) {
		this.list = list;
	}

	public void startService() {
		// 逐一启动各个服务
		for (int i = 0; i < list.size(); i++) {
			// 1. 获取相应的Listener
			ServerListener listener = list.get(i);
			// 2. 启动
			new Thread(listener).start();
		}
	}

	public void stopService() {
		// 逐一启动各个服务
		for (int i = 0; i < list.size(); i++) {
			// 1. 获取相应的Listener
			ServerListener listener = list.get(i);
			// 2. 启动
			listener.shutDown();
		}
	}

	public static void main(String[] args) throws Exception {
		ListenerManager listenerManager = ApplicationContextManager.getBean("listenerManager", ListenerManager.class);
		listenerManager.startService();
	}
}
