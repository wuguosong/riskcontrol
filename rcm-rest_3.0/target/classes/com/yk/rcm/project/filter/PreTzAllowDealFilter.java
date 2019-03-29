package com.yk.rcm.project.filter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;

import com.yk.ext.filter.IProjectTzFilter;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.pre.service.IPreInfoService;
import common.Result;

/**
 * @author hubiao
 *
 *	1：当流程状态为1和2的时候，不允许做修改操作
 *  2：当流程状态为3，接口的本次请求操作则为新增。
 */
public class PreTzAllowDealFilter implements IProjectTzFilter {
	@Resource
	private IPreInfoService preInfoService;

	@Override
	public void doFilter(Map<String, Object> data, Result result,
			ProjectTzFilterChain chain) {
		
		Document doc = (Document)data;
		String businessId = doc.getString("businessid");
		if(null != businessId && !"".equals(businessId)){
			Map<String, Object> pre = preInfoService.getOracleByBusinessId(businessId);
			String wf_state = pre.get("WF_STATE").toString();
			if("1".equals(wf_state) || "2".equals(wf_state)){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("result_status", false);
				map.put("error_info", "当前项目状态不允许提交！");
				map.put("error_code", "");
				String resultStr = JsonUtil.toJson(map);
				result.setResult_data(resultStr);
				result.setSuccess(false);
				return;
			}
			//流程被终止，不允许修改流程，只能新增!
			if("3".equals(wf_state)){
				doc.remove("businessid");
			}
		}
		chain.doFilter(data, result, chain);
	}

}
