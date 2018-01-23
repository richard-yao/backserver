package com.richard.wechat.socket;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
* @author RichardYao richardyao@tvunetworks.com
* @date 2018年1月22日 下午2:40:56
*/
public class SystemWebSocketHandler implements WebSocketHandler {

	private Logger logger = LoggerFactory.getLogger(SystemWebSocketHandler.class);
	
	public static List<WebSocketSession> establishSessions = new ArrayList<WebSocketSession>();
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		logger.info("Remove this WebsocketSession: {}", session.getId());
		establishSessions.remove(session);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		establishSessions.add(session);
		session.sendMessage(new TextMessage("success"));
		logger.info("Websocket established, now the number is {}", establishSessions.size());
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> wsm) throws Exception {
		logger.info("Message content: {} --- {}", session.getId(), wsm.getPayload());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
		if(throwable.getMessage().equals("java.util.concurrent.ExecutionException: java.net.SocketException: Software caused connection abort: socket write error")) {
			logger.info("Close connection normally!");
		} else {
			logger.error("Lost connection!");
		}
		if(session.isOpen()) {
			session.close();
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

}
