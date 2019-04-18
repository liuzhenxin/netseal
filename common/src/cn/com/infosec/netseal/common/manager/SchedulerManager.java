package cn.com.infosec.netseal.common.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.scheduler.BaseScheduler;

/**
 * 维护系统中各个计划任务 启、停、状态
 * 
 * @author zhaojz
 *
 */
public class SchedulerManager {

	private static HashMap<String, ScheduledExecutorService> serviceManager = new HashMap<String, ScheduledExecutorService>();
	private static HashMap<String, BaseScheduler> schedulerManager = new HashMap<String, BaseScheduler>();
	private static HashMap<String, Boolean> statusManager = new HashMap<String, Boolean>();

	/**
	 * 每次 停止 任务后，要重新调用本方法
	 * 
	 * @param key
	 * @param bs
	 */
	public static synchronized void setScheduler(String key, BaseScheduler bs) {
		stopAndClear(key);

		serviceManager.put(key, Executors.newScheduledThreadPool(1));
		schedulerManager.put(key, bs);
		statusManager.put(key, false);
	}

	public static synchronized void start(String key) {
		ScheduledExecutorService sec = serviceManager.get(key);
		BaseScheduler bs = schedulerManager.get(key);
		if (sec == null || bs == null)
			throw new NetSealRuntimeException(ErrCode.GET_SCHEDULER_IS_NULL, "scheduler service is " + sec + " scheduler is " + bs);

		if (!isRunning(key)) {
			sec.scheduleWithFixedDelay(bs, bs.getInitialDelay(), bs.getInterval(), TimeUnit.SECONDS);
			statusManager.put(key, true);
		}
	}

	public static synchronized void startAll() {
		List<String> keys = getKeys();
		for (String key : keys) {
			ScheduledExecutorService sec = serviceManager.get(key);
			BaseScheduler bs = schedulerManager.get(key);

			if (sec == null || bs == null)
				throw new NetSealRuntimeException(ErrCode.GET_SCHEDULER_IS_NULL, "scheduler service is " + sec + " scheduler is " + bs);

			if (!isRunning(key)) {
				sec.scheduleWithFixedDelay(bs, bs.getInitialDelay(), bs.getInterval(), TimeUnit.SECONDS);
				statusManager.put(key, true);
			}
		}
	}

	/**
	 * 计划任务 停止以后 想再启动 需要重新调用 setScheduler()
	 * 
	 * @param key
	 */
	public static synchronized void stopAndClear(String key) {
		ScheduledExecutorService sec = serviceManager.remove(key);
		schedulerManager.remove(key);
		statusManager.remove(key);

		if (sec != null)
			sec.shutdown();
	}

	public static synchronized boolean isRunning(String key) {
		Boolean ret = statusManager.get(key);
		if (ret == null)
			return false;

		return ret;
	}

	public static synchronized List<String> getKeys() {
		List<String> list = new ArrayList<String>();
		Set<String> set = serviceManager.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext())
			list.add(it.next());

		return list;
	}

	// public static void main(String[] args) throws Exception {
	// SchedulerManager.setScheduler("Test", new BaseScheduler() {
	// public void run() {
	// System.out.println("test");
	// }
	// });
	// SchedulerManager.start("Test");
	// System.out.println("ok");
	// }

}