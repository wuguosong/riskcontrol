package com.yk.initfile.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goukuai.srv.YunkuFileSrv;
import com.yk.initfile.entity.InitFile;
import com.yk.initfile.service.IInitFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiPan
 * @description 期初文件处理控制器
 * @Date 2019-05-30
 */
@Controller
@RequestMapping("initfile")
public class InitFileController {
    private String data = "data";
    private String success = "success";
    private static final Logger logger = LoggerFactory.getLogger(InitFileController.class);
    @Resource
    private IInitFileService initFileService;


    /**
     * 查询数据
     *
     * @param list
     * @return
     */
    @RequestMapping("queryMongo")
    @ResponseBody
    public JSONObject queryMongo(String list, String condition) {
        JSONObject js = new JSONObject();
        try {
            if (StringUtils.isNotBlank(list)) {
                List<JSONObject> jsonObjects = JSON.parseArray(list, JSONObject.class);
                JSONObject jsonCondition = JSON.parseObject(condition, JSONObject.class);
                List<InitFile> initFiles = initFileService.queryMongo(jsonObjects, jsonCondition);
                js.put(data, initFiles);
                js.put(success, true);
            }
        } catch (Exception e) {
            js.put(data, e.getMessage());
            js.put(success, false);
            e.printStackTrace();
            logger.error("查询失败：" + e.getMessage());
        }
        return js;
    }

    /**
     * 查询同步状况
     *
     * @param list
     * @param condition
     * @return
     */
    @RequestMapping("querySynchronize")
    @ResponseBody
    public JSONObject querySynchronize(String list, String condition) {
        JSONObject js = new JSONObject();
        try {
            if (StringUtils.isNotBlank(list)) {
                List<JSONObject> jsonObjects = JSON.parseArray(list, JSONObject.class);
                JSONObject jsonCondition = JSON.parseObject(condition, JSONObject.class);
                List<InitFile> initFiles = initFileService.querySynchronize(jsonObjects, jsonCondition);
                js.put(data, initFiles);
                js.put(success, true);
            }
        } catch (Exception e) {
            js.put(data, e.getMessage());
            js.put(success, false);
            e.printStackTrace();
            logger.error("查询同步失败：" + e.getMessage());
        }
        return js;
    }

    /**
     * 同步
     *
     * @param initFile
     * @return
     */
    @RequestMapping("executeSynchronize")
    @ResponseBody
    public JSONObject executeSynchronize(InitFile initFile) {
        JSONObject js = new JSONObject();
        try {
            if (initFile != null) {
                initFile = initFileService.executeSynchronize(initFile);
                js.put(data, initFile);
                js.put(success, true);
            }
        } catch (Exception e) {
            js.put(data, e.getMessage());
            js.put(success, false);
            e.printStackTrace();
            logger.error("同步失败：" + e.getMessage());
        }
        return js;
    }

    /**
     * 同步模块
     *
     * @param list
     * @return
     */
    @RequestMapping("executeSynchronizeModule")
    @ResponseBody
    public JSONObject executeSynchronizeModule(String list) {
        JSONObject js = new JSONObject();
        try {
            if (StringUtils.isNotBlank(list)) {
                List<JSONObject> jsonObjects = JSON.parseArray(list, JSONObject.class);
                List<InitFile> initFiles = initFileService.executeSynchronizeModule(jsonObjects);
                js.put(data, initFiles);
                js.put(success, true);
            }
        } catch (Exception e) {
            js.put(data, e.getMessage());
            js.put(success, false);
            e.printStackTrace();
            logger.error("同步模块失败：" + e.getMessage());
        }
        return js;
    }
}
