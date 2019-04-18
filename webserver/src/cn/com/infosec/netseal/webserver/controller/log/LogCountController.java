package cn.com.infosec.netseal.webserver.controller.log;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 日志统计
 * 
 */
@RequestMapping(value = "/logCount")
@Controller
public class LogCountController {

	@RequestMapping(value = "logCount")
	public String logCount() {
		return "log/logCount";
	}

	@RequestMapping(value = "accessLog")
	public String accessLog() {
		return "log/accessLog";
	}

	@RequestMapping(value = "manageLog")
	public String manageLog() {
		return "log/manageLog";
	}

	@RequestMapping(value = "operateLog")
	public String operateLog() {
		return "log/operateLog";
	}

}
