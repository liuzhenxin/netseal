package cn.com.infosec.netseal.appapi.common.util.socketpool;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Socket池维护线程
 * 
 * 
 */
public class MaintainSocketPoolThread implements Runnable {

	private int minConnNum = 0;
	private int checkNousedIntervalTime = 1000;
	private NewSocketConnectionPool pool = null;

	private volatile boolean isRun = true;

	public MaintainSocketPoolThread() {
	}

	public MaintainSocketPoolThread(NewSocketConnectionPool pool) {
		this.pool = pool;
		checkNousedIntervalTime = pool.getThreadCheckIntervalTime();
		minConnNum = pool.getMinNumConn();
	}

	public void run() {
		Thread.currentThread().setName(" MaintainPool_" + pool.getPoolName());

		ArrayList allPooledSocketListClone = new ArrayList(); // 复制一份，保存对所有Socket对象的引用
		for (int i = 0; i < pool.getAllSocketList().size(); i++) {
			allPooledSocketListClone.add(pool.getAllSocketList().get(i));
		}

		while (isRun) {
			try {
				// 1. 先停滞一段时间
				try {
					Thread.sleep(checkNousedIntervalTime);
				} catch (Exception e) {
				}

				StringBuffer sb = new StringBuffer();
				sb.append("Run SocketPool Maintain Thread ..." + " freeConnSize:" + pool.getFreeSocketList().size() + " ");
				// 2. 对所有连接，先排序
				Collections.sort(allPooledSocketListClone);

				// 3. 保留最小连接数，关闭剩余的连接
				int closeConnNum = 0;
				for (int i = 0; i < allPooledSocketListClone.size(); i++) {

					SocketConnection poolConn = (SocketConnection) allPooledSocketListClone.get(i);
					if (i < minConnNum) {
						// try {
						sb.append(" Keep alive Socket[" + poolConn + "] ");
						// //保证最小连接数
						// poolConn.checkConn();
						//
						// } catch (Exception e) {
						// pool.log("Keep  Socket alive error", e);
						// }
					} else {
						try {
							boolean closed = poolConn.closeNoLongTimeNoUsed();
							sb.append("Close Socket (" + closed + ") [" + poolConn + "] ");
							if (closed)
								closeConnNum++;
						} catch (Exception e) {
						}
					}
				}

				// 4. 记录必要的日志
				// if ( closeConnNum > 0)
				// LoggerUtil.systemlog("Check expired connection thread: closed num "
				// + closeConnNum + pool.LINE_SEPARATOR + sb.toString());
			} catch (Throwable e) {
			}
		}

		allPooledSocketListClone.clear();
	}

	/**
	 * @return the isRun
	 */
	public boolean isRun() {
		return isRun;
	}

	/**
	 * @param isRun
	 *            the isRun to set
	 */
	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
}
