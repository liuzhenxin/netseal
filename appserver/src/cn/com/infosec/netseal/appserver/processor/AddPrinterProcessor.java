package cn.com.infosec.netseal.appserver.processor;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.service.printer.PrinterServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Printer;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.DateUtil;

@Component("AddPrinterProcessor")
@Scope("prototype")
public class AddPrinterProcessor extends BaseProcessor {

	@Autowired
	private PrinterServiceImpl printerService;

	public AddPrinterProcessor() {
		super("AddPrinterProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String printerName = getValue(reqdata, Constants.PRINTER_NAME);
		String sealName = getValue(reqdata, Constants.SEAL_NAME);
		String sealType = getValue(reqdata, Constants.SEAL_TYPE);
		String sealUser = getValue(reqdata, Constants.USER_NAME);
		String printerNum = getValue(reqdata, Constants.PRINTER_NUM);
		String printerLimit = getValue(reqdata, Constants.PRINTER_LIMIT);
		String printerPwd = getValue(reqdata, Constants.PRINTER_PWD);

		// 检查数据值有效性
		checkParamValue(Constants.PRINTER_NAME, printerName, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.SEAL_NAME, sealName, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.SEAL_TYPE, sealType, Constants.PARAM_TYPE_INT);
		checkParamValue(Constants.USER_NAME, sealUser, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.PRINTER_NUM, printerNum, Constants.PARAM_TYPE_INT);
		checkParamValue(Constants.PRINTER_LIMIT, printerLimit, Constants.PARAM_TYPE_INT);
		checkParamValue(Constants.PRINTER_PWD, printerPwd, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);

		// 创建POJO类
		Printer printer = new Printer();
		printer.setName(printerName);
		printer.setSealName(sealName);
		printer.setSealType(Integer.parseInt(sealType));
		printer.setUserName(sealUser);
		printer.setPrintNum(Integer.parseInt(printerNum));
		printer.setPrintedNum(Integer.parseInt(printerNum));
		printer.setPassword(CryptoHandler.hashEnc64(printerPwd));
		printer.setGenerateTime(DateUtil.getCurrentTime());
		printer.setUpdateTime(DateUtil.getCurrentTime());
		// 数据是否已存在
		if (printerService.getPrinter(printerName).size() > 0)
			throw new NetSealRuntimeException(ErrCode.PRINTER_HAS_EXIST_IN_DB, "printer data already exist");

		// 保存数据
		printerService.insertPrinter(printer);
		return getSucceedResponse();
	}
}
