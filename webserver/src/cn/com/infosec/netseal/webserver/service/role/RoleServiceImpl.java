package cn.com.infosec.netseal.webserver.service.role;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.dao.menu.MenuDaoImpl;
import cn.com.infosec.netseal.common.dao.role.RoleDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Menu;
import cn.com.infosec.netseal.common.entity.po.Role;
import cn.com.infosec.netseal.common.entity.vo.RoleVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;

@Service
public class RoleServiceImpl extends BaseService{
	@Autowired
	protected RoleDaoImpl roleDao;
	@Autowired
	private MenuDaoImpl menuDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	
	/**
	 * 添加角色信息
	 * 
	 * @param roleVO
	 */
	public void insertRole(RoleVO roleVO) {
		Role role=new Role();
		BeanUtils.copyProperties(roleVO, role);
		roleDao.insertRole(role);
	}
	
	/**
	 * 查询角色
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public RoleVO getRole(Long id) throws Exception {
		Role role = roleDao.getRole(id);
		if(role == null)
			return null;
		RoleVO roleVO = new RoleVO();
		BeanUtils.copyProperties(role, roleVO);
		
		// 校验角色信息
		isModify(role, "角色表" + role.getName(), "Role which id is " + role.getId() + " and name is " + role.getName());
		
		return roleVO;
	}
	
	/**
	 * 删除角色
	 * @param id
	 * @throws Exception
	 */
	public void deleteRole(Long id) throws Exception {
		roleDao.deleteRole(id);
		// 增加删除记录
		idDeleteDao.insertIDDelete(id, Constants.TABLE_SEAL_ROLE);
	}
	
	/**
	 * 更改角色信息
	 * @param role
	 * @throws Exception
	 */
	public void updateRole(Role role) throws Exception{
		int r=roleDao.updateRole(role);
		if(r==0)
			throw new WebDataException("更新角色失败");
		
	}
	
	/**
	 * 查询角色信息集合
	 * 
	 * @return
	 */
	public List<RoleVO> getRole() {
		List<Role> roleList=roleDao.getRoles();
		List<RoleVO> roleVOList=new ArrayList<RoleVO>();
		for(Role role:roleList){
			RoleVO roleVO=new RoleVO();
			BeanUtils.copyProperties(role, roleVO);
			roleVOList.add(roleVO);
		}
		return roleVOList;
	}
	
	
	/**
	 * 授权
	 * @param roleVO
	 * @param menuIds
	 * @throws Exception 
	 */
	public void roleMenu(Long id, String menuIds) throws Exception {
		Role role = roleDao.getRole(id);
		isModify(role, "角色表" + role.getName(), "Role which id is " + role.getId() + " and name is " + role.getName());
		
		// 校验菜单信息
		List<Menu> meneList = menuDao.getMenuByIds(menuIds);
		for (Menu menu : meneList) 
			isModify(menu, "菜单表 " + menu.getName(), "Menu which id is " + menu.getId() + " and name is " + menu.getName());
		
		long update_time = DateUtil.getCurrentTime();
		role.setUpdateTime(update_time);
		role.setMenuId(menuIds);
		String roleNewMac = role.calMac();
		int r = roleDao.roleMenu(id, menuIds, update_time, roleNewMac);
		if(r == 0)
			throw new WebDataException("授权失败");
		
	}
		
}
