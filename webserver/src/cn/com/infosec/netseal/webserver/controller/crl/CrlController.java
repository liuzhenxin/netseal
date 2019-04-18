package cn.com.infosec.netseal.webserver.controller.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.infosec.netseal.webserver.service.crl.CrlServiceImpl;

@RequestMapping(value = "/crl")
@Controller
public class CrlController {

	@Autowired
	private CrlServiceImpl crlService;

	@RequestMapping(value = "/add")
	public String add() {
		return "";
	}
}
