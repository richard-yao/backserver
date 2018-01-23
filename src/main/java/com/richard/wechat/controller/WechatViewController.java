package com.richard.wechat.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.richard.wechat.model.SendMessageModel;
import com.richard.wechat.model.UserBasicInfo;
import com.richard.wechat.model.UserMessageInstance;
import com.richard.wechat.service.WechatInfoManageService;
import com.richard.wechat.util.ConfigurationUtil;
import com.richard.wechat.util.EncryptUtil;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title WechatViewController
 * @todo TODO
 */
@Controller
public class WechatViewController {
	
	private Logger logger = LoggerFactory.getLogger(WechatViewController.class);

	@Autowired
	private WechatInfoManageService wechatInfoManage;
	@Autowired
	private ConfigurationUtil configUtil;
	
	@RequestMapping(value = "/check", method = {RequestMethod.GET})
	public @ResponseBody String getFromWechat(HttpServletRequest request,
			@RequestParam(value = "signature") String signature,
			@RequestParam(value = "timestamp") String timestamp,
			@RequestParam(value = "nonce") String nonce,
			@RequestParam(value = "echostr") String echostr) {
		logger.info("Get request from server: {}", getBrowerIpAddress(request));
		String beforeEncryptStr = nonce + timestamp + configUtil.getServerCheckToken();
		String sha1Str = EncryptUtil.SHA1(beforeEncryptStr);
		if(signature.toLowerCase().equals(sha1Str.toLowerCase())) {
			return echostr;
		} else {
			logger.error("Bad request to check api!!! The signature: {}", signature);
			return "错误请求";
		}
	}
	
	@RequestMapping(value = "/check", method = {RequestMethod.POST})
	public ResponseEntity<String> postFromWechat(HttpServletRequest request) {
		logger.info("Post request from server: {}", getBrowerIpAddress(request));
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			String inputLine = null;
			String resultData = "";
			while (((inputLine = reader.readLine()) != null)) {
				resultData += inputLine;
			}
			reader.close();
			if(!resultData.equals("")) {
				logger.info("Get post message from wechat: {}", resultData);
				StringBuffer backMsg = new StringBuffer("<xml>");
				SendMessageModel msg = wechatInfoManage.dealUserMessage(resultData);
				if(msg != null) {
					boolean eventMsg = false;
					backMsg.append("<ToUserName><![CDATA[" + msg.getFromUserName() + "]]></ToUserName>");
					backMsg.append("<FromUserName><![CDATA[" + configUtil.getWechatAccount() + "]]></FromUserName>");
					backMsg.append("<CreateTime>" + System.currentTimeMillis()/1000 + "</CreateTime>");
					backMsg.append("<MsgType><![CDATA[text]]></MsgType>");
					if(msg.getMsgType().equals("text") || msg.getMsgType().equals("image")) {
						backMsg.append("<Content><![CDATA[收到！]]></Content>");
					} else if(msg.getMsgType().equals("event")) { // 关注或者取消关注事件
						eventMsg = true;
					} else {
						backMsg.append("<Content><![CDATA[请输入文字或图片消息]]></Content>");
					}
					backMsg.append("</xml>");
					// String message = MyUtil.changeCharset(backMsg.toString(), "UTF-8");
					if(eventMsg) {
						return ResponseEntity.status(HttpStatus.SC_OK).body("");
					} else {
						return ResponseEntity.status(HttpStatus.SC_OK).body(backMsg.toString());
					}
				} else {
					return ResponseEntity.status(HttpStatus.SC_OK).body("success");
				}
			} else {
				return ResponseEntity.status(HttpStatus.SC_OK).body("success");
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("UnsupportedEncoding!");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Someting Wrong!");
		}
	}
	
	@RequestMapping(value = "/refresh/token", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<String> refreshAccessToken() {
		wechatInfoManage.refreshToken();
		return ResponseEntity.status(HttpStatus.SC_OK).body(wechatInfoManage.getAccessToken());
	}
	
	@RequestMapping(value = "/refresh/user", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<String> refreshUserInfo() {
		wechatInfoManage.refreshUserInfo();
		return ResponseEntity.status(HttpStatus.SC_OK).body("Success");
	}
	
	@RequestMapping(value = "/user", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<List<UserBasicInfo>> getUserInfo() {
		return ResponseEntity.status(HttpStatus.SC_OK).body(wechatInfoManage.getAllUserInfo());
	}
	
	@RequestMapping(value = "/message", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<List<UserMessageInstance>> getSendMessage() {
		return ResponseEntity.status(HttpStatus.SC_OK).body(wechatInfoManage.getAllMessage());
	}
	
	/**
	 * 获取客户端的ip地址
	 * @param request
	 * @return
	 */
	private String getBrowerIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if(ip != null && !ip.equals("") && !"unKnown".equalsIgnoreCase(ip)){
			//多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if(index != -1){
				return ip.substring(0,index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if(ip != null && !ip.equals("") && !"unKnown".equalsIgnoreCase(ip)){
			return ip;
		}
		return request.getRemoteAddr();
	}
}
