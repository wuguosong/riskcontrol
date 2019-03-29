/**
 * 
 */
package ws.client.message.client;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Util;
import ws.client.IWebClient;
import ws.client.message.BEWGMessageService;
import ws.client.message.BEWGMessageServiceSoap;
import ws.client.message.MessageBack;
import ws.client.message.MessageDTLink;
import ws.client.message.MessageDTLinkContent;
import ws.client.message.MessageDTText;
import ws.client.message.MessageDTTextContent;
import ws.client.message.MessageEmail;
import ws.client.message.MessageSMS;
import ws.client.message.MessageWxNews;
import ws.client.message.MessageWxText;
import ws.client.message.MessageWxTextContent;

import com.yk.common.SpringUtil;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.project.service.IWsCallService;
import common.Constants;

/**
 * 消息平台异步调用
 * 
 * @author yaphet
 * 
 */
public class SendMessageUtil implements Runnable, IWebClient {

	private static Logger logger = LoggerFactory.getLogger(SendMessageUtil.class);
//	private static String SYSCODE = PropertiesUtil.getProperty("ws.sysCode");
//	private static final String SYSCODE = "T0094";
	private static final String SYSCODE = "T00001";
	
	private String type;
	private MessageDTText dTtextMessage;
	private MessageDTLink dTlinkMessage;
	private MessageWxText wXTextMessage;
	private MessageEmail emailMessage;
	private MessageSMS smsMessage;
	
