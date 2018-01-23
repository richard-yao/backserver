package com.richard.wechat.service;
/**
* @author RichardYao richardyao@tvunetworks.com
* @date 2018年1月22日 下午2:57:28
*/

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richard.wechat.model.UserMessageInstance;
import com.richard.wechat.socket.SystemWebSocketHandler;

@Service("websocketMessageSendJob")
public class WebsocketMessageSendJob {

	private Logger logger = LoggerFactory.getLogger(WebsocketMessageSendJob.class);
	
	@Autowired
	private WechatInfoManageService wechatInfoManage;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private boolean executeSendMessage = false;
	/**
	 * Send message to websocket
	 */
	public void sendMessage() {
		List<WebSocketSession> sessionList = SystemWebSocketHandler.establishSessions;
		if(sessionList.size() > 0) {
			List<UserMessageInstance> messageList = wechatInfoManage.getAllMessage();
			for(WebSocketSession temp : sessionList) {
				if(temp.isOpen()) {
					if(messageList.size() > 0) {
						executeSendMessage = true;
						String message = null;
						for(UserMessageInstance msg : messageList) {
							try {
								message = mapper.writeValueAsString(msg);
								temp.sendMessage(new TextMessage(message));
							} catch (JsonProcessingException e) {
								logger.error(e.getMessage(), e);
							} catch (IOException e1) {
								logger.error(e1.getMessage(), e1);
							}
						}
						executeSendMessage = false;
					}
				}
			}
		}
	}
	
	/**
	 * Send heartbeat to keepalive
	 */
	public void sendHeartBeat() {
		if(!executeSendMessage) { // do not allow two different thread write message in the same time
			List<WebSocketSession> sessionList = SystemWebSocketHandler.establishSessions;
			if(sessionList.size() > 0) {
				for(WebSocketSession temp : sessionList) {
					if(temp.isOpen()) {
						try {
							temp.sendMessage(new TextMessage("heartbeat"));
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
		}
	}
}
