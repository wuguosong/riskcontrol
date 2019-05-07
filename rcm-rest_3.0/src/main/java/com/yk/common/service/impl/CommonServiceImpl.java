package com.yk.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yk.common.dao.ICommonMapper;
import com.yk.common.service.ICommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2019/5/7 0007.
 */
@Service
@Transactional
public class CommonServiceImpl implements ICommonService {
    static final String MESSAGE = "message";// 校验消息
    static final String SUCCESS = "success";// true:通过,false:不通过
    static final String APPROVAL = "approval";
    static final String REVIEW = "review";
    @Resource
    private ICommonMapper commonMapper;

    @Override
    public JSONObject validateProject(String type, String id) {
        JSONObject validate = new JSONObject();
        validate.putAll(this.validateApprovalProject(type, id));
        validate.putAll(this.validateReviewProject(type, id));
        return validate;
    }

    @Override
    public JSONObject validateApprovalProject(String type, String id) {
        List<HashMap<String, Object>> list = commonMapper.selectApprovalProjects(type, id);
        JSONObject validate = new JSONObject();
        validate.put(APPROVAL, this.createValidate(APPROVAL, list));
        return validate;
    }

    @Override
    public JSONObject validateReviewProject(String type, String id) {
        List<HashMap<String, Object>> list = commonMapper.selectReviewProjects(type, id);
        JSONObject validate = new JSONObject();
        validate.put(REVIEW, this.createValidate(REVIEW, list));
        return validate;
    }

    private JSONObject createValidate(String validateType, List<HashMap<String, Object>> list) {
        JSONObject validate = new JSONObject();
        String validateMsg = null;
        if (CollectionUtils.isEmpty(list)) {
            validate.put(MESSAGE, validateMsg);
            validate.put(SUCCESS, true);
        } else {
            String projectName = String.valueOf(list.get(0).get("projectName"));
            if (REVIEW.equalsIgnoreCase(validateType)) {
                validate.put(MESSAGE, "项目" + projectName + "已经在评审当中");
            }
            if (APPROVAL.equalsIgnoreCase(validateType)) {
                validate.put(MESSAGE, "项目" + projectName + "已经在审批当中");
            }
            validate.put(SUCCESS, false);
        }
        return validate;
    }
}
