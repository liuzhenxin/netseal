package cn.com.infosec.netseal.webserver.util;

import java.util.List;

public class Page<T> {
	private int pageNo = 1;// 当前页
	private int pageSize = 10;// 每页条数
	private int totalNo;// 总条数
	private int totalPage;// 总页数
	private String sord;// 排序方式 asc desc
	private String sidx;// 排序字段名
	private String search;
	private List<T> result;// 结果数据

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public int getTotalNo() {
		return totalNo;
	}

	public void setTotalNo(int totalNo) {

		if (totalNo % pageSize == 0) {
			totalPage = totalNo / pageSize;
		} else {
			totalPage = totalNo / pageSize + 1;
		}

		this.totalNo = totalNo;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public int getStart() {
		return (pageNo - 1) * pageSize;
	}

	public int getEnd() {
		return pageNo * pageSize;
	}

	public String getSord() {
		return sord;
	}

	public void setSord(String sord) {
		this.sord = sord;
	}

	public String getSidx() {
		return sidx;
	}

	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

}
