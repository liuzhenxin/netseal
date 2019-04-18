package cn.com.infosec.netseal.webserver.controller.log;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.config.ConfigVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.manager.ServiceManager;

@RequestMapping(value = "/logConfig")
@Controller
public class LogConfigController extends BaseController {
	@Autowired
	private HttpServletRequest request;
	/**
	 * 日志配置跳转
	 * @return
	 */
	@RequestMapping(value = "logConfig")
	public String logConfig() {
		ConfigUtil config = ConfigUtil.getInstance();
		request.setAttribute("config", config);
		return "log/logConfig";
	}

	/**
	 * 保存日志配置
	 * @param config
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "logConfigSave")
	@SealLog(optype = Constants.LOG_OPTYPE_LOGCONFIG)
	public ModelAndView logConfigSave(ConfigVO configVO) throws Exception {
		try {
			ConfigUtil.getInstance().saveLogConfig(configVO);
			boolean result = ServiceManager.reloadConfig();
			if(!result)
				throw new WebDataException("保存成功, 同步配置出错, 详细信息查看日志");
		
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}

		return getModelAndView(getSuccMap("保存成功, 配置实时生效"));
	}

}
