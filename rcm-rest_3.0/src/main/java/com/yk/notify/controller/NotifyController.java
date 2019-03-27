package com.yk.notify.controller;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import com.yk.notify.entity.Notify;
import com.yk.notify.service.INotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
}
