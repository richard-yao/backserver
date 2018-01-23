package com.richard.wechat.model;
/**
* @author RichardYao richardyao@tvunetworks.com
* @date 2018年1月22日 上午11:23:13
*/
public class UserMessageInstance {

	private String nickname;
	private String headimgurl;
	private String content;
	private String picUrl;
	
	public String getNickname() {
		return nickname;
	}
	public String getHeadimgurl() {
		return headimgurl;
	}
	public String getContent() {
		return content;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
}
