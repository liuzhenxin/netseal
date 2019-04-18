package cn.com.infosec.netseal.common.entity.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统菜单
 */
public class MenuVO extends BaseVO {

	private Long pid;
	private String name;
	private String url;
	private String img;

	// 扩展
	private List<MenuVO> child = new ArrayList<MenuVO>(); // 子菜单
	private boolean checked = false;
	private boolean sealMac;

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

	public List<MenuVO> getChild() {
		return child;
	}

	public void setChild(List<MenuVO> child) {
		this.child = child;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isSealMac() {
		return sealMac;
	}

	public void setSealMac(boolean sealMac) {
		this.sealMac = sealMac;
	}

}
