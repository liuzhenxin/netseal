package cn.com.infosec.netseal.webserver.controller.count;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.infosec.netseal.common.entity.vo.AccessLogCountVO;
import cn.com.infosec.netseal.webserver.service.accessLog.AccessLogServiceImpl;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.webserver.service.user.UserServiceImpl;
import cn.com.infosec.netseal.webserver.util.CommonUtil;

@RequestMapping(value = "/count")
@Controller
public class countController {

	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private AccessLogServiceImpl accessLogService;
	@Autowired
	private CertServiceImpl certServiceImpl;

	/**
	 * 统计主页
	 */

	@RequestMapping(value = "toCount")
	public String toCount() {
		return "count/count";
	}

	/**
	 * 签章人统计
	 * 
	 * @return
	 */
	@RequestMapping(value = "/userCount")
	public String userCount() {
		int userTotalCount = userService.getUserTotalCount();// 获取总的签章人数量
		int userCount = sealService.getSealUserCount();// 获取制发印章的签章人

		request.setAttribute("userTotalCount", userTotalCount);
		request.setAttribute("userCount", userCount);

		return "count/userCount";
	}

	/**
	 * 印章统计
	 * 
	 * @param searchTime
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/sealCount")
	public String sealCount(String searchTime) throws ParseException {
		long startTime = -1;
		if (searchTime != null && !"".equals(searchTime)) {
			startTime = CommonUtil.timeStrStart(searchTime);
		}
		int sealNo = sealService.getSealCount(startTime);
		request.setAttribute("sealNo", sealNo);

		return "count/sealCount";
	}

	/**
	 * 条件查询
	 * 
	 * @param page
	 * @param sysUser
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/sealCountSearch")
	public Map<String, Object> sealCountSearch(String searchTime) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		long startTime = -1;
		if (searchTime != null && !"".equals(searchTime)) {
			startTime = CommonUtil.timeStrStart(searchTime);
		}

		int sealNo = sealService.getSealCount(startTime);
		modelMap.put("sealNo", sealNo);
		return modelMap;
	}

	/**
	 * 业务统计
	 */

	@RequestMapping(value = "accessLogCount")
	public String accessLogCount() throws Exception {
		List<AccessLogCountVO> accessLogCountVOList = accessLogService.getAccessLogCount();
		request.setAttribute("accessLogCountList", accessLogCountVOList);
		return "count/accessLogCount";
	}

	/**
	 * 证书统计
	 * 
	 * @return
	 */
	@RequestMapping(value = "/certCount")
	public String certCount() {
		int userCount = certServiceImpl.userCertCount();
		int sysUserCount = certServiceImpl.sysUserCertCount();

		request.setAttribute("userCount", userCount);
		request.setAttribute("sysUserCount", sysUserCount);

		return "count/certCount";
	}

}
