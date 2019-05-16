package com.yk.notify.controller;

import com.alibaba.fastjson.JSONObject;
import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import com.yk.notify.entity.Notify;
import com.yk.notify.service.INotifyService;
import common.PageAssistant;
import common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import util.ThreadLocalUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统知会功能
 * Created by LiPan on 2019/3/27.
 */
@Controller
@RequestMapping("notify")
public class NotifyController {
    private Logger logger = LoggerFactory.getLogger(NotifyController.class);
    @Resource
    private INotifyService notifyService;
    @Resource
    private Notify notify;

    /**
     * 查询知会信息列表
     * @param business_module 业务模块
     * @param business_id 业务ID
     * @return List<Notify> 知会信息列表
     */
    @RequestMapping("list")
    @ResponseBody
    @SysLog(module = LogConstant.MODULE_SYS, operation = LogConstant.QUERY, description = "查询知会信息列表")
    public List<Notify> list(String business_module, String business_id) {
        List<Notify> notifies = new ArrayList<Notify>();
        try {
            notifies = notifyService.list(business_module, business_id);
            logger.info("获取知会列表成功!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取知会列表失败!" + e.getMessage());
        }
        return notifies;
    }

    /**
     * 保存知会信息
     * @param business_module 业务模块
     * @param business_id 业务Id
     * @param notifies_user 知会人id字符串，格式形如："asfsafd,fafdasfsa,hfshfgh"
     * @return List<Notify> 知会信息列表
     */
    @RequestMapping("save")
    @ResponseBody
    @SysLog(module = LogConstant.MODULE_SYS, operation = LogConstant.CREATE, description = "保存知会信息列表")
    public List<Notify> save(String business_module, String business_id, String notifies_user) {
        List<Notify> notifies = new ArrayList<Notify>();
        try {
            notifies = notifyService.save(business_module, business_id, notifies_user);
            logger.info("保存知会列表成功!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存知会列表失败!" + e.getMessage());
        }
        return notifies;
    }

    /**
     * 更改知会状态
     * @param notify_id 知会id
     * @param notify_status 状态
     * @return Notify 知会信息
     */
    @RequestMapping("updateStatus")
    @ResponseBody
    @SysLog(module = LogConstant.MODULE_SYS, operation = LogConstant.UPDATE, description = "更新知会信息")
    public Notify updateStatus(String notify_id, String notify_status) {
        try {
            notify = notifyService.get(notify_id);
            notify.setNotifyStatus(notify_status);
            notifyService.update(notify);
            logger.info("更新知会状态成功!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("更新知会状态失败!" + e.getMessage());
        }
        return notify;
    }

    /**
     * 查询待阅已阅相关信息
     * @return JSONObject
     */
    @RequestMapping("queryNotifyInfo")
    @ResponseBody
    @SysLog(module = LogConstant.MODULE_SYS, operation = LogConstant.QUERY, description = "查询待阅已阅")
    public JSONObject queryNotifyInfo(){
        JSONObject jsonObject = notifyService.queryNotifyInfo();
        return jsonObject;
    }

    /**
     * 分页查询待阅
     * @param page
     * @return Result
     */
    @RequestMapping("queryMyReadingPage")
    @ResponseBody
    @SysLog(module = LogConstant.MODULE_SYS, operation = LogConstant.QUERY, description = "查询待阅")
    public Result queryMyReadingPage(String page){
        Result result = new Result();
        PageAssistant pageAssistant = new PageAssistant(page);
        Map<String, Object> paramMap = pageAssistant.getParamMap();
        if(null == paramMap){
            pageAssistant.setParamMap(new HashMap<String, Object>());
        }
        pageAssistant.getParamMap().put("notifyStatus", Notify.STATUS_1);// 待阅
        notifyService.queryNotifyInfoPage(pageAssistant);
        result.setResult_data(pageAssistant);
        return result;
    }

    /**
     * 分页查询已阅
     * @param page
     * @return Result
     */
    @RequestMapping("queryMyReadPage")
    @ResponseBody
    @SysLog(module = LogConstant.MODULE_SYS, operation = LogConstant.QUERY, description = "查询已阅")
    public Result queryMyReadPage(String page){
        Result result = new Result();
        PageAssistant pageAssistant = new PageAssistant(page);
        Map<String, Object> paramMap = pageAssistant.getParamMap();
        if(null == paramMap){
            pageAssistant.setParamMap(new HashMap<String, Object>());
        }
        pageAssistant.getParamMap().put("notifyStatus", Notify.STATUS_2);// 已阅
        notifyService.queryNotifyInfoPage(pageAssistant);
        result.setResult_data(pageAssistant);
        return result;
    }

    /**
     * 删除知会人信息
     * @param business_module 业务类型
     * @param business_id 业务ID
     * @param notify_user 知会人信息，格式如：{KEY:'',VALUE:''}
     * @return List<Notify>
     */
    @RequestMapping("delete")
    @ResponseBody
    @SysLog(module = LogConstant.MODULE_SYS, operation = LogConstant.CREATE, description = "删除知会人")
    public List<Notify> delete(String business_module, String business_id, String notify_user) {
        List<Notify> notifies = new ArrayList<Notify>();
        try {
            notifies = notifyService.delete(business_module, business_id, notify_user);
            logger.info("删除知会列表成功!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除知会列表失败!" + e.getMessage());
        }
        return notifies;
    }
}
