package cn.com.infosec.netseal.appapi.common.util.socketpool;

public class SocketPoolShutDownHook extends Thread {
	private NewSocketConnectionPool pool = null;

	public SocketPoolShutDownHook(NewSocketConnectionPool pool) {
		this.pool = pool;
	}

	public void run() {
		if (pool != null) {
			pool.release();
		}
	}
}
