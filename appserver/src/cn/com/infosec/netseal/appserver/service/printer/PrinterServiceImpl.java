package cn.com.infosec.netseal.appserver.service.printer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.common.dao.printer.PrinterDaoImpl;
import cn.com.infosec.netseal.common.entity.po.Printer;

@Service
public class PrinterServiceImpl extends BaseService{

	@Autowired
	private PrinterDaoImpl printerDao;

	public List<Printer> getPrinter(String name) {
		List<Printer> printerList = printerDao.getPrinter(name);
		return printerList;
	}

	public void insertPrinter(Printer printer) {
		printerDao.insertPrinter(printer);
	}
	
	/**
	 * 校验信息
	 * 
	 * @param printer
	 * @param msg
	 */
	public void isModify (Printer printer) {
		isModify(printer, "the data of Printer which id is " + printer.getId() + " and name is " + printer.getName());
	}
}
