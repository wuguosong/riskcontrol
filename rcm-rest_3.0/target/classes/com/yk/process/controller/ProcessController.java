package com.yk.process.controller;

import com.yk.process.entity.NodeConfig;
import com.yk.process.service.IProcessService;
import common.Constants;
import common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * 流程信息控制器,用于获取流程节点信息,将流程以进度条方式展示
 * Created by LiPan on 2019/2/13.
 */
@Controller
@RequestMapping("process")
public class ProcessController {
    private Logger logger = LoggerFactory.getLogger(ProcessController.class);
    @Resource
    private IProcessService processService;

    /**
     * 通过流程Key和业务Key获取流程进度信息
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return Result
     */
    @RequestMapping(value = "config", method = RequestMethod.POST)
    @ResponseBody
    public List<HashMap<String, Object>> config(String processKey, String businessKey) {
        Result result = new Result();
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        try {
            list = processService.listProcessConfiguration(processKey, businessKey);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_name("获取流程进度配置成功!");
            result.setResult_data(list);
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_name("获取流程进度配置失败!" + e.getMessage());
            result.setResult_data(e);
            logger.error("获取流程进度失败!" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 通过流程Key和业务Key获取流程进度信息
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return Result
     */
    @RequestMapping(value = "detail", method = RequestMethod.POST)
    @ResponseBody
    public List<HashMap<String, Object>> detail(String processKey, String businessKey) {
        Result result = new Result();
        List<HashMap<String, Object>> list = null;
        try {
            list = processService.listProcessNode(processKey, businessKey);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_name("获取流程进度详情成功!");
            result.setResult_data(list);
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("获取流程进度详情失败!" + e.getMessage());
            logger.error("获取流程进度详情失败!" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @RequestMapping(value = "allConfig", method = RequestMethod.POST)
    @ResponseBody
    public NodeConfig allConfig(String processKey, String businessKey) {
        NodeConfig nodeConfig = processService.createNodeConfig(processKey, businessKey);
        // 渲染所有节点
        nodeConfig = processService.renderNodeConfig(nodeConfig);
        return nodeConfig;
    }
}
