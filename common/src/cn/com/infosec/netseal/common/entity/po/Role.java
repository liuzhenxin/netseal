package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class Role extends Base {

	private String name;
	private String menuId;

	// 获取角色下面的菜单
	private List<Menu> menus = new ArrayList<Menu>();

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	
	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(name));
			baos.write(calMac(menuId));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
		
	}
	
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(getId());
		sb.append(" name:").append(name);
		sb.append(" menuId:").append(menuId);
		sb.append(" generateTime:").append(getGenerateTime());
		sb.append(" updateTime:").append(getUpdateTime());
		sb.append(" mac:").append(getMac());
		
		return sb.toString();
	}
}
