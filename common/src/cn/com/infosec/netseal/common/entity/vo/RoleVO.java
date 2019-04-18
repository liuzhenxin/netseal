package cn.com.infosec.netseal.common.entity.vo;

import java.util.ArrayList;
import java.util.List;

public class RoleVO extends BaseVO {

	private String name;
	private String menuId;

	// 获取角色下面的菜单
	private List<MenuVO> menus = new ArrayList<MenuVO>();

	public List<MenuVO> getMenus() {
		return menus;
	}

	public void setMenus(List<MenuVO> menus) {
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

}
