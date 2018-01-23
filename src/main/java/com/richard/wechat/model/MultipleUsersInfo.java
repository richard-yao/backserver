package com.richard.wechat.model;

import java.util.List;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title MultipleUsersInfo
 * @todo TODO
 */

public class MultipleUsersInfo extends ErrorMessage {

	private List<UserBasicInfo> user_info_list;

	public List<UserBasicInfo> getUser_info_list() {
		return user_info_list;
	}

	public void setUser_info_list(List<UserBasicInfo> user_info_list) {
		this.user_info_list = user_info_list;
	}
}
