package cn.com.infosec.netseal.webserver.service.menu;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.dao.menu.MenuDaoImpl;
import cn.com.infosec.netseal.common.entity.po.Menu;
import cn.com.infosec.netseal.common.entity.vo.MenuVO;
import cn.com.infosec.netseal.webserver.service.BaseService;

@Service
public class MenuServiceImpl extends BaseService{
	@Autowired
	protected MenuDaoImpl menuDao;

	/**
	 * 查询菜单集合
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<MenuVO> getMenu() throws Exception {
		List<Menu> menuList = menuDao.getMenu();
		List<MenuVO> menuVOList = new ArrayList<MenuVO>();
		for(Menu menu : menuList){
			MenuVO menuVO = new MenuVO();
			BeanUtils.copyProperties(menu, menuVO);
			
			// 校验信息
			isModify(menu, menuVO);
			
			menuVOList.add(menuVO);
		}
		return menuVOList;
	}
	
	/**
	 * 查询父级菜单
	 * 
	 * @param pid
	 * @return
	 */
	public List<MenuVO> getMenuByPid(Long pid) {
		if(pid!=null){
			List<Menu> menuList = menuDao.getMenuByPid(pid);
			List<MenuVO> menuVOList = new ArrayList<MenuVO>();
			for(Menu menu : menuList){
				MenuVO menuVO = new MenuVO();
				BeanUtils.copyProperties(menu, menuVO);
				menuVOList.add(menuVO);
			}
			return menuVOList;
		}else{
			return new ArrayList<MenuVO>();
		}
	}
	
	/**
	 * 获取所有菜单集合
	 * 
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public List<MenuVO> getMenuByIds(String ids) throws Exception {
		if(ids!=null){
			List<Menu> menuList = menuDao.getMenuByIds(ids);
			List<MenuVO> menuVOList = new ArrayList<MenuVO>();
			for(Menu menu : menuList){
				MenuVO menuVO = new MenuVO();
				BeanUtils.copyProperties(menu, menuVO);
				
				// 校验菜单信息
				isModify(menu, "菜单表" + menu.getName(), "Menu which id is " + menu.getId() + " and name is " + menu.getName());
				
				menuVOList.add(menuVO);
			}
			return menuVOList;
		}else{
			return new ArrayList<MenuVO>();
		}
	}

}
