package cn.com.infosec.netseal.webserver.service.cert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.vo.CertVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;

@Service
public class CertServiceImpl extends BaseService {

	@Autowired
	protected CertDaoImpl certDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	@Autowired
	private CertDataDaoImpl certDataDao;

	/**
	 * 添加证书信息
	 * 
	 * @param certVO
	 */
	public void insertCert(CertVO certVO) {
		Cert cert = new Cert();
		BeanUtils.copyProperties(certVO, cert);
		certDao.insertCert(cert);
	}

	/**
	 * 根据certDN获取证书信息
	 * 
	 * @param certDN
	 * @return
	 */
	public List<CertVO> getCert(String certDN) {
		List<Cert> certList = certDao.getCert(certDN);
		List<CertVO> certVOList = new ArrayList<CertVO>();
		for (Cert cert : certList) {
			CertVO certVO = new CertVO();
			BeanUtils.copyProperties(cert, certVO);
			certVOList.add(certVO);
		}
		return certVOList;
	}
    
	/**
	 * 根据certDN、certUsage获取证书信息
	 * 
	 * @param certDN
	 * @param certUsage
	 * @return
	 */
	public List<CertVO> getCert(String certDn,Integer certUsage ) {
		List<Cert> certList = certDao.getCert(certDn,certUsage);
		List<CertVO> certVOList = new ArrayList<CertVO>();
		for (Cert cert : certList) {
			CertVO certVO = new CertVO();
			BeanUtils.copyProperties(cert, certVO);
			certVOList.add(certVO);
		}
		return certVOList;
	}
	
	/**
	 * 根据证书id获取证书信息
	 * 
	 * @param id
	 * @return
	 */
	public CertVO getCert(Long id) {
		try {
			Cert cert = certDao.getCert(id);
			if (cert == null)
				return null;
			CertVO certVO = new CertVO();
			BeanUtils.copyProperties(cert, certVO);
			return certVO;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 删除证书
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteCert(Long id) throws Exception {
		certDao.deleteCert(id);
		// 增加删除记录
		idDeleteDao.insertIDDelete(id, Constants.TABLE_SEAL_CERT);
	}

	/**
	 * 根据id查询证书集合
	 * 
	 * @param id
	 * @return
	 */
	public List<CertVO> getCertByUserId(Long id) {
		List<Cert> certList = certDao.getCertByUserId(id);
		List<CertVO> certVOList = new ArrayList<CertVO>();
		//Set<String> set = new TreeSet<String>();
		for (Cert cert : certList) {
			//if(!set.contains(cert.getCertDn())) {
				CertVO certVO = new CertVO();
				BeanUtils.copyProperties(cert, certVO);
				certVOList.add(certVO);
				//set.add(cert.getCertDn());
			//}
		}

		return certVOList;
	}
	
	/**
	 * 根据id查询证书集合
	 * 
	 * @param id
	 * @return
	 */
	public List<CertVO> getCertByUserId(Long id, Integer certUsage) {
		List<Cert> certList = certDao.getCertByUserId(id,certUsage);
		List<CertVO> certVOList = new ArrayList<CertVO>();
		
		for (Cert cert : certList) {
			CertVO certVO = new CertVO();
			BeanUtils.copyProperties(cert, certVO);
			certVOList.add(certVO);
		}

		return certVOList;
	}

	/**
	 * 根据管理员id查询证书集合
	 * 
	 * @param id
	 * @return
	 */
	public List<CertVO> getCertBySysUserId(Long id) {
		List<Cert> certList = certDao.getCertBySysUserId(id);
		List<CertVO> certVOList = new ArrayList<CertVO>();
		for (Cert cert : certList) {
			CertVO certVO = new CertVO();
			BeanUtils.copyProperties(cert, certVO);
			certVOList.add(certVO);
		}

		return certVOList;
	}

	/**
	 * 管理员证书统计
	 * 
	 * @return
	 */
	public int sysUserCertCount() {
		return certDao.sysUserCertCount();
	}

	/**
	 * 签章人证书统计
	 * 
	 * @return
	 */
	public int userCertCount() {
		return certDao.userCertCount();
	}

	/**
	 * 校验证书信息
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public boolean isModify(long id) throws Exception {
		Cert cert = certDao.getCert(id);
		return isModify(cert);
	}

	/**
	 * 初始化证书文件
	 * 
	 * @param certPath
	 * @param certDataId
	 * @return
	 * @throws Exception
	 */
	public byte[] getCertData(String certPath, Long certDataId) throws Exception {
		byte[] data = new byte[0];
		if (FileUtil.checkPath(certPath)) {
			data = FileUtil.getFile(certPath);
		} else {
			CertData certData = certDataDao.getCertData(certDataId);
			if (certData == null)
				throw new WebDataException("数据库中证书数据不存在");

			// 校验证书数据信息
			isModify(certData, "证书数据表 id 为 :" + certData.getId(), "CertData which id is " + certData.getId());
			
			data = certData.getData();
			FileUtil.storeFile(certPath, data);
		}

		return data;
	}

}
