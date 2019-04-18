package cn.com.infosec.netseal.webserver.network;

import java.util.Date;

public class LogFileItem {

	String fileName = "";
	double size = 0L;
	Date lastModify = null;
	
	public String getFileName()
	{
		return this.fileName;
	}
	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public double getSize()
	{
		return this.size;
	}
	
	public void setSize(double size)
	{
		this.size = size;
	}
	
	public Date getLastModify()
	{
		return this.lastModify;
	}
	
	public void setLastModify(Date lastModify)
	{
		this.lastModify = lastModify;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �Զ���ɷ������

	}

}
