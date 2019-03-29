package com.yk.rcm.project.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import util.Util;

import com.yk.common.SpringUtil;
import com.yk.ext.filter.IProjectTzFilter;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.flow.util.JsonUtil;
import com.yk.power.service.IServiceTypeService;

import common.Result;

/**
 * 转换一级业务类型
 * @author wufucan
 *
 */
public class PreTzDealServiceTypeFilter implements IProjectTzFilter {

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(Map<String, Object> data, Result result,
			ProjectTzFilterChain chain) {
		
		String docJson = JsonUtil.toJson((Document)data);
		
		Document copyDoc = Document.parse(docJson);
		Document apply=(Document) copyDoc.get("apply");
		//存旧的类型到mongo
		apply.put("oldServiceType", apply.get("serviceType"));
		List<Document> serviceTypeList = (List<Document>) apply.get("serviceType");
		List<Document> NewServiceTypeList = new ArrayList<Document>();
		
		
		IServiceTypeService serviceTypeService = (IServiceTypeService) SpringUtil.getBean("serviceTypeService");
		
		HashSet<String> serviceTypeIdSet = new HashSet<String>();
		for (Document serviceType : serviceTypeList) {
			if(Util.isNotEmpty(serviceType)){
				String serviceTypeKey = serviceType.getString("KEY");
				if(Util.isNotEmpty(serviceTypeKey)){
					
					Map<String, Object> rcmServiceType = serviceTypeService.getRcmServiceTypeByTzServiceType(serviceTypeKey);
					serviceTypeKey = (String) rcmServiceType.get("RCMSERVICETYPEID");
					//利用set去重
					serviceTypeIdSet.add(serviceTypeKey);
				}
			}
		}
		for (String key : serviceTypeIdSet) {
			
			Map<String, Object> dict = serviceTypeService.getDictServiceTypeByCode(key);
			String value = (String) dict.get("ITEM_NAME");
			Document serviceType = new Document();
			serviceType.put("KEY", key);
			serviceType.put("VALUE", value);
			NewServiceTypeList.add(serviceType);
			break;
		}
		apply.put("serviceType",NewServiceTypeList);
		data = copyDoc;
		chain.doFilter(data, result, chain);
	}

}
