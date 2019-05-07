package com.yk.common.controller;

import com.alibaba.fastjson.JSONObject;
import com.yk.common.service.ICommonService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 公共Controller
 */
@Controller
@RequestMapping("common")
public class CommonController {
    @Resource
    private ICommonService commonService;

    /**
     * 校验项目是否在流程中或者在评审中
     * @param type 项目类型
     * @param id 项目ID
     * @return JSONObject
     */
    @RequestMapping("validateProject")
    @ResponseBody
    public JSONObject validateProject(String type, String id){
       return commonService.validateProject(type, id);
    }

    /**
     * 校验项目是否在流程中
     * @param type 项目类型
     * @param id 项目ID
     * @return JSONObject
     */
    @RequestMapping("validateApprovalProject")
    @ResponseBody
    public JSONObject validateApprovalProject(String type, String id){
        return commonService.validateApprovalProject(type, id);
    }

    /**
     * 校验项目是否在评审中
     * @param type 项目类型
     * @param id 项目ID
     * @return JSONObject
     */
    @RequestMapping("validateReviewProject")
    @ResponseBody
    public JSONObject validateReviewProject(String type, String id){
        return commonService.validateReviewProject(type, id);
    }
}
