package cn.com.infosec.netseal.common.scheduler;

public abstract class BaseScheduler implements Runnable {

	private String name = "BASE";
	private long initialDelay = 60;// 首次执行的延迟时间
	private long interval = 300; // 一次执行终止和下一次执行开始之间的延迟
	private long processNum = 0; // 处理交易数
	private long completeTime = 0; // 完成任务时间

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public long getProcessNum() {
		return processNum;
	}

	public void setProcessNum(long processNum) {
		this.processNum = processNum;
	}

	public long getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(long completeTime) {
		this.completeTime = completeTime;
	}

}
