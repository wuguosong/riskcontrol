package com.yk.rcm.project.filter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;

import util.Util;

import com.yk.ext.filter.IProjectTzFilter;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.flow.util.JsonUtil;
import com.yk.power.service.IOrgService;
import com.yk.rcm.base.user.service.IRepoUnitUserService;

import common.Result;

/**
 * 对投资推送的正式评审增加大区字段
 * @author wufucan
 *
 */
public class PreTzDealPertainAreaFilter implements IProjectTzFilter {
	@Resource
	private IOrgService orgService;
	@Resource
	private IRepoUnitUserService repoUnitUserService;
	
	@Override
	public void doFilter(Map<String, Object> data, Result result,
			ProjectTzFilterChain chain) {
		Document doc = (Document)data;
		Document newApply=(Document) doc.get("apply");
		Document reportingUnit = (Document) newApply.get("reportingUnit");
		Document companyHeader = (Document) newApply.get("companyHeader");
		String oldCompanyHeaderJson = JsonUtil.toJson(companyHeader);
		Document fromJson = JsonUtil.fromJson(oldCompanyHeaderJson, Document.class);
		newApply.put("oldCompanyHeader", fromJson);
		
		//处理大区pertainArea
		String orgpkvalue = reportingUnit.getString("value");
		
		Document pertainArea = new Document();
		Map<String, Object> queryPertainArea = orgService.queryPertainArea(orgpkvalue);
		if(Util.isEmpty(queryPertainArea) ){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("result_status", false);
			map.put("error_info", "项目申报单位所属大区为空！");
			map.put("error_code", "apply.companyHeader");
			String resultStr = JsonUtil.toJson(map);
			result.setResult_data(resultStr);
			return ;
		}
		Map<String, Object> reportUnit = repoUnitUserService.queryByRepoUnitId((String)queryPertainArea.get("ORGPKVALUE"));
		if(Util.isEmpty(reportUnit) ){
			
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("result_status", false);
			map.put("error_info", "系统中大区负责人没有配置！");
			map.put("error_code", "apply.reportingUnit");
			String resultStr = JsonUtil.toJson(map);
			result.setResult_data(resultStr);
			return ;
		}
		String newCompanyHeaderId = (String) reportUnit.get("COMPANYHEADER_ID");
		String newCompanyHeaderName = (String) reportUnit.get("COMPANYHEADER_NAME");
		companyHeader.put("value", newCompanyHeaderId);
		companyHeader.put("name", newCompanyHeaderName);
		newApply.put("companyHeader", companyHeader);
		
		pertainArea.put("KEY", queryPertainArea.get("ORGPKVALUE"));
		pertainArea.put("VALUE", queryPertainArea.get("NAME"));
		newApply.put("pertainArea", pertainArea);
		chain.doFilter(data, result, chain);
		
	}
	
}
