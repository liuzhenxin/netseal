package cn.com.infosec.netseal.webserver.controller.role;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.MenuVO;
import cn.com.infosec.netseal.common.entity.vo.RoleVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.menu.MenuServiceImpl;
import cn.com.infosec.netseal.webserver.service.role.RoleServiceImpl;

@RequestMapping(value = "/role")
@Controller
public class RoleController extends BaseController{
	@Autowired
	private  HttpServletRequest request;
	@Autowired
	private MenuServiceImpl menuService;
	@Autowired
	private RoleServiceImpl roleService;
	

	/**
	 * 角色显示跳转
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="toEditRoleMenu")
	public String toEditRoleMenu(Long id) throws Exception{
		List<MenuVO> allMenu = menuService.getMenu();
		request.setAttribute("allMune", allMenu);
		
		RoleVO role = roleService.getRole(id);
		if (role != null) {
			String menuIds = role.getMenuId();
			List<MenuVO> menuList = menuService.getMenuByIds(menuIds);
			//如果子级被选中父也应该被选中
			boolean ck = false;
			for (MenuVO menus :menuList) {
				//获取当前父级权限下的所有子级权限
				for (MenuVO child : allMenu) {
					for (MenuVO sy_any : menuList) {
						if(child.getId().equals(sy_any.getId())){
							child.setChecked(true);
							ck = true;
						}
					}
				}
				menus.setChecked(ck);
				ck = false;
			}
		}
		request.setAttribute("role", role);
		return "role/roleEdit";
	}
	
	/**
	 * 角色授权
	 * @param id
	 * @param menuIds
	 * @return
	 * @throws Exception 
	 */
	@SealLog(optype=Constants.LOG_OPTYPE_EDITROLE)
	@RequestMapping(value="editRoleMenu")
	public ModelAndView editRoleMenu(Long id, String [] menuIds) throws Exception{
		try {
			String menuId = "";
			if (menuIds != null) {
				menuId = Arrays.toString(menuIds).replace("[", "").replace("]", "");
			}
			roleService.roleMenu(id, menuId);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("角色授权成功"));
	}
	
	//查询所有角色
	@RequestMapping(value = "roleList")
	public String searchRole(){
		List<RoleVO> list = roleService.getRole();
		request.setAttribute("list", list);
		return "role/roleList";
	}
	
}
