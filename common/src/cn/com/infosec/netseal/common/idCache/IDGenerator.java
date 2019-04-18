package cn.com.infosec.netseal.common.idCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.idCache.IDCacheDaoImpl;
import cn.com.infosec.netseal.common.entity.po.IDCache;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;

@Component
@Scope("prototype")
public class IDGenerator {

	@Autowired
	private IDCacheDaoImpl idCacheDao;

	private IDCache idCache;
	private String tableName;
	private String tablePrefixId = ConfigUtil.getInstance().getTablePrefixId();

	public String getTableName() {
		return tableName;
	}

	/**
	 * 表主键生成
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public synchronized long getNewId(String tableName) {
		if (this.tableName == null)
			this.tableName = tableName;

		if (idCache == null)
			idCache = getIDCache();

		long curId = idCache.getCurId();
		long endId = idCache.getEndId();

		// 缓存中ID用完,重新初始化ID
		if (curId > endId) {
			idCache = getIDCache();
			curId = idCache.getCurId();
		}

		// 为下次获取时使用，可以直接获取不用 +1 了
		idCache.setCurId(curId + 1);

		curId = Long.parseLong(tablePrefixId + String.format("%011d", curId));
		return curId;
	}

	/**
	 * 重新初始化id
	 * 
	 * @param tableName
	 *            表名
	 * @throws Exception
	 */
	private IDCache getIDCache()  {
		// 从数据库中取数据
		IDCache idCache = idCacheDao.getIDCache(tableName);
		
		if (idCache == null) {
			idCache = new IDCache();
			idCache.setTableName(tableName);
			idCacheDao.insertIDCache(idCache);
		} else {
			// 校验数据信息
			IDCache idc = idCacheDao.getIDCache(tableName);
			String idcaMac = idc.calMac();
			if (!idcaMac.equals(idc.getMac()))
				throw new NetSealRuntimeException("IDCache data information had been tampered with, the table name is " + idCache.getTableName());

		}
				
		long lastId = idCache.getLastId();
		long curId = lastId + 1;
		long endId = lastId + idCache.getBatchId();

		idCache.setLastId(endId);
		idCache.setCurId(curId);
		idCache.setEndId(endId);

		// 更新数据库
		long result = idCacheDao.updateIDCache(idCache, lastId);

		if (result == 0) {
			throw new NetSealRuntimeException("id cache error,database exist synchronized update transaction");
		}

		return idCache;
	}

}
