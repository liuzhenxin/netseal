package cn.com.infosec.netseal.common.util.socketpool;

import cn.com.infosec.netseal.common.util.cryptoCard.fishman.FishManUtil;

public class SocketPoolShutDownHook extends Thread {
	private NewSocketConnectionPool pool = null;

	public SocketPoolShutDownHook(NewSocketConnectionPool pool) {
		this.pool = pool;
	}

	public void run() {
		if (pool != null) {
			pool.release();
		}

		try {
			FishManUtil.closeDevice();
		} catch (Exception e) {
		}
	}
}