	public SendMessageUtil(Map<String, Object> data) throws Exception {
		String type = (String) data.get("type");
		if (Util.isEmpty(type)) {
			throw new Exception("参数不全：借口类型type字段必填！");
		}else if(!Constants.MESSAGE_TYPE_DT_LINK.equals(type) 
				&& !Constants.MESSAGE_TYPE_DT_TEXT.equals(type) 
				&& !Constants.MESSAGE_TYPE_EMAIL.equals(type)
				&& !Constants.MESSAGE_TYPE_SMS.equals(type) 
//				&& !Constants.MESSAGE_TYPE_WX_NEWS.equals(type)
				&& !Constants.MESSAGE_TYPE_WX_TEXT.equals(type)){
			throw new Exception("参数错误：没有此借口类型！");
		}
		
		this.type = type;
		
		if(Constants.MESSAGE_TYPE_DT_LINK.equals(type)){
			//钉钉链接验证
			String touser = (String) data.get("touser");//员工id列表（消息接收者，多个接收者用|分隔）	非必填
			String toparty = (String) data.get("toparty");//部门id列表，多个接收者用|分隔。touser或者toparty 二者有一个必填	非必填
//			String content = (String) data.get("content");//文本消息内容
			String messageUrl = (String) data.get("messageUrl");//链接消息指向的URL
			String picUrl = (String) data.get("picUrl");//链接消息显示的图标
			String text = (String) data.get("text");//链接消息显示的文本内容
			String title = (String) data.get("title");//链接消息显示的标题
			
			if(Util.isEmpty(touser) && Util.isEmpty(toparty)){
				throw new Exception("参数错误：员工id或部门id必填一个！");
			}
			if(Util.isEmpty(messageUrl)){
				throw new Exception("参数错误：链接消息指向的URL必填！");
			}
			if(Util.isEmpty(picUrl)){
				throw new Exception("参数错误：链接消息显示的图标必填！");
			}
			if(Util.isEmpty(text)){
				throw new Exception("参数错误：链接消息显示的文本内容必填！");
			}
			if(Util.isEmpty(title)){
				throw new Exception("参数错误：链接消息显示的标题必填！");
			}
			
			dTlinkMessage = new MessageDTLink();
			MessageDTLinkContent link = new MessageDTLinkContent();
			link.setMessageUrl(messageUrl);
			link.setPicUrl(picUrl);
			link.setText(text);
			link.setTitle(title);
			dTlinkMessage.setLink(link);
			dTlinkMessage.setSysCode(SYSCODE);
			
//			XMLGregorianCalendar dateToXmlDate = Util.dateToXmlDate(Util.now());
//			dTlinkMessage.setTargetTime(dateToXmlDate);
			if(Util.isNotEmpty(toparty)){
				dTlinkMessage.setToparty(toparty);
			}
			if(Util.isNotEmpty(touser)){
				dTlinkMessage.setTouser(touser);
			}
		}else if(Constants.MESSAGE_TYPE_DT_TEXT.equals(type)){
			//钉钉文本消息参数验证
			
			String touser = (String) data.get("touser");//员工id列表（消息接收者，多个接收者用|分隔）	非必填
			String toparty = (String) data.get("toparty");//部门id列表，多个接收者用|分隔。touser或者toparty 二者有一个必填	非必填
			String content = (String) data.get("content");//文本消息内容
			
			if(Util.isEmpty(touser) && Util.isEmpty(toparty)){
				throw new Exception("参数错误：员工id或部门id必填一个！");
			}
			
			if(Util.isEmpty(content)){
				throw new Exception("参数错误：发送内筒必填！");
			}
			
			dTtextMessage = new MessageDTText();
			dTtextMessage.setSysCode(SYSCODE);
//			XMLGregorianCalendar dateToXmlDate = Util.dateToXmlDate(Util.now());
//			dTtextMessage.setTargetTime(dateToXmlDate);
			MessageDTTextContent dTTextContent = new MessageDTTextContent();
			dTTextContent.setContent(content);
			dTtextMessage.setText(dTTextContent);
			
			if(Util.isNotEmpty(toparty)){
				dTtextMessage.setToparty(toparty);
			}
			if(Util.isNotEmpty(touser)){
				dTtextMessage.setTouser(touser);
			}
//		}else if(Constants.MESSAGE_TYPE_WX_NEWS.equals(type)){
//			//微信图文消息验证
			
		}else if(Constants.MESSAGE_TYPE_WX_TEXT.equals(type)){
			//微信文本消息验证
			
			String touser = (String) data.get("touser");//成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送	非必填
			String toparty = (String) data.get("toparty");//部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数	非必填
			String totag = (String) data.get("totag");//标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数。非必填
			Short safe = null;
			if(Util.isNotEmpty(data.get("safe"))){
				safe =Short.parseShort((String)data.get("safe")) ;//表示是否是保密消息，0表示否，1表示是，默认0		非必填
			}
			String content = (String) data.get("content");//消息内容
			
			if(Util.isEmpty(touser) && Util.isEmpty(toparty)){
				throw new Exception("参数错误：员工id或部门id必填一个！");
			}
			if(Util.isEmpty(content)){
				throw new Exception("参数错误：消息内容必填！");
			}
			
			wXTextMessage = new MessageWxText();
			if(Util.isNotEmpty(safe)){
				wXTextMessage.setSafe(safe);
			}
			wXTextMessage.setSysCode(SYSCODE);
//			XMLGregorianCalendar dateToXmlDate = Util.dateToXmlDate(Util.now());
//			wXTextMessage.setTargetTime(dateToXmlDate);
			
			MessageWxTextContent wxTextContent = new MessageWxTextContent();
			wxTextContent.setContent(content);
			wXTextMessage.setText(wxTextContent);
			if(Util.isNotEmpty(totag)){
				wXTextMessage.setTotag(totag);
			}
			if(Util.isNotEmpty(toparty)){
				wXTextMessage.setToparty(toparty);
			}
			if(Util.isNotEmpty(touser)){
				wXTextMessage.setTouser(touser);
			}
			
		}else if(Constants.MESSAGE_TYPE_SMS.equals(type)){
			//短信验证
			smsMessage = new MessageSMS();
			String target = (String) data.get("target");//接收方手机号码
			String content = (String) data.get("content");//短信内容
//			String targetTime = (String) data.get("targetTime");//计划发送短信的时间，如果为空则为立即发送。非必填
			
			if(Util.isEmpty(content)){
				throw new Exception("参数错误：短信内容content必填！");
			}
			if(Util.isEmpty(target)){
				throw new Exception("参数错误：短信接收人手机号target必填！");
			}
			
			smsMessage.setContent(content);
			smsMessage.setSysCode(SYSCODE);
			smsMessage.setTarget(target);
//			XMLGregorianCalendar dateToXmlDate = Util.dateToXmlDate(Util.now());
//			smsMessage.setTargetTime(dateToXmlDate);
		}else if(Constants.MESSAGE_TYPE_EMAIL.equals(type)){
			emailMessage = new MessageEmail();
			//邮件验证
			String sender = (String) data.get("sender");//发送方Email
			String target = (String) data.get("target");//接收方Email
			String cc = (String) data.get("CC");//抄送联系人Email 非必填
			String title = (String) data.get("title");//邮件标题
			String content = (String) data.get("content");//消息内容
			
			if(Util.isEmpty(sender)){
				throw new Exception("参数错误：发送方Email必填！");
			}
			if(Util.isEmpty(target)){
				throw new Exception("参数错误：接收方Email必填！");
			}
			if(Util.isEmpty(title)){
				throw new Exception("参数错误：邮件标题必填！");
			}
			if(Util.isEmpty(content)){
				throw new Exception("参数错误：消息内容必填！");
			}
			
			if(Util.isNotEmpty(cc)){
				emailMessage.setCC(cc);
			}
			emailMessage.setContent(content);
//			emailMessage.setPriority(0);//暂时无用
			emailMessage.setSender(sender);
			emailMessage.setSysCode(SYSCODE);
			emailMessage.setTarget(target);
//			XMLGregorianCalendar dateToXmlDate = Util.dateToXmlDate(Util.now());
//			emailMessage.setTargetTime(dateToXmlDate);
			emailMessage.setTitle(title);
			
		}
	}

