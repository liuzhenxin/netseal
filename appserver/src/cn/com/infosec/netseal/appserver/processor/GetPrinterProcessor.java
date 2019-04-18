package cn.com.infosec.netseal.appserver.processor;

import java.util.List;
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

@Component("GetPrinterProcessor")
@Scope("prototype")
public class GetPrinterProcessor extends BaseProcessor {

	@Autowired
	private PrinterServiceImpl printerService;

	public GetPrinterProcessor() {
		super("GetPrinterProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String printerName = getValue(reqdata, Constants.PRINTER_NAME);
		String printerPwd = getValue(reqdata, Constants.PRINTER_PWD);

		// 检查数据值有效性
		checkParamValue(Constants.PRINTER_NAME, printerName, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.PRINTER_PWD, printerPwd, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);

		// 数据是否已存在
		List<Printer> printerList = printerService.getPrinter(printerName);
		if (printerList.size() == 0)
			throw new NetSealRuntimeException(ErrCode.PRINTER_NOT_EXIST_IN_DB, "printer data not exist");
		if (printerList.size() >= 2)
			throw new NetSealRuntimeException(ErrCode.PRINT_NOT_UNIQUE_IN_DB, "printer data more than one");
		Printer printer = printerList.get(0);
		// 校验信息
		printerService.isModify(printer);
		
		if (!CryptoHandler.hashEnc64(printerPwd).equals(printer.getPassword()))
			throw new NetSealRuntimeException(ErrCode.VERIFY_PASSWORD_FAILED, "password verify false");

		pro.setProperty(Constants.PRINTER_NUM, getValue(String.valueOf(printer.getPrintedNum())));
		return getSucceedResponse();
	}
}
