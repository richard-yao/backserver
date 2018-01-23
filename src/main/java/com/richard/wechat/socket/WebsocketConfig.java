package com.richard.wechat.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
* @author RichardYao richardyao@tvunetworks.com
* @date 2018年1月22日 下午2:31:43
*/
@Configuration
@EnableWebSocket
public class WebsocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	public WebsocketConfig() {}
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(systemWebSocketHandler(), "/websocket").setAllowedOrigins("*").addInterceptors(new DefaultHandshakeInterceptor());
	}

	@Bean
	public WebSocketHandler systemWebSocketHandler() {
		return new SystemWebSocketHandler();
	}
}
