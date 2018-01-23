package com.richard.wechat.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title ConfigurationUtil
 * @todo TODO
 */
@Component("configUtil")
public class ConfigurationUtil {
	
	@Value("${getTokenUrl}")
	private String getTokenUrl;
	@Value("${getBatchUserUrl}")
	private String getBatchUserUrl;
	@Value("${getBatchUserInfoUrl}")
	private String getBatchUserInfoUrl;
	@Value("${wechatAppId}")
	private String wechatAppId;
	@Value("${wechatAppSecret}")
	private String wechatAppSecret;
	@Value("${serverCheckToken}")
	private String serverCheckToken;
	@Value("${wechatAccount}")
	private String wechatAccount;
	@Value("${getSingleUserInfoUrl}")
	private String getSingleUserInfoUrl;
	
	public String getGetTokenUrl() {
		return getTokenUrl;
	}
	public void setGetTokenUrl(String getTokenUrl) {
		this.getTokenUrl = getTokenUrl;
	}
	public String getGetBatchUserUrl() {
		return getBatchUserUrl;
	}
	public void setGetBatchUserUrl(String getBatchUserUrl) {
		this.getBatchUserUrl = getBatchUserUrl;
	}
	public String getGetBatchUserInfoUrl() {
		return getBatchUserInfoUrl;
	}
	public void setGetBatchUserInfoUrl(String getBatchUserInfoUrl) {
		this.getBatchUserInfoUrl = getBatchUserInfoUrl;
	}
	public String getWechatAppId() {
		return wechatAppId;
	}
	public void setWechatAppId(String wechatAppId) {
		this.wechatAppId = wechatAppId;
	}
	public String getWechatAppSecret() {
		return wechatAppSecret;
	}
	public void setWechatAppSecret(String wechatAppSecret) {
		this.wechatAppSecret = wechatAppSecret;
	}
	public String getServerCheckToken() {
		return serverCheckToken;
	}
	public void setServerCheckToken(String serverCheckToken) {
		this.serverCheckToken = serverCheckToken;
	}
	public String getWechatAccount() {
		return wechatAccount;
	}
	public void setWechatAccount(String wechatAccount) {
		this.wechatAccount = wechatAccount;
	}
	public String getGetSingleUserInfoUrl() {
		return getSingleUserInfoUrl;
	}
	public void setGetSingleUserInfoUrl(String getSingleUserInfoUrl) {
		this.getSingleUserInfoUrl = getSingleUserInfoUrl;
	}
}