	@Override
	public void run() {
		boolean success = true;
		
		String sendStr = "";
		String receive = "";
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			
			BEWGMessageService client = new BEWGMessageService();
			BEWGMessageServiceSoap bewgMessageServiceSoap = client.getBEWGMessageServiceSoap();
			
			if(Constants.MESSAGE_TYPE_DT_TEXT.equals(type)){
				//钉钉文本
				MessageBack sendSMS = bewgMessageServiceSoap.sendDTText(dTtextMessage);
				sendStr = JsonUtil.toJson(dTtextMessage);
				
				HashMap<String, Object> fromJson = JsonUtil.fromJson(sendStr, HashMap.class);
				fromJson.put("type", type);
				sendStr = JsonUtil.toJson(fromJson);
				receive = JsonUtil.toJson(sendSMS);
			}else if(Constants.MESSAGE_TYPE_DT_LINK.equals(type)){
				//钉钉链接
				MessageBack sendSMS = bewgMessageServiceSoap.sendDTLink(dTlinkMessage);
				sendStr = JsonUtil.toJson(dTlinkMessage);
				
				HashMap<String, Object> fromJson = JsonUtil.fromJson(sendStr, HashMap.class);
				fromJson.put("type", type);
				sendStr = JsonUtil.toJson(fromJson);
				receive = JsonUtil.toJson(sendSMS);
			}else if(Constants.MESSAGE_TYPE_WX_TEXT.equals(type)){
				//微信
				sendStr = JsonUtil.toJson(wXTextMessage);
				MessageBack sendSMS = bewgMessageServiceSoap.sendWxText(wXTextMessage);
				
				HashMap<String, Object> fromJson = JsonUtil.fromJson(sendStr, HashMap.class);
				fromJson.put("type", type);
				sendStr = JsonUtil.toJson(fromJson);
				receive = JsonUtil.toJson(sendSMS);
			}else if(Constants.MESSAGE_TYPE_SMS.equals(type)){
				//短信
				sendStr = JsonUtil.toJson(smsMessage);
				MessageBack sendSMS = bewgMessageServiceSoap.sendSMS(smsMessage);
				
				HashMap<String, Object> fromJson = JsonUtil.fromJson(sendStr, HashMap.class);
				fromJson.put("type", type);
				sendStr = JsonUtil.toJson(fromJson);
				receive = JsonUtil.toJson(sendSMS);
			}else if(Constants.MESSAGE_TYPE_EMAIL.equals(type)){
				//邮件
				sendStr = JsonUtil.toJson(emailMessage);
				MessageBack sendSMS = bewgMessageServiceSoap.sendEmail(emailMessage);
				
				HashMap<String, Object> fromJson = JsonUtil.fromJson(sendStr, HashMap.class);
				fromJson.put("type", type);
				sendStr = JsonUtil.toJson(fromJson);
				receive = JsonUtil.toJson(sendSMS);
			}
		} catch (Throwable e) {
			 logger.error(Util.parseException(e));
			 receive = Util.parseException(e);
			 success = false;
		}
		data.put("content", sendStr);
		data.put("success", success);
		data.put("receive", receive);
		data.put("type", "MSG");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
	}
	
	@Override
	public boolean resend(String json) {
		System.out.println(json);
		run();
		return true;
	}
}
