package cn.com.infosec.netseal.webserver.controller.system.network;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.network.DNSManager;
import cn.com.infosec.netseal.webserver.network.Network;
import cn.com.infosec.netseal.webserver.network.NetworkCard;
import cn.com.infosec.netseal.webserver.network.NetworkUtil;

/**
 *
 */
@RequestMapping(value = "/system/network")
@Controller
public class NetworkController  extends BaseController {

	@Autowired
	private HttpServletRequest request;
	

	/**
	 * 网络配置
	 * 
	 * @return
	 */
	@RequestMapping(value = "networkList")
	public String networkList() {
		Vector<NetworkCard> networkCardList = new Vector<NetworkCard>();
		Vector<NetworkCard> networkCardListNew = new Vector<NetworkCard>();

		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			networkCardList = new Network().getNetWorkCardInfo();
		} else {
			request.setAttribute("msg", "此功能只支持Linux操作系统使用");
		}

		for (int i = 0; i < networkCardList.size(); i++) {
			if (!networkCardList.get(i).getName().endsWith(":0")) {
				networkCardListNew.add(networkCardList.get(i));
			}
		}
		request.setAttribute("networkCardList", networkCardListNew);

		return "system/network/networkList";
	}

	/**
	 * 网口设置 读取本机网络设置
	 * 
	 * @return
	 */
	@RequestMapping(value = "networkCardConfig")
	public String networkCardConfig(String name) {
		Network network = new Network();
		NetworkCard networkCard = new NetworkCard();
		try {
			Vector<NetworkCard> list = network.getNetWorkCardInfo();
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					NetworkCard card = (NetworkCard) list.get(i);
					String tempName = card.getName().trim();
					if (tempName.equalsIgnoreCase(name)) {
						networkCard = card;
						break;
					}
				}
			}

		} catch (Exception e) {
			LoggerUtil.errorlog("读取网络配置错误", e);
		}
		request.setAttribute("networkCard", networkCard);
		return "system/network/networkCardConfig";

	}

	/**
	 * 网口设置保存
	 * 
	 * @param networkCard
	 * @return
	 */
	@RequestMapping(value = "networkCardConfigSave")
	@SealLog(optype = "networkCardConfigSave")
	public ModelAndView networkCardConfigSave(NetworkCard networkCard) {
		boolean success = false;
		String message = "";
		String mask = networkCard.getMask();
		String ip = networkCard.getIp();
		String ipv6 = networkCard.getIpv6();
		
		Network network = new Network();
		try {
			success = NetworkUtil.isNetMask(mask);
			if (success) {
				network.setNetworkCardInfoNotConfigWithIpv6(networkCard.getName(), ip, mask, ipv6);
				message = "设置网卡成功";
			} else {
				message = "设置网卡失败,子网掩码错误";
			}
		} catch (Exception e) {
			LoggerUtil.errorlog("设置网卡错误", e);
			message = "设置网卡错误";

		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * 网络配置 默认网关设置
	 * 
	 * @return
	 */
	@RequestMapping(value = "networkGatewayConfig")
	public String networkGatewayConfig() {
		Network network = new Network();
		try {
			List<String> list = network.getGateWayInfo();
			String gatewayIp = list.get(0);
			request.setAttribute("gatewayIp", gatewayIp);
			String gatewayIpv6 = list.get(1);
			request.setAttribute("gatewayIpv6", gatewayIpv6);
		} catch (Exception e) {
			LoggerUtil.errorlog("读取网关配置错误", e);
		}
		return "system/network/networkGatewayConfig";
	}

	/**
	 * 网关设置保存
	 * 
	 * @param networkCard
	 * @return
	 */
	@RequestMapping(value = "networkGatewayConfigSave")
	@SealLog(optype = "networkGatewayConfigSave")
	public ModelAndView networkGatewayConfigSave(String gatewayIp, String gatewayIpv6) {
		boolean success = true;
		String message = "";
		Network network = new Network();
		try {
			network.setGageWayInfo(gatewayIp, gatewayIpv6);
			message = "设置网关成功,重启网关服务生效";

		} catch (Exception e) {
			LoggerUtil.errorlog("设置网关失败", e);
			success = false;
			message = "设置网关失败";

		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * 网络配置 DNS设置
	 * 
	 * @return
	 */
	@RequestMapping(value = "networkDNSConfig")
	public String networkDNSConfig(String name) {
		NetworkCard networkCard = new NetworkCard();
		try {
			DNSManager dnsManage = new DNSManager();
			Vector<String> dnsIpList = dnsManage.getNameServerList();

			request.setAttribute("dnsIpList", dnsIpList);
		} catch (Exception e) {
			LoggerUtil.errorlog("读取网络DNS错误", e);
		}
		request.setAttribute("networkCard", networkCard);
		return "system/network/networkDNSConfig";

	}

	/**
	 * DNS设置 添加
	 * 
	 * @return
	 */
	@RequestMapping(value = "networkDNSAdd")
	public String networkDNSAdd() {
		return "system/network/networkDNSAdd";
	}

	/**
	 * DNS设置保存 添加
	 * 
	 * @param dnsIp
	 * @return
	 */
	@RequestMapping(value = "networkDNSAddSave")
	@SealLog(optype = "networkDNSAddSave")
	public ModelAndView networkDNSConfigSave(String dnsIp) {
		boolean success = false;
		String message = "";
		try {
			DNSManager dnsManage = new DNSManager();
			Vector<String> dnsIpList = dnsManage.getNameServerList();

			if (!dnsIpList.contains(dnsIp)) {
				dnsIpList.add(dnsIp);
				dnsManage.setNameServerInfo(dnsIpList);
				success = true;
				message = "添加DNS成功";
			} else {
				message = "添加DNS失败,已存在该DNS服务器配置";
			}

		} catch (Exception e) {
			LoggerUtil.errorlog("添加DNS错误", e);
			message = "添加DNS错误";

		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * DNS设置删除
	 * 
	 * @param networkCard
	 * @return
	 */
	@RequestMapping(value = "networkDNSDelete")
	@SealLog(optype = "networkDNSDelete")
	public ModelAndView networkDNSDelete(String dnsIp) {
		boolean success = false;
		String message = "";
		try {
			DNSManager dnsManage = new DNSManager();
			Vector<String> dnsIpList = dnsManage.getNameServerList();
			if (dnsIpList.contains(dnsIp)) {
				dnsManage.deleteNameServer(dnsIp);
				success = true;
				message = "删除DNS成功";
			} else {
				message = "删除DNS失败,不存在该DNS服务器配置";
			}

		} catch (Exception e) {
			LoggerUtil.errorlog("删除DNS错误", e);
			message = "删除DNS错误";

		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * DNS设置修改
	 * 
	 * @param dnsIp
	 * @return
	 */
	@RequestMapping(value = "networkDNSEdit")
	public String networkDNSEdit(String dnsIp) {

		request.setAttribute("dnsIp", dnsIp);
		return "system/network/networkDNSEdit";
	}

	/**
	 * DNS设置修改 保存
	 * 
	 * @param networkCard
	 * @return
	 */
	@RequestMapping(value = "networkDNSEditSave")
	@SealLog(optype = "networkDNSEditSave")
	public ModelAndView networkDNSEditSave(String oldDNSIp, String dnsIp) {
		boolean success = false;
		String message = "";
		try {
			DNSManager dnsManage = new DNSManager();
			Vector<String> dnsIpList = dnsManage.getNameServerList();

			if (dnsIpList.contains(oldDNSIp)) {
				int locate = dnsIpList.indexOf(oldDNSIp);
				dnsIpList.removeElementAt(locate);
				dnsIpList.insertElementAt(dnsIp, locate);
				dnsManage.setNameServerInfo(dnsIpList);
				success = true;
				message = "修改DNS成功";
			} else {
				message = "修改DNS失败,不存在该DNS服务器配置";
			}

		} catch (Exception e) {
			LoggerUtil.errorlog("修改DNS错误", e);
			message = "修改DNS错误";
		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}


}
