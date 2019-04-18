package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.define.Constants;

/**
 * 公司
 */
public class CompanyVO extends BaseVO {

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

	public String getMac() {
		return "";
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
		sb.append(getId());
		sb.append(" ");
		sb.append(pid);
		sb.append(" ");
		sb.append(name);
		sb.append(" ");
		sb.append(isParent);
		sb.append(" ");
		sb.append(treeId);
		sb.append(" ");
		sb.append(remark);
		return sb.toString();
	}

}
