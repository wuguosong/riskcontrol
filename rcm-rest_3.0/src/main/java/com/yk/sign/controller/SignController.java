package com.yk.sign.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yk.sign.entity._ApprovalNode;
import com.yk.sign.service.ISignService;
import common.Constants;
import common.PageAssistant;
import common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/3/6.
 * 加签(转办)
 */
@Controller
@RequestMapping("/sign")
public class SignController {
    private final Logger logger = LoggerFactory.getLogger(SignController.class);
    @Resource
    private ISignService signService;

    /**
     * 执行加签
     *
     * @param business_module
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     * @return
     */
    @RequestMapping("/doSign")
    @ResponseBody
    public Result doSign(String type, String business_module, String business_id, String user_json, String task_id, String option) {
        Result result = new Result();
        try {
            signService.doSign(type, business_module, business_id, user_json, task_id, option);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_name("加签操作成功!");
            logger.debug("加签操作成功:" + type + " " + business_module + " " + business_id + " " + user_json + " " + task_id + " " + option);
        } catch (Exception e) {
            logger.error("加签操作失败:" + e.getMessage());
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_name("加签操作失败!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取业务审批日志记录
     *
     * @param business_module 业务模块
     * @param business_id     业务ID
     * @return List<Map>
     */
    @RequestMapping("/listLogs")
    @ResponseBody
    public List<Map<String, Object>> listLogs(String business_module, String business_id) {
        List<Map<String, Object>> logs = null;
        try {
            logs = signService.listLogs(business_module, business_id);
            logger.debug("获取审批记录成功:[" + business_module + "," + business_id + "]");
        } catch (Exception e) {
            logger.error("获取审批记录失败:" + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * 查询代办
     * @param key
     * @param page
     * @return
     */
    @RequestMapping("/queryAgency")
    @ResponseBody
    public Result queryAgency(String key, String page) {
        Result result = new Result();
        try {
            PageAssistant data = signService.queryAgencyList(key, new PageAssistant(page));
            result.setResult_data(data);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_name("查询代办记录成功!");
            logger.debug("查询代办记录成功!");
        } catch (Exception e) {
            logger.error("查询代办记录失败:" + e.getMessage());
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_name("查询代办记录失败!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 结束加签
     *
     * @param business_module
     * @param business_id
     * @param task_id
     * @param option
     * @return
     */
    @RequestMapping("/endSign")
    @ResponseBody
    public Result endSign(String business_module, String business_id, String task_id, String option) {
        Result result = new Result();
        try {
            signService.endSign(business_module, business_id, task_id, option);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_name("加签结束成功!");
            logger.debug("加签结束成功:" + business_module + " " + business_id + " " + task_id + " " + option);
        } catch (Exception e) {
            logger.error("加签结束失败:" + e.getMessage());
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_name("加签结束失败!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 校验加签
     * @param business_module
     * @param business_id
     * @return
     */
    @RequestMapping("/validateSign")
    @ResponseBody
    public HashMap<String, Object> validateSign(String business_module, String business_id) {
        HashMap<String, Object> validate = null;
        try {
            validate = signService.validateSign(business_module, business_id);
            logger.debug("校验加签成功:" + business_module + " " + business_id);
        } catch (Exception e) {
            logger.error("校验加签失败:" + e.getMessage());
            e.printStackTrace();
        }
        return validate;
    }

    /**
     * 检测某个任务节点是否通过
     * @param processKey
     * @param businessKey
     * @return HashMap<String, Object>
     */
    @RequestMapping(value = "getCurrentTask", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getCurrentTask(String processKey, String businessKey){
        return signService.getCurrentTask(processKey, businessKey);
    }


    /**
     * 获取流程图进度
     * @param processKey
     * @param processId
     * @return
     */
    @RequestMapping(value = "getProcessImageStep", method = RequestMethod.POST)
    @ResponseBody
    public _ApprovalNode getProcessImageStep(String processKey, String processId){
        _ApprovalNode _approvalNode = signService.getNewProcessImageStep(processKey, processId);
        System.out.println(JSON.toJSONString(_approvalNode));
        return _approvalNode;
    }

    /**
     * 执行驳回操作
     * @param processKey 流程Key
     * @param businessKey 业务Key
     * @param taskId 当前任务Id
     * @param activityId 驳回的节点Key
     * @return JSONObject
     */
    @RequestMapping(value = "executeBreak", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject executeBreak(String processKey, String businessKey, String taskId, String activityId, String comments){
        JSONObject jsonObject = new JSONObject();
        try{
            signService.executeBreak(processKey, businessKey, taskId, activityId, comments);
            jsonObject.put("success", true);
            jsonObject.put("message", "流程驳回操作成功!");
            logger.info("流程驳回操作成功！");
        }catch(Exception e){
            jsonObject.put("message", e.getMessage());
            jsonObject.put("success", false);
            logger.error("流程驳回操作失败！" + e.getMessage());
        }
        return jsonObject;
    }

    /**
     * 检测并且删除不再运行时的审批任务记录
     * @param processKey
     * @param businessKey
     * @return
     */
    @RequestMapping(value = "checkDeleteNotInRunTask", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject checkDeleteNotInRunTask(String processKey, String businessKey){
        JSONObject jsonObject = new JSONObject();
        try{
            signService.deleteLogsNotInRunTask(processKey,businessKey);
            jsonObject.put("success", true);
            jsonObject.put("message", "检测成功!");
            logger.info("检测成功！");
        }catch(Exception e){
            jsonObject.put("message", e.getMessage());
            jsonObject.put("success", false);
            logger.error("检测失败！" + e.getMessage());
        }
        return jsonObject;
    }

    /**
     * 保存基层法务意见到Mongo中
     * @param processKey 流程Key
     * @param businessKey 业务Id
     * @param mongoData 要保存的Mongo数据
     * @return
     */
    @RequestMapping(value = "saveGrassrootsLegalStaffOpinionMongo", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject saveGrassrootsLegalStaffOpinionMongo(String processKey, String businessKey, String mongoData){
        JSONObject jsonObject = new JSONObject();
        try{
            signService.saveGrassrootsLegalStaffOpinionMongo(processKey,businessKey, mongoData);
            jsonObject.put("message", "保存成功!");
            jsonObject.put("success", true);
        }catch(Exception e){
            jsonObject.put("message", e.getMessage());
            logger.error("保存失败！" + e.getMessage());
            jsonObject.put("success", false);
        }
        return jsonObject;
    }
}
