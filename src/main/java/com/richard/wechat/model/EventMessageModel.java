package com.richard.wechat.model;
/**
* @author RichardYao richardyao@tvunetworks.com
* @date 2018年1月22日 下午1:31:30
*/
public class EventMessageModel extends SendMessageModel {

	private String event;

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}
}
