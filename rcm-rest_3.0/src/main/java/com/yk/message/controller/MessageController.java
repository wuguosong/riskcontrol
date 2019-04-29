package com.yk.message.controller;

import com.alibaba.fastjson.JSONObject;
import com.yk.message.entity.Message;
import com.yk.message.service.IMessageService;
import common.Constants;
import common.PageAssistant;
import common.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import util.UserUtil;
import ws.msg.client.MessageBack;
import ws.msg.client.MessageClient;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/1/25.
 */
@Controller
@RequestMapping("message")
public class MessageController {
	private Logger logger = LoggerFactory.getLogger(MessageController.class);
	@Resource
	private IMessageService messageService;

	/**
	 * 将留言信息以树的方式展示,每次只根据父节点查询其以下的所有子节点
	 *
	 * @param procInstId 流程实例ID
	 * @param parentId   父节点ID
	 * @return Result
	 */
	@RequestMapping(value = "tree", method = RequestMethod.POST)
	@ResponseBody
	public Result tree(String procInstId, String parentId) {
		Result result = new Result();
		try {
			List<Message> list = messageService.getMessageTree(procInstId, new Long(parentId));
			result.setSuccess(true);
			result.setResult_code(Constants.S);
			result.setResult_data(list);
			result.setResult_name("获取留言树成功!");
		} catch (Exception e) {
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_data(e);
			e.printStackTrace();
			result.setResult_name("获取留言树失败!" + e.getMessage());
			logger.error("获取留言树失败!" + e.getMessage());
		}
		return result;
	}

	/**
	 * 获取一个留言下的所有叶子留言
	 * 
	 * @param parentId 父留言ID
	 * @return
	 */
	@RequestMapping(value = "leaves", method = RequestMethod.POST)
	@ResponseBody
	public Result leaves(String parentId) {
		Result result = new Result();
		try {
			List<Message> list = messageService.getMessageChildren(new Long(parentId));
			result.setSuccess(true);
			result.setResult_code(Constants.S);
			result.setResult_data(list);
			result.setResult_name("获取留言叶子成功!");
		} catch (Exception e) {
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_data(e);
			result.setResult_name("获取留言叶子失败!" + e.getMessage());
			e.printStackTrace();
			logger.error("获取留言叶子失败!" + e.getMessage());
		}
		return result;
	}

	/**
	 * 保存
	 *
	 * @param message 留言信息实体
	 * @return Result
	 */
	@RequestMapping(value = "add", method = RequestMethod.POST)
	@ResponseBody
	public Result addMessage(Message message) {
		Result result = new Result();
		message.setCreatedBy(UserUtil.getCurrentUserUuid());
		message.setCreatedName(UserUtil.getCurrentUserName());
		message.setPushFlag("N");
		message.setReadFlag("N");
		try {
			message = messageService.save(message);
			// 保存成功后调用钉钉接口推送信息
			if(StringUtils.isNotBlank(message.getViaUsers())){
				messageService.shareMessage(message.getMessageId(), message.getViaUsers(), MessageClient._DT);
			}
			result.setSuccess(true);
			result.setResult_code(Constants.S);
			result.setResult_data(message);
			result.setResult_name("保存留言成功!");
		} catch (Exception e) {
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_data(e);
			e.printStackTrace();
			result.setResult_name("保存留言失败!" + e.getMessage());
			logger.error("保存留言失败!" + e.getMessage());
		}
		return result;
	}

	/**
	 * 更新
	 *
	 * @param message 留言信息实体
	 * @return Result
	 */
	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public Result updateMessage(Message message) {
		Result result = new Result();
		try {
			if (message != null) {
				messageService.update(message);
				message = messageService.get(message.getMessageId());
				result.setSuccess(true);
				result.setResult_code(Constants.S);
				result.setResult_data(message);
				result.setResult_name("更新留言成功!");
			}
		} catch (Exception e) {
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_data(e);
			result.setResult_name("更新留言失败!" + e.getMessage());
			e.printStackTrace();
			logger.error("更新留言失败!" + e.getMessage());
		}
		return result;
	}

	/**
	 * 获取
	 *
	 * @param messageId 留言ID
	 * @return Result
	 */
	@RequestMapping(value = "get", method = RequestMethod.POST)
	@ResponseBody
	public Result getMessage(Long messageId) {
		Result result = new Result();
		if (messageId != null) {
			try {
				Message message = messageService.get(messageId);
				result.setSuccess(true);
				result.setResult_code(Constants.S);
				result.setResult_data(message);
				result.setResult_name("获取留言成功!");
			} catch (Exception e) {
				result.setResult_code(Constants.R);
				result.setSuccess(false);
				result.setResult_data(e);
				result.setResult_name("获取留言失败!" + e.getMessage());
				e.printStackTrace();
				logger.error("获取留言失败!" + e.getMessage());
			}
		} else {
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_name("没有获取到留言ID!");
			logger.error("没有获取到留言ID!");
		}
		return result;
	}

