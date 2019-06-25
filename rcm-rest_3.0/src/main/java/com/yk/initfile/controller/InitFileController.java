package com.yk.initfile.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yk.initfile.entity.InitFile;
import com.yk.initfile.entity.MeetingFile;
import com.yk.initfile.service.IInitFileService;
import common.PageAssistant;
import common.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
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
                JSONObject jsonCondition = this.createCondition(condition);
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
                JSONObject jsonCondition = this.createCondition(condition);
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
    public JSONObject executeSynchronizeModule(String list, String condition) {
        JSONObject js = new JSONObject();
        try {
            if (StringUtils.isNotBlank(list)) {
                List<JSONObject> jsonObjects = JSON.parseArray(list, JSONObject.class);
                JSONObject jsonCondition = this.createCondition(condition);
                List<InitFile> initFiles = initFileService.executeSynchronizeModule(jsonObjects, jsonCondition);
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

    /**
     * 查询上会文件
     *
     * @param meeting
     * @param condition
     * @return
     */
    @RequestMapping("queryMeetingFiles")
    @ResponseBody
    public JSONObject queryMeetingFiles(String meeting, String condition) {
        JSONObject js = new JSONObject();
        try {
            if (StringUtils.isNotBlank(meeting)) {
                JSONObject jsMeeting = JSON.parseObject(meeting, JSONObject.class);
                JSONObject jsCondition = this.createCondition(condition);
                List<MeetingFile> meetingFiles = initFileService.queryMeetingSynchronize(jsMeeting, jsCondition);
                js.put(data, meetingFiles);
                js.put(success, true);
            }
        } catch (Exception e) {
            js.put(data, e.getMessage());
            js.put(success, false);
            e.printStackTrace();
            logger.error("查询上会文件失败：" + e.getMessage());
        }
        return js;
    }

    /**
     * 同步上会附件
     *
     * @param meeting
     * @param condition
     * @return
     */
    @RequestMapping("executeMeetingFiles")
    @ResponseBody
    public JSONObject executeMeetingFiles(String meeting, String condition) {
        JSONObject js = new JSONObject();
        try {
            if (StringUtils.isNotBlank(meeting)) {
                JSONObject jsMeeting = JSON.parseObject(meeting, JSONObject.class);
                JSONObject jsCondition = this.createCondition(condition);
                List<MeetingFile> meetingFiles = initFileService.executeMeetingSynchronize(jsMeeting, jsCondition);
                js.put(data, meetingFiles);
                js.put(success, true);
            }
        } catch (Exception e) {
            js.put(data, e.getMessage());
            js.put(success, false);
            e.printStackTrace();
            logger.error("同步上会文件失败：" + e.getMessage());
        }
        return js;
    }

    /**
     * 同步上会文件
     *
     * @param meetingFile
     * @return
     */
    @RequestMapping("executeMeetingFile")
    @ResponseBody
    public JSONObject executeMeetingFile(MeetingFile meetingFile) {
        JSONObject js = new JSONObject();
        try {
            if (meetingFile != null) {
                MeetingFile meetingF = initFileService.executeMeetingSynchronize(meetingFile);
                js.put(data, meetingF);
                js.put(success, true);
            }
        } catch (Exception e) {
            js.put(data, e.getMessage());
            js.put(success, false);
            e.printStackTrace();
            logger.error("同步上会文件失败：" + e.getMessage());
        }
        return js;
    }

    /**
     * 创建查询条件
     *
     * @param condition
     * @return
     */
    private JSONObject createCondition(String condition) {
        JSONObject jsCondition = new JSONObject();
        if (StringUtils.isNotBlank(condition)) {
            jsCondition = JSON.parseObject(condition, JSONObject.class);
        }
        return jsCondition;
    }

    /**
     * 查询文件名不一致的文件列表
     *
     * @param list
     * @param condition
     * @return
     */
    @RequestMapping("queryDifferentFiles")
    @ResponseBody
    public JSONObject queryDifferentFiles(String list, String condition) {
        JSONObject js = new JSONObject();
        try {
            if (StringUtils.isNotBlank(list)) {
                List<JSONObject> jsonObjects = JSON.parseArray(list, JSONObject.class);
                JSONObject jsCondition = this.createCondition(condition);
                List<InitFile> initFiles = initFileService.queryDifferentFiles(jsonObjects, jsCondition);
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
     * 更新文件名不一致的文件列表
     *
     * @param list
     * @param condition
     * @return
     */
    @RequestMapping("updateDifferentFiles")
    @ResponseBody
    public JSONObject updateDifferentFiles(String list, String condition) {
        JSONObject js = new JSONObject();
        try {
            if (StringUtils.isNotBlank(list)) {
                List<JSONObject> jsonObjects = JSON.parseArray(list, JSONObject.class);
                JSONObject jsonCondition = this.createCondition(condition);
                List<InitFile> initFiles = initFileService.updateDifferentFiles(jsonObjects, jsonCondition);
                js.put(data, initFiles);
                js.put(success, true);
            }
        } catch (Exception e) {
            js.put(data, e.getMessage());
            js.put(success, false);
            e.printStackTrace();
            logger.error("更新失败：" + e.getMessage());
        }
        return js;
    }

    /**
     * 更新文件名不一致的文件列表
     *
     * @param initFile
     * @return
     */
    @RequestMapping("updateDifferentFile")
    @ResponseBody
    public JSONObject updateDifferentFile(InitFile initFile) {
        JSONObject js = new JSONObject();
        try {
            if (initFile != null) {
                InitFile initFiles = initFileService.updateDifferentFile(initFile);
                js.put(data, initFiles);
                js.put(success, true);
            }
        } catch (Exception e) {
            js.put(data, e.getMessage());
            js.put(success, false);
            e.printStackTrace();
            logger.error("更新失败：" + e.getMessage());
        }
        return js;
    }

    /**
     * 分页查询文件列表
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "queryFileListByPage", method = RequestMethod.POST)
    @ResponseBody
    public Result queryFileListByPage(String page) {
        Result result = new Result();
        try {
            PageAssistant pageAssistant = new PageAssistant(page);
            initFileService.queryFileListByPage(pageAssistant);
            result.setResult_data(pageAssistant);
            logger.info("查询信息成功!");
        } catch (Exception e) {
            logger.error("查询信息失败!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从Mongo中获取文件列表
     * @param docType
     * @param docCode
     * @return
     */
    @RequestMapping(value = "getFileListFromMongo", method = RequestMethod.POST)
    @ResponseBody
    public Result getFileListFromMongo(String docType, String docCode) {
        Result result = new Result();
        try{
            JSONObject data = initFileService.getDataFromMongo(docType,docCode);
            if(data != null){
                result.setResult_data(data.get("attachmentList"));
                result.setSuccess(true);
            }
        }catch(Exception e){
            result.setSuccess(false);
            result.setError_msg(e.getMessage());
            result.setResult_name(e.getMessage());
        }
        return result;
    }
}
