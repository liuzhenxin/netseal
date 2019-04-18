package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 系统菜单
 */
public class Menu extends Base {

	private Long pid;
	private String name;
	private String url;
	private String img;

	// 扩展
	private List<Menu> child = new ArrayList<Menu>(); // 子菜单
	private boolean checked = false;

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public List<Menu> getChild() {
		return child;
	}

	public void setChild(List<Menu> child) {
		this.child = child;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(name));
			baos.write(calMac(url));
			baos.write(calMac(pid));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
			baos.write(calMac(img));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	} 
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(getId());
		sb.append(" name:").append(name);
		sb.append(" url:").append(url);
		sb.append(" pid:").append(pid);
		sb.append(" generateTime:").append(getGenerateTime());
		sb.append(" updateTime:").append(getUpdateTime());
		sb.append(" mac:").append(getMac());
		
		return sb.toString();
	}
	
}
