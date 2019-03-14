package com.yk.sign.controller;

import com.yk.sign.service.ISignService;
import common.Constants;
import common.PageAssistant;
import common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
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
}
