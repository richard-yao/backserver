package com.richard.wechat.model;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title ImageMessageModel
 * @todo TODO
 */

public class ImageMessageModel extends SendMessageModel {

	private String picUrl;
	private String mediaId;

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
}
