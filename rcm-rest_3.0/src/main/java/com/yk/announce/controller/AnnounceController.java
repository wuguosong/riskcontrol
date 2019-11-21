package com.yk.announce.controller;

import com.yk.announce.entity.Announce;
import com.yk.announce.servcie.IAnnounceService;
import common.Constants;
import common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
