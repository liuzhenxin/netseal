package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 公司
 */
public class Company extends Base {

	private Long pid;
	private String name;
	private Integer isParent = Constants.DEFAULT_INT; // 0是叶子节点 1是父节点
	private String treeId; // 唯一编码
	private String remark;

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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
		baos.write(calMac(getId()));
		baos.write(calMac(pid));
		baos.write(calMac(name));
		baos.write(calMac(isParent));
		baos.write(calMac(treeId));
		baos.write(calMac(remark));
		baos.write(calMac(getGenerateTime()));
		baos.write(calMac(getUpdateTime()));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}

	public Integer getIsParent() {
		return isParent;
	}

	public void setIsParent(Integer isParent) {
		this.isParent = isParent;
	}

	public String getTreeId() {
		return treeId;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:");
		sb.append(getId());
		sb.append(" pid:");
		sb.append(pid);
		sb.append(" name:");
		sb.append(name);
		sb.append(" isParent:");
		sb.append(isParent);
		sb.append(" treeId:");
		sb.append(treeId);
		sb.append(" ");
		sb.append(remark);
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" mac:");
		sb.append(getMac());
		
		return sb.toString();
	}

}
