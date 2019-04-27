package cn.com.infosec.netseal.webserver.controller.system.ha;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.OperateLogVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.network.LogFileItem;
import cn.com.infosec.netseal.webserver.network.Network;
import cn.com.infosec.netseal.webserver.network.NetworkUtil;
import cn.com.infosec.netseal.webserver.network.WFWConstant;
import cn.com.infosec.netseal.webserver.network.ha.HAInfo;
import cn.com.infosec.netseal.webserver.network.ha.HAManager;
import cn.com.infosec.netseal.webserver.util.Page;

/**
 *
 */
@RequestMapping(value = "/system/ha")
@Controller
public class HaController  extends BaseController {

	@Autowired
	private HttpServletRequest request;

	/**
	 * HA管理
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "haManage")
	public String haManage(HttpServletRequest request) {

		return "system/ha/haManage";
	}

	/**
	 * HA配置
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "haConfig")
	public String haConfig(HttpServletRequest request) {
		String os = System.getProperties().getProperty("os.name");
		if (os == null || os.toLowerCase().indexOf("linux") == -1) {
			request.setAttribute("message", "此功能只支持Linux操作系统使用");
			return "system/ha/haConfig";
		}
		String message = "";
		try {
			Network network = new Network();
			// hosts文件列表
			HashMap<String, String> hostNameMap = network.getHostNames();
			HAInfo haInfo = HAManager.getHAInfo();
			String mainNodeDeviceName = haInfo.getMainNodeDeviceName();
			ArrayList<String> nodeList = haInfo.getNodeList();// 主机和备机名称
			int size = hostNameMap.size();
			String[] ports = new String[size];
			String[] values = (String[]) hostNameMap.values().toArray(ports);
			// 匹配主机节点名称在hosts文件中存在
			String hostname = "";
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals(mainNodeDeviceName)) {
						hostname = values[i];
						break;
					} else {
						continue;
					}
				}

			}
			String masterNodeName = "";
			if (StringUtil.isNotBlank(hostname)) {
				masterNodeName = hostname;
			} else {
				message = "HA配置的主机节点名称:【" + mainNodeDeviceName + "】错误,hosts文件中没有与之对应的节点名称或配置错误";
				request.setAttribute("message", message);
				return "system/ha/haConfig";
			}
			// 匹配备机节点名称在hosts文件中存在
			String slaveNodeName = "";
			hostname = "";
			String nodeName2 = String.valueOf(nodeList.get(1));
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals(nodeName2)) {
						hostname = values[i];
					} else {
						continue;
					}
				}

			}
			if (StringUtil.isNotBlank(hostname)) {
				slaveNodeName = hostname;
			} else {
				message = "HA配置的备机节点名称:【" + nodeName2 + "】错误,hosts文件中没有与之对应的节点名称或配置错误";
				request.setAttribute("message", message);
				return "system/ha/haConfig";
			}

			// 虚IP
			String vmIP = haInfo.getVmIP();
			int netMaskValue = haInfo.getNetMaskValue();
			String netMask = NetworkUtil.parseMaskValue(netMaskValue);

			ArrayList<String> netWorkNameList = network.getNetWorkCardNameList();
			String bindNetWorkName = "";
			String ipDevice = haInfo.getIPDevice();
			ipDevice = ipDevice.split(" ")[0].trim();
			ipDevice = ipDevice.substring(0, ipDevice.length() - 2);
			for (int i = 0; i < netWorkNameList.size(); i++) {
				String netWorkName = netWorkNameList.get(i).trim();
				if (StringUtil.isBlank(netWorkName)) {
					continue;
				}

				if (ipDevice.equals(netWorkName)) {
					bindNetWorkName = netWorkName;
					break;
				}
			}
			// 网口健康检查
			String checkNetWorkName = "";
			String haDevice = haInfo.getHADevice();
			for (int i = 0; i < netWorkNameList.size(); i++) {
				String netWorkName = netWorkNameList.get(i).trim();
				if (StringUtil.isBlank(netWorkName)) {
					continue;
				}
				haDevice = haDevice.split(" ")[0].trim();
				if (haDevice.equals(netWorkName)) {// ?
					checkNetWorkName = netWorkName;
					break;
				}
			}

			// 过滤掉虚网口
			netWorkNameList.remove(bindNetWorkName + ":0");
			request.setAttribute("masterNodeName", masterNodeName);// 主机节点名称
			request.setAttribute("slaveNodeName", slaveNodeName);// 备机节点名称
			request.setAttribute("hostNameMap", hostNameMap);
			request.setAttribute("mainNodeDeviceName", mainNodeDeviceName);
			request.setAttribute("nodeList", nodeList);

			request.setAttribute("vmIP", vmIP);// 虚IP地址
			request.setAttribute("netMask", netMask);// 子网掩码
			request.setAttribute("netWorkNameList", netWorkNameList);// 所有网口
			request.setAttribute("bindNetWorkName", bindNetWorkName);// 绑定网口名称
			request.setAttribute("checkNetWorkName", checkNetWorkName);// 网口健康检查

		} catch (WebDataException e) {
			message = e.getMessage();
		} catch (Exception e) {
			message = "读取配置信息错误,错误内容查看日志";
			LoggerUtil.errorlog("读取配置信息错误", e);
		}
		request.setAttribute("message", message);
		return "system/ha/haConfig";
	}

	/**
	 * HA配置保存
	 * 
	 * @param request
	 * @param haInfo
	 * @return
	 */
	@RequestMapping(value = "haConfigSave")
	@SealLog(optype = Constants.LOG_OPTYPE_HACONFIGSAVE)
	public ModelAndView haConfigSave(HttpServletRequest request, HAInfo haInfo) {
		String message = "";
		boolean success = false;
		try {
			String netMask = haInfo.getNetmask();
			// 检验
			success = NetworkUtil.isNetMask(netMask);
			if (success) {
				String haDevice = haInfo.getHADevice();
				String slaveNodeName = haInfo.getSlaveNodeName();
				String mainNodeDeviceName = haInfo.getMainNodeDeviceName();
				String nodeName = null;// 对方服务器名
				String hostName = NetworkUtil.getHostName();// 当前服务器名
				if (slaveNodeName.equals(hostName)) {
					nodeName = mainNodeDeviceName;
				} else if (mainNodeDeviceName.equals(hostName)) {
					nodeName = slaveNodeName;
				}
				Network network = new Network();
				HashMap<String, String> hostNames = network.getHostNames();
				boolean temp = false;
				for (String ip : hostNames.keySet()) {
					String value = hostNames.get(ip);
					if (value.equals(nodeName)) {
						haDevice += " " + ip;
						temp = true;
						break;
					}

				}
				if (!temp) {
					throw new WebDataException("hosts文件配置有错误");
				}

				haInfo.setHADevice(haDevice);
				ArrayList<String> nodeList = new ArrayList<String>();// 主和备列表
				nodeList.add(haInfo.getMainNodeDeviceName());
				nodeList.add(haInfo.getSlaveNodeName());
				haInfo.setNodeList(nodeList);
				int netMaskValue = NetworkUtil.getNetMaskValue(netMask);
				haInfo.setNetMaskValue(netMaskValue);
				HAInfo info2 = HAManager.setHAInfo(haInfo);

				if (info2 == null) {
					message = "HA配置不成功!";
				} else {
					message = "HA配置成功!";
				}

			} else {
				message = "子网掩码不合法!";
			}
		} catch (WebDataException e) {
			message = e.getMessage();
		} catch (Exception e) {
			LoggerUtil.errorlog("HA配置错误", e);
			message = "HA配置错误";
		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	@RequestMapping(value = "haService")
	public String haService(HttpServletRequest request) {
		String message = "";
		String status = "";
		try {
			boolean serverStatus = HAManager.queryServerStatus();
			if (serverStatus) {
				status = "start";
				message = "服务正在运行";
			} else {
				status = "stop";
				message = "服务已停止";
			}
		} catch (Exception e) {
			LoggerUtil.errorlog("查看HA服务错误", e);
			message = e.getMessage();
		}
		request.setAttribute("message", message);
		request.setAttribute("status", status);
		return "system/ha/haService";
	}

	@RequestMapping(value = "haServiceUpdate")
	@SealLog(optype = Constants.LOG_OPTYPE_HASERVICEUPDATE)
	public ModelAndView haServiceUpdate(String status) {
		boolean success = false;
		String message = "";
		try {
			if ("stop".equals(status)) {
				success = HAManager.stopServer();
				if (success)
					message = "停止操作成功!";
				else
					message = "停止操作失败!";
			} else if ("start".equals(status)) {
				success = HAManager.startServer();
				if (success)
					message = "启动操作成功!";
				else
					message = "启动操作失败!";

			} else {
				message = "无效的操作类型!";
			}

		} catch (Exception e) {
			message = e.getMessage();
			LoggerUtil.errorlog("启动/停止HA服务错误", e);
		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	@RequestMapping(value = "haLog")
	public String haLog(HttpServletRequest request, Page<OperateLogVO> page) {
		ArrayList logList = HAManager.getAllLogInfo();
		int total = logList.size();
		int start = page.getStart();
		int end = page.getEnd();
		List<OperateLogVO> list = new ArrayList<OperateLogVO>();
		for (int i = 0; i < total; i++) {
			LogFileItem lgi = (LogFileItem) logList.get(i);
			if (i >= start && i <= end) {
				OperateLogVO log = new OperateLogVO();
				String fileName = lgi.getFileName();
				String[] strArray = fileName.split("/");
				fileName = strArray[strArray.length - 1];
				log.setFileName(fileName);
				log.setFilePath(lgi.getFileName());
				long size = Math.round(lgi.getSize());
				log.setFileSize(size / 1024);
				log.setFileTime(lgi.getLastModify().getTime());
				list.add(log);
			}
		}
		page.setTotalNo(total);
		page.setResult(list);

		request.setAttribute("page", page);

		return "system/ha/haLog";
	}

	@RequestMapping(value = "hostsConfig")
	public String hostsConfig(HttpServletRequest request) {
		try {
			Network network = new Network();
			String hostsConfig = network.getHosts(WFWConstant.HOSTSFILE);
			request.setAttribute("hostsConfig", hostsConfig.trim());
		} catch (Exception e) {

		}
		return "system/ha/hostsConfig";
	}

	@RequestMapping(value = "hostsConfigSave")
	@SealLog(optype = Constants.LOG_OPTYPE_HOSTSCONFIGSAVE)
	public ModelAndView hostsConfigSave(HttpServletRequest request, String hostsConfig) {
		if (hostsConfig.indexOf("127.0.0.1") == -1)// 默认添加
			hostsConfig += "\n127.0.0.1 localhost";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		Network n = new Network();
		try {
			n.writeToHosts(hostsConfig);
			message = "hosts配置成功!";
		} catch (Exception e) {
			success = false;
			message = "hosts配置失败, " + e.getMessage();
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		return this.getModelAndView(resultMap);
	}

	/**
	 * HA下载日志文件
	 * 
	 * @param fileName
	 * @param request
	 * @param response  
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DOWNHAFILE)
	@RequestMapping(value = "haDownFile")
	public void haDownFile(String fileName, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtil.isBlank(fileName))
			return;

		String filePath = WFWConstant.HA_LOG_PATH;
		File targetFile = new File(filePath);

		if (!targetFile.exists())
			return;

		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);

		FileInputStream fis = null;
		OutputStream os = null;
		try {
			filePath = filePath + fileName;
			fis = new FileInputStream(filePath);
			os = response.getOutputStream();

			int length = 0;
			byte[] b = new byte[1024];
			while ((length = fis.read(b)) > 0) {
				os.write(b, 0, length);
			}

		} catch (Exception e) {
			LoggerUtil.errorlog("download file error, ", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * HA查看日志文件内容
	 * 
	 * @param fileName
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "haViewFile")
	public String haViewFile(String tab, String fileName, Page<String> page) throws Exception {
		request.setAttribute("fileName", fileName);
		request.setAttribute("tab", tab);// 显示tab页id

		if (StringUtil.isBlank(fileName))
			return "system/ha/haViewFile";

		String filePath = WFWConstant.HA_LOG_PATH;
		File targetFile = new File(filePath);

		if (!targetFile.exists())
			return "system/ha/haViewFile";

		filePath = filePath + fileName;
		page.setPageSize(100);
		int start = page.getStart();
		int end = page.getEnd();

		String ret = FileUtil.getSpecifyLine(filePath, start, end);
		int total = FileUtil.getFileLineNum(filePath);

		List<String> list = new ArrayList<String>();
		list.add(ret);
		page.setTotalNo(total);
		page.setResult(list);

		request.setAttribute("page", page);
		return "system/ha/haViewFile";
	}


}
