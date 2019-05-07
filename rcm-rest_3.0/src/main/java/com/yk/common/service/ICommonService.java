package com.yk.common.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2019/5/7 0007.
 */
public interface ICommonService {
    /**
     * 校验项目
     * @param type 项目类型
     * @param id 项目ID
     * @return JSONObject
     */
    JSONObject validateProject(String type, String id);
    /**
     * 校验项目是否在审批中
     * @param type 项目类型
     * @param id 项目ID
     * @return JSONObject
     */
    JSONObject validateApprovalProject(String type, String id);
    /**
     * 校验项目是否在评审中
     * @param type 项目类型
     * @param id 项目ID
     * @return JSONObject
     */
    JSONObject validateReviewProject(String type, String id);
}
