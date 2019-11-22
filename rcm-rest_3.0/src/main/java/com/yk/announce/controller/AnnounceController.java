package com.yk.announce.controller;

import com.alibaba.fastjson.JSON;
import com.yk.announce.entity.Announce;
import com.yk.announce.servcie.IAnnounceService;
import com.yk.rcm.file.service.IFileService;
import common.Constants;
import common.PageAssistant;
import common.Result;
import fnd.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import util.DateUtil;
import util.UserUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lipan92
 * @version 1.0
 * @calssName AnnounceController
 * @description 公告
 * @date 2019/11/21 0021 14:26
 **/
@Controller
@RequestMapping("announce")
public class AnnounceController {
    @Autowired
    private IAnnounceService announceService;
    @Autowired
    private IFileService fileService;

    /**
     * @param announce
     * @return Result
     * @author lipan92
     * @description 保存或者更新
     * @date 2019/11/21 0021 15:28
     **/
    @RequestMapping(value = "saveOrUpdate", method = RequestMethod.POST)
    @ResponseBody
    public Result saveOrUpdate(Announce announce) {
        Result result = new Result();
        try {
            announce = announceService.initAnnounce(announce);
            if (announce.getAnnounceId() == null) {
                announce = announceService.save(announce);
            } else {
                announce = announceService.update(announce);
            }
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(announce);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult_code(Constants.E);
            result.setResult_name(e.getMessage());
        }
        return result;
    }

    /**
     * @param id Id
     * @return Result
     * @author lipan92
     * @description 通过Id获取
     * @date 2019/11/21 0021 15:28
     **/
    @RequestMapping(value = "findOne", method = RequestMethod.POST)
    @ResponseBody
    public Result findOne(Long id) {
        Result result = new Result();
        try {
            Announce announce = announceService.findOne(id);
            announce = announceService.initAnnounce(announce);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(announce);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult_code(Constants.E);
            result.setResult_name(e.getMessage());
        }
        return result;
    }

    /**
     * @param id
     * @param status
     * @return Result
     * @author lipan92
     * @description 更新状态
     * @date 2019/11/22 0022 13:44
     **/
    @RequestMapping(value = "updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public Result updateStatus(Long id, String status) {
        Result result = new Result();
        try {
            Announce announce = announceService.findOne(id);
            announce.setStatus(status);
            UserDto user = UserUtil.getCurrentUser();
            announce.setUpdateBy(user.getUuid());
            announce.setUpdateTime(DateUtil.getCurrentDate());
            announce = announceService.update(announce);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(announce);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult_code(Constants.E);
            result.setResult_name(e.getMessage());
        }
        return result;
    }

    /**
     * @param id
     * @return Result
     * @author lipan92
     * @description 通过id删除
     * @date 2019/11/21 0021 15:29
     **/
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(Long id) {
        Result result = new Result();
        try {
            announceService.delete(id);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult_code(Constants.E);
            result.setResult_name(e.getMessage());
        }
        return result;
    }

    /**
     * @param page
     * @return Result
     * @author lipan92
     * @description 分页查询
     * @date 2019/11/21 0021 18:29
     **/
    @RequestMapping(value = "pageList", method = RequestMethod.POST)
    @ResponseBody
    public Result pageList(String page) {
        Result result = new Result();
        try {
            PageAssistant pageAssistant = new PageAssistant(page);
            announceService.pageList(pageAssistant);
            result.setResult_data(pageAssistant);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult_code(Constants.E);
            result.setResult_name(e.getMessage());
        }
        return result;
    }


    /**
     * @param params 查询参数
     * @return Result
     * @author lipan92
     * @description 列表查询
     * @date 2019/11/22 0022 17:27
     **/
    @RequestMapping(value = "findList", method = RequestMethod.POST)
    @ResponseBody
    public Result findList(String params) {
        Result result = new Result();
        try {
            Map<String, Object> map = new HashMap();
            if (StringUtils.isNotBlank(params)) {
                map = JSON.parseObject(params, HashMap.class);
            }
            List<Announce> list = announceService.findList(map);
            result.setResult_data(list);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult_code(Constants.E);
            result.setResult_name(e.getMessage());
        }
        return result;
    }
}
