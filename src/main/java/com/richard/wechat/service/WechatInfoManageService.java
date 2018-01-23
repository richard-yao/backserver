package com.richard.wechat.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richard.wechat.model.BatchUserModel;
import com.richard.wechat.model.EventMessageModel;
import com.richard.wechat.model.ImageMessageModel;
import com.richard.wechat.model.MultipleUsersInfo;
import com.richard.wechat.model.SendMessageModel;
import com.richard.wechat.model.TextMessageModel;
import com.richard.wechat.model.UserBasicInfo;
import com.richard.wechat.model.UserMessageInstance;
import com.richard.wechat.util.ConfigurationUtil;
import com.richard.wechat.util.FrequentUseParas;
import com.richard.wechat.util.HttpsUtil;
import com.richard.wechat.util.MyUtil;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title WechatInfoManageService
 * @todo TODO
 */
@Service("wechatInfoManage")
public class WechatInfoManageService {

	private Logger logger = LoggerFactory.getLogger(WechatInfoManageService.class);
	private String accessToken;
	private Map<String, UserBasicInfo> subscribeUserMap = new HashMap<String, UserBasicInfo>();
	private List<Object> allMessageList = new ArrayList<Object>();
	
	@Autowired
	private ConfigurationUtil configUtil;
	
	public void refreshToken() {
		logger.info("refreshToken task start!");
		String apppId = configUtil.getWechatAppId();
		String appSecret = configUtil.getWechatAppSecret();
		String getTokenUrl = configUtil.getGetTokenUrl() + "?grant_type=client_credential&appid={0}&secret={1}";
		getTokenUrl = getTokenUrl.replace("{0}", apppId);
		getTokenUrl = getTokenUrl.replace("{1}", appSecret);
		try {
			String result = HttpsUtil.sendSSLGetRequest(getTokenUrl);
			if(result != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				HashMap<String, Object> resultJson = objectMapper.readValue(result, HashMap.class);
				if(resultJson != null && resultJson.containsKey("access_token") && resultJson.containsKey("expires_in")) {
					accessToken = resultJson.get("access_token").toString();
					logger.info("Get access token successfully! The access_token is {}", accessToken);
				} else {
					logger.error("Cannot get access_token, the error info {}", result);
				}
			}
			logger.info("refreshToken task is over!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void refreshUserInfo() {
		logger.info("refreshUserInfo task start!");
		if(accessToken == null) {
			refreshToken();
		} 
		if(accessToken != null) {
			List<UserBasicInfo> subscribeUserList = new ArrayList<UserBasicInfo>();
			List<String> openIds = new ArrayList<String>();
			String getUserUrl = configUtil.getGetBatchUserUrl() + "?access_token={0}&next_openid=";
			getUserUrl = getUserUrl.replace("{0}", accessToken);
			try {
				String result = HttpsUtil.sendSSLGetRequest(getUserUrl);
				if(result != null) {
					ObjectMapper objectMapper = new ObjectMapper();
					BatchUserModel users = objectMapper.readValue(result, BatchUserModel.class);
					if(users != null ) {
						if(users.getErrcode() == 0 && users.getErrmsg() == null) {
							if(users.getNext_openid() != null && users.getTotal() > users.getCount()) { // all user's number over 10 thousand
								openIds.addAll(getRemainUser(getUserUrl, users.getNext_openid()));
							} else { // all users info has got
								openIds.addAll(Arrays.asList(users.getData().getOpenid()));
							}
							if(openIds.size() > 0) {
								getSubscribeUsersDetailInfo(openIds, subscribeUserList);
								if(subscribeUserList.size() > 0) {
									subscribeUserMap.clear();
									for(UserBasicInfo temp : subscribeUserList) {
										subscribeUserMap.put(temp.getOpenid(), temp);
									}
								}
							}
						} else {
							logger.error("Get batch user info failed, the error code: {}, error info: {}", users.getErrcode(), users.getErrmsg());
						}
					} else {
						logger.error("Cannot convert the result: {}", result);
					}
				}
				logger.info("refreshUserInfo task is over! The total subscribe user's number: {}", openIds.size());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Intercept the long list and send reques to wechat for users' information
	 * @param openIds
	 */
	private void getSubscribeUsersDetailInfo(List<String> openIds, List<UserBasicInfo> subscribeUserList) {
		if(openIds != null) {
			if(openIds.size() > FrequentUseParas.MAX_BATCH_GET_NUMBER) {
				List<String> tempOpenIds = openIds.subList(0, FrequentUseParas.MAX_BATCH_GET_NUMBER);
				requestWechatToGetUserInfo(tempOpenIds, subscribeUserList);
				getSubscribeUsersDetailInfo(openIds.subList(FrequentUseParas.MAX_BATCH_GET_NUMBER, openIds.size()), subscribeUserList);
			} else {
				requestWechatToGetUserInfo(openIds, subscribeUserList);
			}
		}
	}
	
	/**
	 * Request user's basic information with specify openIds
	 * @param openIds
	 */
	private void requestWechatToGetUserInfo(List<String> openIds, List<UserBasicInfo> subscribeUserList) {
		HashMap<String, List<HashMap<String, String>>> postMap = new HashMap<String, List<HashMap<String, String>>>();
		List<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < openIds.size(); i++) {
			HashMap<String, String> tempData = new HashMap<String, String>();
			tempData.put("openid", openIds.get(i));
			tempData.put("lang", "zh_CN");
			listData.add(tempData);
		}
		postMap.put("user_list", listData);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String postJson = mapper.writeValueAsString(postMap);
			String accessUrl = configUtil.getGetBatchUserInfoUrl() + "?access_token={0}";
			accessUrl = accessUrl.replace("{0}", accessToken);
			String responseResult = HttpsUtil.sendSSLJsonPost(accessUrl, postJson);
			if(responseResult != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					MultipleUsersInfo usersInfo = objectMapper.readValue(responseResult, MultipleUsersInfo.class);
					if(usersInfo != null) {
						if(usersInfo.getErrcode() == 0 && usersInfo.getErrmsg() == null) {
							subscribeUserList.addAll(usersInfo.getUser_info_list());
						} else {
							logger.error("Get batch user info failed, the error code: {}, error info: {}", usersInfo.getErrcode(), usersInfo.getErrmsg());
						}
					} else {
						logger.error("Cannot convert the result: {}", responseResult);
					}
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Query all subscribe users information and get them openIds
	 * @param originalUserUrl
	 * @param nextOpenId
	 * @return
	 */
	private List<String> getRemainUser(String originalUserUrl, String nextOpenId) {
		String getUserUrl = originalUserUrl + nextOpenId;
		try {
			String result = HttpsUtil.sendSSLGetRequest(getUserUrl);
			if(result != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				BatchUserModel users = objectMapper.readValue(result, BatchUserModel.class);
				if(users != null ) {
					if(users.getErrcode() == 0 && users.getErrmsg() == null) { 
						if(users.getNext_openid() == null || users.getNext_openid().equals("")) { // get all users now
							return Arrays.asList(users.getData().getOpenid());
						} else {
							List<String> openIds = Arrays.asList(users.getData().getOpenid());
							openIds.addAll(getRemainUser(originalUserUrl, users.getNext_openid()));
							return openIds;
						}
					} else {
						logger.error("Get batch user info failed, the error code: {}, error info: {}", users.getErrcode(), users.getErrmsg());
					}
				} else {
					logger.error("Cannot convert the result: {}", result);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<String>();
	}
	
	public synchronized SendMessageModel dealUserMessage(String msg) {
		Object msgRecord = convertStringToMsg(msg);
		if(msgRecord instanceof TextMessageModel) {
			allMessageList.add(msgRecord);
		} else if(msgRecord instanceof ImageMessageModel) {
			allMessageList.add(msgRecord);
		} else if(msgRecord instanceof EventMessageModel) {
			final EventMessageModel event = (EventMessageModel) msgRecord;
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					checkNewSubscribeUserInfo(event);
				}
			});
			thread.start();
		}
		return (SendMessageModel) msgRecord;
	}
	
	/**
	 * Convert wechat send message to Object instance
	 * @param msg
	 * @return
	 */
	private Object convertStringToMsg(String msg) {
		SendMessageModel messageModel = null;
		try {
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(msg.getBytes()));
			if(doc.getElementsByTagName("xml").item(0) != null){
				Node content = doc.getElementsByTagName("xml").item(0);
				if(content.getChildNodes() != null && content.getChildNodes().getLength() > 0) {
					NodeList childNodes = content.getChildNodes();
					for(int i=0;i<childNodes.getLength();i++) {
						Node temp = childNodes.item(i);
						if(temp.getNodeName().equals("MsgType")) {
							String msgType = temp.getFirstChild().getNodeValue();
							// msgType = MyUtil.convertXmlCdata(msgType);
							if(msgType.equals("text")) {
								messageModel = new TextMessageModel();
								break;
							} else if(msgType.equals("image")) {
								messageModel = new ImageMessageModel();
								break;
							} else if(msgType.equals("event")) {
								messageModel = new EventMessageModel();
								break;
							} else {
								messageModel = new SendMessageModel();
								break;
							}
						}
					}
					for(int i=0;i<childNodes.getLength();i++) {
						Node temp = childNodes.item(i);
						String nodeName = temp.getNodeName();
						String nodeVal = temp.getFirstChild().getNodeValue();
						// nodeVal = MyUtil.convertXmlCdata(nodeVal);
						String methodStr = "set" + nodeName;
						Method method;
						try {
							method = MyUtil.getUnitClass(messageModel.getClass(), methodStr);
							method.setAccessible(true);
							method.invoke(messageModel, nodeVal);
						} catch (NoSuchMethodException e) {
							logger.error("Cannot get this method! {}", nodeName);
						} catch (IllegalAccessException e) {
							logger.error("Cannot access this method! {}", nodeName);
						} catch (IllegalArgumentException e) {
							logger.error("Illegal argument, method name {}, value {}", methodStr, nodeName);
						} catch (InvocationTargetException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
		} catch (ParserConfigurationException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (SAXException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (IOException e1) {
			logger.error(e1.getMessage(), e1);
		}
		return messageModel;
	}
	
	/**
	 * When new subscriber trigger subscribe event, get the only user info
	 * @param eventMsg
	 */
	private void checkNewSubscribeUserInfo(EventMessageModel eventMsg) {
		if(eventMsg.getEvent().equals("subscribe")) {
			String openId = eventMsg.getFromUserName();
			String accessUrl = configUtil.getGetSingleUserInfoUrl() + "?access_token={0}&openid={1}&lang=zh_CN";
			accessUrl = accessUrl.replace("{0}", accessToken);
			accessUrl = accessUrl.replace("{1}", openId);
			String result;
			try {
				result = HttpsUtil.sendSSLGetRequest(accessUrl);
				if(result != null) {
					ObjectMapper objectMapper = new ObjectMapper();
					UserBasicInfo user = objectMapper.readValue(result, UserBasicInfo.class);
					if(user != null && user.getOpenid() != null) {
						logger.info("New subscriber: {}", user.getNickname());
						subscribeUserMap.put(user.getOpenid(), user);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public List<UserBasicInfo> getAllUserInfo() {
		List<UserBasicInfo> allInfo = new ArrayList<UserBasicInfo>();
		allInfo.addAll(subscribeUserMap.values());
		return allInfo;
	}
	
	public synchronized List<UserMessageInstance> getAllMessage() {
		List<UserMessageInstance> allMessage = new ArrayList<UserMessageInstance>();
		if(allMessageList.size() > 0) {
			for(Object temp : allMessageList) {
				UserMessageInstance tempMsg = new UserMessageInstance();
				String openId = null;
				if(temp instanceof TextMessageModel) {
					TextMessageModel textMsg = (TextMessageModel) temp;
					tempMsg.setContent(textMsg.getContent());
					openId = textMsg.getFromUserName();
				} else if(temp instanceof ImageMessageModel) {
					ImageMessageModel imgMsg = (ImageMessageModel) temp;
					tempMsg.setPicUrl(imgMsg.getPicUrl());
					openId = imgMsg.getFromUserName();
				}
				if(openId != null && subscribeUserMap.containsKey(openId)) {
					UserBasicInfo tempUser = subscribeUserMap.get(openId);
					tempMsg.setNickname(tempUser.getNickname());
					tempMsg.setHeadimgurl(tempUser.getHeadimgurl());
				} else {
					logger.info("Message with null nickName, content: {}, image: {}", tempMsg.getContent(), tempMsg.getPicUrl());
				}
				allMessage.add(tempMsg);
			}
		}
		allMessageList.clear();
		return allMessage;
	}
}
