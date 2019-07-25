/**
 * 
 */
package com.yk.rcm.project.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;

import util.Util;

import com.alibaba.fastjson.JSONObject;
import com.yk.ext.filter.IProjectTzFilter;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;

import common.Result;

/**
 * 处理投资系统推送的数据的结构
 * 
 * @author wufucan
 *
 */
public class PfrTzDataConversionFilter implements IProjectTzFilter {

	@Resource
	private IFormalAssessmentInfoMapper formalAssessmentInfoMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yk.ext.filter.IProjectTzFilter#doFilter(java.util.Map,
	 * common.Result, com.yk.ext.filter.ProjectTzFilterChain)
	 */
	@Override
	public void doFilter(Map<String, Object> data, Result result, ProjectTzFilterChain chain) {
		formatData_V01(data);
		chain.doFilter(data, result, chain);
	}

	// 处理投资系统推送的数据的结构-高鹤修改最新
	public Map<String, Object> formatData_V01(Map<String, Object> doc) {
		Document apply = (Document) doc.get("apply");
		Document taskallocation = new Document();
		// 添加任务节点
		taskallocation.put("fixedGroup", null);
		taskallocation.put("reviewLeader", null);

		// 获得投资模式
		Boolean investmentModel = (Boolean) apply.get("investmentModel");
		apply.put("investmentModel", investmentModel);

		// 获得项目编码
		String projectNo = (String) apply.get("projectNo");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("PROJECTCODE", projectNo);
		List<Map<String, Object>> list = this.formalAssessmentInfoMapper.queryAproData(params);
		apply.put("projectNo", projectNo);
		apply.put("projectNoNew", list.get(0).get("PROJECTCODENEW"));

		// 所属大区
		Document pertainArea = new Document();
		pertainArea.put("KEY", list.get(0).get("ORGCODE"));
		pertainArea.put("VALUE", list.get(0).get("ORGNAME"));
		apply.put("pertainArea", pertainArea);

		// 业务类型
		List<JSONObject> serviceTypeList = new ArrayList<JSONObject>();
		JSONObject serviceType = new JSONObject();
		serviceType.put("VALUE", list.get(0).get("ITEM_NAME"));
		serviceType.put("KEY", list.get(0).get("SERVICETYPE"));
		serviceTypeList.add(serviceType);
		apply.put("serviceType", serviceTypeList);

		// 处理大区reportingUnit
		Document reportingUnit = new Document();
		reportingUnit.put("VALUE", list.get(0).get("REPORTINGUNITNAME"));
		reportingUnit.put("KEY", list.get(0).get("REPORTINGUNITCODE"));
		apply.put("reportingUnit", reportingUnit);

		// investmentManager
		Document investmentManager = new Document();
		investmentManager.put("NAME", list.get(0).get("RESPONSIBLEUSER"));
		investmentManager.put("VALUE", list.get(0).get("RESPONSIBLEUSERID"));
		apply.put("investmentManager", investmentManager);

		// companyHeader
		Document companyHeader = new Document();
		companyHeader.put("NAME", list.get(0).get("ORGHEADERNAME"));
		companyHeader.put("VALUE", list.get(0).get("ORGHEADERID"));
		apply.put("companyHeader", companyHeader);

		// projectAddress
		apply.put("projectAddress", list.get(0).get("ADDRESS"));

		// projectName
		apply.put("projectName", list.get(0).get("PROJECTNAME"));
		apply.put("projectNameTZ", list.get(0).get("PROJECTNAME"));

		// projectModel
		apply.put("projectModel", apply.get("projectModel"));

		// projectNum
		String projectNum = (String) apply.get("projectNum");
		if (Util.isNotEmpty(projectNum)) {
			apply.put("projectNum", projectNum);
		}

		// projectSize
		String projectSize = (String) apply.get("projectSize");
		if (Util.isNotEmpty(projectSize)) {
			apply.put("projectSize", projectSize);
		}

		// investMoney
		String investMoney = (String) apply.get("investMoney");
		if (Util.isNotEmpty(investMoney)) {
			apply.put("investMoney", investMoney);
		}

		// createBy
		apply.put("createby", list.get(0).get("RESPONSIBLEUSERID"));

		// 是否补充评审
		String is_supplement_review = (String) doc.get("is_supplement_review");
		if (Util.isNotEmpty(is_supplement_review)) {
			doc.put("is_supplement_review", is_supplement_review);
			apply.put("supplementReview", is_supplement_review.equals("1") ? true : false);
			// supplementaryItem
			String supplementaryItem = (String) apply.get("supplementaryItem");
			if (Util.isNotEmpty(supplementaryItem)) {
				apply.put("supplementaryItem", supplementaryItem);
			}
		}

		doc.put("taskallocation", taskallocation);

		doc.put("istz", "1");

		// 起草人(创建人)
		apply.remove("create_by");
		apply.remove("create_name");

		return doc;
	}

	// 处理投资系统推送的数据的结构
	public Map<String, Object> formatData(Map<String, Object> doc) {
		Document apply = (Document) doc.get("apply");

		// investmentManager
		Document investmentManager = (Document) apply.get("investmentManager");
		Document newInvestmentManager = new Document();
		newInvestmentManager.put("NAME", investmentManager.getString("name"));
		newInvestmentManager.put("VALUE", investmentManager.getString("value"));
		apply.put("investmentManager", newInvestmentManager);

		// 处理大区reportingUnit
		Document reportingUnit = (Document) apply.get("reportingUnit");
		Document newReportingUnit = new Document();
		newReportingUnit.put("VALUE", reportingUnit.getString("name"));
		newReportingUnit.put("KEY", reportingUnit.getString("value"));
		apply.put("reportingUnit", newReportingUnit);

		// companyHeader
		Document companyHeader = (Document) apply.get("companyHeader");
		Document newCompanyHeader = new Document();
		newCompanyHeader.put("VALUE", companyHeader.getString("value"));
		newCompanyHeader.put("NAME", companyHeader.getString("name"));
		apply.put("companyHeader", newCompanyHeader);

		Document createBy = new Document();
		createBy.put("VALUE", apply.getString("create_by"));
		createBy.put("NAME", apply.getString("create_name"));

		// grassrootsLegalStaff
		Document grassrootsLegalStaff = (Document) apply.get("grassrootsLegalStaff");
		Document newGrassrootsLegalStaff = null;
		if (null != grassrootsLegalStaff) {
			newGrassrootsLegalStaff = new Document();
			newGrassrootsLegalStaff.put("VALUE", grassrootsLegalStaff.getString("value"));
			newGrassrootsLegalStaff.put("NAME", grassrootsLegalStaff.getString("name"));
		} else {
			// 如果基层法务评审人ID为空，则 默认为 创建人（投资经理）
			newGrassrootsLegalStaff = createBy;
		}
		apply.put("grassrootsLegalStaff", newGrassrootsLegalStaff);

		// directPerson
		Document directPerson = (Document) apply.get("directPerson");
		if (Util.isNotEmpty(directPerson) && Util.isNotEmpty(directPerson.getString("value"))) {
			Document newDirectPerson = new Document();
			newDirectPerson.put("VALUE", directPerson.getString("value"));
			newDirectPerson.put("NAME", directPerson.getString("name"));
			apply.put("directPerson", newDirectPerson);
		}

		// 投资经理(区域经理)
		Document investmentPerson = (Document) apply.get("investmentPerson");
		if (Util.isNotEmpty(investmentPerson) && Util.isNotEmpty(investmentPerson.getString("value"))) {
			Document newInvestmentPerson = new Document();
			newInvestmentPerson.put("VALUE", investmentPerson.getString("value"));
			newInvestmentPerson.put("NAME", investmentPerson.getString("name"));
			apply.put("investmentPerson", newInvestmentPerson);
		}

		// 起草人(创建人)
		apply.put("createBy", createBy);
		apply.remove("create_by");
		apply.remove("create_name");

		// 处理附件中的人
		String investmentManagerName = newInvestmentManager.getString("NAME");
		String investmentManagerVALUE = newInvestmentManager.getString("VALUE");
		ArrayList<Document> attachments = (ArrayList<Document>) doc.get("attachment");
		for (Document attachment : attachments) {
			List<Document> files = (List<Document>) attachment.get("files");
			// 处理附件审核人
			for (Document file : files) {
				Object versionObject = file.get("version");
				if (null == versionObject) {
					versionObject = "1";
				}
				String version = versionObject.toString();
				if (version.indexOf(".") != -1) {
					version = version.substring(0, version.indexOf("."));
				}
				file.put("version", version);

				// 审核人为空，使用投资经理
				Document programmed = (Document) file.get("programmed");
				if (Util.isEmpty(programmed)) {
					file.put("programmed", new Document());
					programmed = (Document) file.get("programmed");
				}
				if (Util.isEmpty(programmed.getString("VALUE"))) {
					if (Util.isEmpty(programmed.getString("value"))) {
						programmed.remove("value");
						programmed.put("VALUE", investmentManagerVALUE);
					} else {
						programmed.put("VALUE", programmed.getString("value"));
						programmed.remove("value");
					}
				}
				if (Util.isEmpty(programmed.getString("NAME"))) {
					if (Util.isEmpty(programmed.getString("name"))) {
						programmed.remove("name");
						programmed.put("NAME", investmentManagerName);
					} else {
						programmed.put("NAME", programmed.getString("name"));
						programmed.remove("name");
					}
				}

				// 审核人为空，使用投资经理
				Document approved = (Document) file.get("approved");
				if (Util.isEmpty(approved)) {
					file.put("approved", new Document());
					approved = (Document) file.get("approved");
				}
				if (Util.isEmpty(approved.getString("VALUE"))) {
					if (Util.isEmpty(approved.getString("value"))) {
						approved.remove("value");
						approved.put("VALUE", investmentManagerVALUE);
					} else {
						approved.put("VALUE", approved.getString("value"));
						approved.remove("value");
					}
				}

				if (Util.isEmpty(approved.getString("NAME"))) {
					if (Util.isEmpty(approved.getString("name"))) {
						approved.remove("name");
						approved.put("NAME", investmentManagerName);
					} else {
						approved.put("NAME", approved.getString("name"));
						approved.remove("name");
					}
				}
			}
		}
		return doc;
	}
}
