/**
 * 
 */
package com.yk.rcm.project.filter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.PropertiesUtil;
import util.Util;

import com.yk.ext.filter.IProjectTzFilter;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.flow.util.JsonUtil;
import common.Result;

/**
 * 对投资推送的正式评审数据进行验证
 * @author wufucan
 *
 */
public class PfrTzValidFilter implements IProjectTzFilter {
	private Logger log = LoggerFactory.getLogger(PfrTzValidFilter.class);

	/* (non-Javadoc)
	 * @see com.yk.ext.filter.IProjectTzFilter#doFilter(java.util.Map, common.Result, com.yk.ext.filter.ProjectTzFilterChain)
	 */
	@Override
	public void doFilter(Map<String, Object> data, Result result,
			ProjectTzFilterChain chain) {
		validData((Document)data,result);
		if(result.isSuccess()){
			chain.doFilter(data, result, chain);
		}else{
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("result_status", false);
			map.put("error_info", result.getResult_name());
			map.put("error_code", result.getResult_code());
			String resultStr = JsonUtil.toJson(map);
			result.setResult_data(resultStr);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Result validData(Document doc,Result result){
		Document apply = (Document) doc.get("apply");
		//验证附件不能为空
		List<Document> attachments = (List<Document>) doc.get("attachment");
		if(Util.isEmpty(attachments)){
			return result.setSuccess(false)
						.setResult_name("附件不能空！")
						.setResult_code("attachment");
		}
		//验证附件是否存在
		for (Document attachment : attachments) {
			Object filesObject = attachment.get("files");
			if(!(filesObject instanceof List) || Util.isEmpty(filesObject)){
//				return result.setSuccess(false)
//						.setResult_name("附件不能空！")
//						.setResult_code("attachment.files");
				continue;
			}
			List<Document> files = (List<Document>)filesObject;
			for (Document file : files) {
				String filePath = file.getString("filePath");
				if(Util.isEmpty(filePath)){
					return result.setSuccess(false)
							.setResult_name("附件不能空！")
							.setResult_code("attachment.filePath");
				}
				//替换文件路径中的斜杠
				String preStr = filePath.replace("\\", File.separator).replace("\\\\", File.separator);
				String tzshare = PropertiesUtil.getProperty("tzshare");
				preStr = tzshare.concat(File.separator).concat(preStr);
				//原文件名
			    File preFile = new File(preStr);
			    if(!preFile.exists()){
			    	log.error("找不到文件:".concat(preStr));
			    	return result.setSuccess(false)
			    			.setResult_name("附件文件不存在！")
			    			.setResult_data(preStr)
							.setResult_code("attachment.filePath");
			    }
			}
		}
		//验证单位负责人
		Document companyHeader = (Document) apply.get("companyHeader");
		if(Util.isEmpty(companyHeader)){
			return result.setSuccess(false)
						.setResult_name("单位负责人不能为空！")
						.setResult_code("companyHeader");
		}
		//验证一级业务类型
		List<Document> serviceTypeList = (List<Document>) apply.get("serviceType");
		if(Util.isEmpty(serviceTypeList)){
			return result.setSuccess(false)
						.setResult_name("一级业务类型不能为空！")
						.setResult_code("serviceType");
		}
		//如果是传统水务或水环境，双投中心必须有人  如果不是,可以没人
		boolean isServiceTypeIsEmpty = true;
		for (Document serviceType : serviceTypeList) {
			if(Util.isNotEmpty(serviceType)){
				String serviceTypeKey = serviceType.getString("KEY");
				if(Util.isNotEmpty(serviceTypeKey)){
					if(serviceTypeKey.equals("1") || serviceTypeKey.equals("2") || serviceTypeKey.equals("4")){
						Document investmentPerson = (Document) apply.get("investmentPerson");
						if(Util.isEmpty(investmentPerson) || Util.isEmpty(investmentPerson.getString("name")) || Util.isEmpty(investmentPerson.getString("value"))){
							return result.setSuccess(false)
									.setResult_name("一级业务类型如果为 传统水务或水环境 ，区域负责人不能为空！")
									.setResult_code("investmentPerson");
						}
					}
					isServiceTypeIsEmpty = false;
				}
			}
		}
		if(isServiceTypeIsEmpty){
			return result.setSuccess(false)
						.setResult_name("一级业务类型不能为空！")
						.setResult_code("serviceType");
		}
		return result;
	}

}
