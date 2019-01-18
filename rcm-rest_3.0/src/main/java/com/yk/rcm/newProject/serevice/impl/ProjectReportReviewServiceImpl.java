package com.yk.rcm.newProject.serevice.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.power.service.IRoleService;
import com.yk.rcm.newProject.dao.IProjectReportReviewMapper;
import com.yk.rcm.newProject.serevice.IProjectReportReviewService;

import common.Constants;
import common.PageAssistant;

@Service
@Transactional
public class ProjectReportReviewServiceImpl implements IProjectReportReviewService {

/*	@Resource
	private IProjectReportService decisionService;*/

	@Resource
	private IProjectReportReviewMapper projectReportMapper;

	@Resource
	public IRoleService roleService;

	@Resource
	public IBaseMongo baseMongo;

	@Override
	public void queryProjectReportListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		params.put("todayDate", Util.format(new Date(), "yyyy-MM-dd"));
		List<Map<String, Object>> list = projectReportMapper
				.queryProjectReportListByPage(params); 
		Map<String, Object> projectInfo = new HashMap<String, Object>();
		Map<String, Object> reviewReport = new HashMap<String, Object>();
		Map<String, Object> decisionNotice = new HashMap<String, Object>();
		System.out.println(list.size());
		for(int i=0; i<list.size();i++){
			projectInfo = list.get(i);
			String businessId = (String) projectInfo.get("BUSINESS_ID");
			BasicDBObject queryAndWhere =new BasicDBObject();
			queryAndWhere.put("projectFormalId",businessId);
			if(projectInfo.get("PROJECT_TYPE").equals("pre")){
				Map<String, Object> report = baseMongo.queryById(businessId,Constants.RCM_PRE_INFO);
				Document document = (Document) report.get("reviewReport");
				if(document != null){
					reviewReport = document;
					list.get(i).put("reviewReport", reviewReport);
				}
			} else {
				List<Map<String, Object>> report = this.baseMongo.queryByCondition(queryAndWhere,
						Constants.RCM_FORMALREPORT_INFO);
				reviewReport =  report.get(0);
				list.get(i).put("reviewReport", reviewReport);
			}
			List<Map<String, Object>> queryByCondition = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_NOTICEDECISION_INFO);
			if(queryByCondition.size() != 0){
				list.get(i).put("decisionNotice", queryByCondition.get(0));
			}
		}
		page.setList(list);
	}
}
