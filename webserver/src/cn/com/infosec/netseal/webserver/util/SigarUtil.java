package cn.com.infosec.netseal.webserver.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.log.LoggerUtil;

public class SigarUtil {

	public static SigarUtil sigarUtil;
	public static Sigar sigar;

	private SigarUtil() {
	}

	public synchronized static SigarUtil getInstance() {
		if (sigar == null) {
			String path = System.getProperty("java.library.path");
			if (!path.contains(Constants.NATIVELIB_PATH))
				System.setProperty("java.library.path", path + ":" + Constants.NATIVELIB_PATH);
			sigar = new Sigar();
		}

		if (sigarUtil == null)
			sigarUtil = new SigarUtil();

		return sigarUtil;
	}

	private synchronized void close() {
		if (sigar != null)
			sigar.close();
		sigar = null;
	}

	/**
	 * 获取系统信息
	 * 
	 * @return
	 */
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		Map<String, String> envInfoMap = System.getenv();
		String computerName = envInfoMap.get("COMPUTERNAME");// 获取计算机名
		dataMap.put("computerName", computerName);
		try {
			// CPU使用率
			CpuPerc cpuPercAll = sigar.getCpuPerc();
			double cpuPer = cpuPercAll.getUser() + cpuPercAll.getSys();
			dataMap.put("cpuPer", String.format("%.2f", cpuPer * 100));

			// 内存使用率
			Mem mem = sigar.getMem();
			double memPer = (double) mem.getActualUsed() / (double) mem.getTotal();
			dataMap.put("memPer", String.format("%.2f", memPer * 100));

			// Swap分区使用率
			Swap swap = sigar.getSwap();
			double swapPer = (double) swap.getUsed() / (double) swap.getTotal();
			dataMap.put("swapPer", String.format("%.2f", swapPer * 100));

			// 硬盘使用率
			FileSystem fslist[] = sigar.getFileSystemList();
			long total = 0;
			long used = 0;
			for (int i = 0; i < fslist.length; i++) {
				FileSystem fs = fslist[i];
				FileSystemUsage usage = null;
				try {
					usage = sigar.getFileSystemUsage(fs.getDirName());
				} catch (Exception e) {
					continue;
				}
				if (fs.getType() == 2) {
					total += usage.getTotal();
					used += usage.getUsed();
				}
			}
			double usagePer = (double) used / (double) total;
			dataMap.put("usagePer", String.format("%.2f", usagePer * 100));

		} catch (Exception e) {
			LoggerUtil.errorlog("getDataMap error,", e);
			close();
		}
		return dataMap;
	}

	/**
	 * 获取网络信息
	 * 
	 * @return
	 */
	public Map<String, String> getNetInfoMap() {
		Map<String, String> netInfoMap = new HashMap<String, String>();
		try {
			// 网络信息
			String nIfNames[] = sigar.getNetInterfaceList();
			for (int i = 0; i < nIfNames.length; i++) {
				String name = nIfNames[i];
				NetInterfaceConfig nIfConfig = sigar.getNetInterfaceConfig(name);
				if ((nIfConfig.getFlags() & 1L) <= 0L) {
					continue;
				}
				String address = nIfConfig.getAddress();
				netInfoMap.put(name, address);
			}

		} catch (SigarException e) {
			LoggerUtil.errorlog("getNetInfoMap error", e);
			close();
		}
		return netInfoMap;
	}

	/**
	 * 获取系统数据
	 * 
	 * @return
	 * @throws SigarException
	 */
	public ModelAndView getSystemData() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			FileSystem fslist[] = sigar.getFileSystemList(); // 磁盘信息
			FileSystemUsage usage = null;
			List<String> nameList = new ArrayList<>();
			List<Long> totalList = new ArrayList<>();
			List<Long> usedList = new ArrayList<>();
			for (int i = 0; i < fslist.length; i++) {
				FileSystem fs = fslist[i];
				String name = fs.getDevName();
				usage = sigar.getFileSystemUsage(fs.getDirName());
				long total = usage.getTotal() / 1024L;
				if (total > 0) {
					nameList.add(name);
					totalList.add(usage.getTotal() / 1024L);
					usedList.add(usage.getUsed() / 1024L);
				}
			}

			CpuPerc cpu = sigar.getCpuPerc(); // cpu 信息
			DecimalFormat df = new DecimalFormat("######0.00");

			Mem mem = sigar.getMem(); // 内存信息
			Swap swap = sigar.getSwap(); //交换区信息
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String systime = sdf2.format(new Date(System.currentTimeMillis()));
			resultMap.put("mem_Tused", mem.getActualUsed() /1024L / 1024L);
			resultMap.put("mem_Tfree", mem.getActualFree() /1024L / 1024L);
			resultMap.put("mem_Sused", swap.getUsed() /1024L / 1024L);
			resultMap.put("mem_Sfree", swap.getFree() /1024L / 1024L);
			resultMap.put("cpu_sys", df.format(cpu.getSys() * 100));
			resultMap.put("cpu_use", df.format(cpu.getUser() * 100));
			resultMap.put("systime", systime);
			resultMap.put("nameList", nameList);
			resultMap.put("totalList", totalList);
			resultMap.put("usedList", usedList);

		} catch (SigarException e) {
			LoggerUtil.errorlog("getSystemData error", e);
			close();
		}

		return new ModelAndView(new MappingJacksonJsonView(), resultMap);
	}

}
