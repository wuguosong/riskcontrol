package com.yk.rcm.project.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;

import com.yk.ext.filter.IProjectTzFilter;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import common.Result;

/**
 * 对投资推送的正式评审进行验证，看是否可以新增或修改，如果数据库中没有，或者数据库中有，但是审核状态是0,3，
 * 那么允许处理，否则不处理 
 * @author wufucan
 *
 */
public class PfrTzAllowDealFilter implements IProjectTzFilter {
	@Resource
	private IFormalAssessmentInfoService pfrInfoService;
	
	@Resource
	private IFormalAssessmentInfoMapper formalAssessmentInfoMapper;

	@Override
	public void doFilter(Map<String, Object> data, Result result,
			ProjectTzFilterChain chain) {
		
		
		
		Document apply = (Document) data.get("apply");
		String projectNo = (String) apply.get("projectNo");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("PROJECTCODE", projectNo);
		List<Map<String, Object>> list = this.formalAssessmentInfoMapper.queryAproData(params);
		if(list.size() == 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("result_status", false);
			map.put("error_info", "该项目编号主数据不存在！");
			map.put("error_code", "");
			String resultStr = JsonUtil.toJson(map);
			result.setResult_data(resultStr);
			result.setSuccess(false);
			return;
		}
		
//		Document doc = (Document)data;
//		String businessId = doc.getString("businessid");
//		if(null != businessId && !"".equals(businessId)){
//			Map<String, Object> pfr = this.pfrInfoService.getOracleByBusinessId(businessId);
//			String wf_state = pfr.get("WF_STATE").toString();
//			if("1".equals(wf_state) || "2".equals(wf_state)){
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("result_status", false);
//				map.put("error_info", "当前项目状态不允许提交！");
//				map.put("error_code", "");
//				String resultStr = JsonUtil.toJson(map);
//				result.setResult_data(resultStr);
//				result.setSuccess(false);
//				return;
//			}
//			//流程被终止，不允许修改流程，只能新增!
//			if("3".equals(wf_state)){
//				doc.remove("businessid");
//			}
//		}
		chain.doFilter(data, result, chain);
	}

}
