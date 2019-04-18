package cn.com.infosec.netseal.webserver.controller.monitor;

import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.manager.JmxManager;
import cn.com.infosec.netseal.webserver.util.SigarUtil;

@RequestMapping(value = "/monitor")
@Controller
public class MonitorController {

	@Autowired
	private HttpServletRequest request;

	/**
	 * 跳转monitorSystem
	 * 
	 * @throws Exception
	 */
	@RequestMapping("/monitorSystem")
	public String toMonitorSystem() throws Exception {
		return "monitor/monitorSystem";
	}

	/**
	 * system 数据请求
	 * 
	 * @return
	 * @throws SigarException
	 */
	@ResponseBody
	@RequestMapping("/monitorDataSystem")
	public ModelAndView monitorDataSystem() throws Exception {
		return SigarUtil.getInstance().getSystemData();
	}

	/**
	 * 跳转monitorApp
	 */
	@RequestMapping("/monitorApp")
	public String toMonitorApp() {
		return "monitor/monitorApp";
	}

	@ResponseBody
	@RequestMapping("/MonitorDataAPP")
	public String MonitorDataAPP() throws Exception {
		Object SocketNum = JmxManager.getInstance().getAttribute(Constants.JMX_INFOSEC_OBJ_NAME, "SocketNum");
		Object PoolingC = JmxManager.getInstance().getAttribute(Constants.JMX_DRUID_OBJ_NAME, "PoolingCount");
		Object PoolingPeak = JmxManager.getInstance().getAttribute(Constants.JMX_DRUID_OBJ_NAME, "PoolingPeak");
		Object PoolingPeakTime = JmxManager.getInstance().getAttribute(Constants.JMX_DRUID_OBJ_NAME, "PoolingPeakTime");
		Object ActiveC = JmxManager.getInstance().getAttribute(Constants.JMX_DRUID_OBJ_NAME, "ActiveCount");

		MemoryUsage heapMemoryUsage = JmxManager.getInstance().getCompositeAttribute(Constants.JMX_MEMORY_OBJ_NAME, "HeapMemoryUsage");
		MemoryUsage nonheapMemoryUsage = JmxManager.getInstance().getCompositeAttribute(Constants.JMX_MEMORY_OBJ_NAME, "NonHeapMemoryUsage");
		long heapMax = heapMemoryUsage.getMax() / 1024 / 1024;
		long heapUsed = heapMemoryUsage.getUsed() / 1024 / 1024;
		long stackUsed = nonheapMemoryUsage.getUsed() / 1024 / 1024;

		String dt = PoolingPeakTime.toString();
		SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timedate = sdf2.format(sdf1.parse(dt));

		String systime = sdf2.format(new Date(System.currentTimeMillis()));

		JSONObject json = new JSONObject();
		json.put("SocketNum", SocketNum.toString());
		json.put("PoolingC", PoolingC.toString());
		json.put("PoolingPeak", PoolingPeak.toString());
		json.put("PoolingPeakTime", timedate);
		json.put("ActiveC", ActiveC.toString());
		json.put("systime", systime);
		json.put("heapMax", heapMax);
		json.put("heapUsed", heapUsed);
		json.put("stackUsed", stackUsed);

		return json.toString();
	}

	/**
	 * 跳转monitorBusiness
	 */
	@RequestMapping("/monitorBusiness")
	public String toMonitorBusiness() {
		return "monitor/monitorBusiness";
	}

	@ResponseBody
	@RequestMapping("/monitorDataBusn")
	public String monitorDataBusn() throws Exception {
		Object dealSucc = JmxManager.getInstance().getAttribute(Constants.JMX_INFOSEC_OBJ_NAME, "DealSuccNum");
		Object dealFail = JmxManager.getInstance().getAttribute(Constants.JMX_INFOSEC_OBJ_NAME, "DealFailNum");

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String systime = sdf2.format(new Date(System.currentTimeMillis()));

		JSONObject json = new JSONObject();
		json.put("dealSucc", dealSucc.toString());
		json.put("dealFail", dealFail.toString());
		json.put("systime", systime);

		return json.toString();
	}

}
