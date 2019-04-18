package cn.com.infosec.netseal.webserver.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface SealInterface {

	@WebMethod
	@WebResult(name = "jsonResponse")
	public String pdfStamp(@WebParam(name = "jsonRequest") String request);
}