	/**
	 * 删除
	 *
	 * @param messageId 留言ID
	 * @return Result
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public Result deleteMessage(Long messageId) {
		Result result = new Result();
		try {
			Message message = messageService.delete(messageId);
			result.setSuccess(true);
			result.setResult_code(Constants.S);
			result.setResult_data(message);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_data(e);
			result.setResult_name(e.getMessage());
			logger.error("删除留言失败!" + e.getMessage());
		}
		return result;
	}

	/**
	 * 更新是否已读
	 *
	 * @param messageId 留言ID
	 * @return Result
	 */
	@RequestMapping(value = "read", method = RequestMethod.POST)
	@ResponseBody
	public Result updateRead(Long messageId) {
		Result result = new Result();
		try {
			Message message = messageService.updateRead(messageId);
			result.setSuccess(true);
			result.setResult_code(Constants.S);
			result.setResult_data(message);
			result.setResult_name("更新已阅成功!");
		} catch (Exception e) {
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_data(e);
			result.setResult_name("更新已阅失败!" + e.getMessage());
			logger.error("更新已阅失败!" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param procInstId
	 * @return
	 */
	@RequestMapping(value = "findMessages", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<Message>> findMessages(String procInstId) {
		Map<String, List<Message>> map = null;
		try {
			map = messageService.findMessages(procInstId, UserUtil.getCurrentUserUuid());
			logger.info("获取信息成功!");
		} catch (Exception e) {
			logger.error("更新已阅失败!" + e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param procInstId
	 * @return
	 */
	@RequestMapping(value = "messages", method = RequestMethod.POST)
	@ResponseBody
	public List<JSONObject> messages(String procInstId) {
		List<JSONObject> messages = null;
		try {
			messages = messageService.messages(procInstId, UserUtil.getCurrentUserUuid());
			logger.info("获取信息成功!");
		} catch (Exception e) {
			logger.error("更新已阅失败!" + e.getMessage());
			e.printStackTrace();
		}
		return messages;
	}

	/**
	 * @param procInstId
	 * @return
	 */
	@RequestMapping(value = "queryMessagesList", method = RequestMethod.POST)
	@ResponseBody
	public List<List<JSONObject>> queryMessagesList(String procInstId, String parentId) {
		List<List<JSONObject>> messages = null;
		try {
			messages = messageService.queryMessagesList(procInstId, new Long(parentId));
			logger.info("获取信息成功!");
		} catch (Exception e) {
			logger.error("获取信息失败!" + e.getMessage());
			e.printStackTrace();
		}
		return messages;
	}

	@RequestMapping(value = "queryMessagesListPage", method = RequestMethod.POST)
	@ResponseBody
	public Result queryMessagesListPage(String page) {
		Result result = new Result();
		try {
			PageAssistant pageAssistant = new PageAssistant(page);
			messageService.queryMessagesListPage(pageAssistant);
			result.setResult_data(pageAssistant);
			logger.info("获取信息成功!");
		} catch (Exception e) {
			logger.error("获取信息失败!" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 信息分享
	 * @param messageId
	 * @param shareUsers
	 * @return
	 */
	@RequestMapping(value = "share", method = RequestMethod.POST)
	@ResponseBody
	public Result share(Long messageId, String shareUsers, String type) {
		Result result = new Result();
		try {
			MessageBack messageBack = messageService.shareMessage(messageId, shareUsers, type);
			if(messageBack != null){
				result.setSuccess(true);
				result.setResult_data(messageBack);
				result.setResult_code(Constants.S);
				result.setResult_name(messageBack.getMessage());
			}else{
				result.setSuccess(false);
				result.setResult_code(Constants.R);
				result.setResult_name(messageBack.getMessage());
			}
			result.setResult_data(messageBack);
		} catch (Exception e) {
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_data(e);
			result.setResult_name("分享信息失败!" + e.getMessage());
			logger.error("分享信息失败!" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@RequestMapping(value = "getUserInfo", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, Object> getUserInfo(String uuid) {
		HashMap<String, Object> userInfo = null;
		try {
			userInfo = messageService.getUserByUuid(uuid);
		} catch (Exception e) {
			logger.error("分享信息失败!" + e.getMessage());
			e.printStackTrace();
		}
		return userInfo;
	}

	@RequestMapping(value = "getMessageNotify", method = RequestMethod.POST)
	@ResponseBody
	public List<Message> getMessageNotify(){
		return messageService.getMessageNotify();
	}

	/**
	 * 用户选择框，分页查询用户
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryViaUsers")
	@ResponseBody
	public Result queryViaUsers(HttpServletRequest request, String message_business_id, String message_type){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Map<String, Object> paramMap = page.getParamMap();
		paramMap.put("message_business_id", message_business_id);
		paramMap.put("message_type", message_type);
		page.setParamMap(paramMap);
		messageService.queryViaUsers(page);
		result.setResult_data(page);
		return result;
	}

}
