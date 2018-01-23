package com.richard.wechat.model;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title ErrorMessage
 * @todo TODO
 */

public class ErrorMessage {

	private int errcode;
	private String errmsg;
	
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
}
