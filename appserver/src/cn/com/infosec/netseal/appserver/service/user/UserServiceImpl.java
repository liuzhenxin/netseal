package cn.com.infosec.netseal.appserver.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.count.CountDaoImpl;
import cn.com.infosec.netseal.common.dao.user.UserDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.po.Count;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;

@Service
public class UserServiceImpl extends BaseService {

	@Autowired
	private UserDaoImpl userDao;
	@Autowired
	private CertDaoImpl certDao;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private CountDaoImpl countDao;

	/**
	 * 查单条
	 * 
	 * @param ID
	 * @return
	 */
	public User getUser(long id) {
		User user = userDao.getUser(id);
		return user;
	}

	/**
	 * 查单条
	 * 
	 * @param ID
	 * @return
	 */
	public List<User> getUser(String name, long companyID) {
		List<User> userList = userDao.getUser(name, companyID);
		return userList;
	}

	/**
	 * 查单条
	 * 
	 * @param ID
	 * @return
	 */
	public List<User> getUser(String name, long companyID, long certID) {
		List<User> userList = userDao.getUser(name, companyID, certID);
		return userList;
	}

	/**
	 * 增
	 * 
	 * @param seal
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertUser(User user, List<Cert> certList, boolean insertUser) {
		// 判断证书数量是否超限
		long limit = ConfigUtil.getInstance().getCheckCertNumLimit();
		Count count = new Count();
		count.setName(Constants.CERT_NUM);
		count.setLocation(ConfigUtil.getInstance().getTablePrefixId());
		List<Count> countList = countDao.getCount(count);
		
		Count c = countList.get(0);
		// 校验证书数量信息
		isModify(c, "the data of Count which name is " + c.getName());
		
		long certNum = c.getNum() + certList.size();
		if (certNum > limit) 
			throw new NetSealRuntimeException(ErrCode.CERT_NUM_OVER_LIMIT, "user cert num over limit");
		
		if (insertUser)
			userDao.insertUser(user);
		
		for (int i = 0; i < certList.size(); i++) {
			Cert cert =certList.get(i);
			String fileName = FileUtil.getFileName();
			String certPath = Constants.CERT_PATH + DateUtil.getDateDir() + fileName + Constants.CERT_SUFFIX;
			CertData certData = cert.getCertData();
			certDataDao.insertCertData(certData);			
			
			cert.setCertDataId(certData.getId());
			cert.setUserId(user.getId());
			cert.setCertPath(certPath);
			cert.setNotBefor(0L);
			cert.setNotAfter(0L);
			cert.setUserId(user.getId());
			cert.setSysUserId(-1L);
			certDao.insertCert(cert);
		}
		c.setNum(c.getNum() + certList.size());
		int n = countDao.updateCountPlus(c, limit);
		if( n == 0 )
			throw new NetSealRuntimeException(ErrCode.CERT_NUM_OVER_LIMIT, "user cert num over limit");
		
		//存储证书文件
		for (Cert cert : certList)
			FileUtil.storeFile(cert.getCertPath(), cert.getCertData().getData());
		
	}

	/**
	 * 校验证书DN
	 * 
	 * @param account
	 * @param certDn
	 * @return
	 */
	public boolean checkUserCertDn(User user, String certDn) {
		Long userId = user.getId();
		if (userId == null)
			return false;

		List<Cert> list = certDao.getCertByUserId(userId);
		for (int i = 0; i < list.size(); i++) {
			String dn = list.get(i).getCertDn();
			if (dn.equals(certDn))
				return true;
		}

		return false;
	}
	
	public void isModify(User user){
		isModify(user, "the data of User which id is " + user.getId() + " and name is " + user.getName());
	}
}
