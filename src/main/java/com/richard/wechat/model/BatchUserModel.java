package com.richard.wechat.model;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title GetBatchUserModel
 * @todo TODO
 */

public class BatchUserModel extends ErrorMessage {

	private int total;
	private int count;
	private DataModel data;
	private String next_openid;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public DataModel getData() {
		return data;
	}
	public void setData(DataModel data) {
		this.data = data;
	}
	public String getNext_openid() {
		return next_openid;
	}
	public void setNext_openid(String next_openid) {
		this.next_openid = next_openid;
	}
}