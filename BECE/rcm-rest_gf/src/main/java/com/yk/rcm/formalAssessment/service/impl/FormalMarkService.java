package com.yk.rcm.formalAssessment.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.rcm.formalAssessment.dao.IFormalMarkMapper;
import com.yk.rcm.formalAssessment.service.IFormalMarkService;


/**
 * @author yaphet
 */
@Service
@Transactional
public class FormalMarkService implements IFormalMarkService{
	@Resource
	private IFormalMarkMapper formalMarkMapper;
	
	@Override
	public Map<String, Object> queryMarks(String businessId) {
		return formalMarkMapper.queryMarks(businessId);
	}

	@Override
	public void saveOrUpdate(String businessId, String json) {
		HashMap<String, Object> data = new HashMap<String,Object>();
		Map<String, Object> queryMarks = this.formalMarkMapper.queryMarks(businessId);
		data.put("businessId", businessId);
		Document mark = Document.parse(json);
		
		//流程熟悉度
		if(Util.isNotEmpty(mark.get("flowMark"))){
			Integer flowMark = (Integer) mark.get("flowMark");
			data.put("flowMark", flowMark);
		}
		if(Util.isNotEmpty(mark.get("flowMarkDetail"))){
			String flowMarkDetail = (String) mark.get("flowMarkDetail");
			data.put("flowMarkDetail", flowMarkDetail);
		}
		if(Util.isNotEmpty(mark.get("flowMarkReason"))){
			String flowMarkReason = (String) mark.get("flowMarkReason");
			data.put("flowMarkReason", flowMarkReason);
		}
		
		//资料-及时性fileTime	fileTimeReason
		if(Util.isNotEmpty(mark.get("fileTime"))){
			Integer fileTime = (Integer) mark.get("fileTime");
			data.put("fileTime", fileTime);
		}
		
		if(Util.isNotEmpty(mark.get("fileTimeDetail"))){
			String fileTimeDetail = (String) mark.get("fileTimeDetail");
			data.put("fileTimeDetail", fileTimeDetail);
		}
		if(Util.isNotEmpty(mark.get("fileTimeReason"))){
			String fileTimeReason = (String) mark.get("fileTimeReason");
			data.put("fileTimeReason", fileTimeReason);
		}
		
		//资料-完整性fileContent	fileContentReason
		if(Util.isNotEmpty(mark.get("fileContent"))){
			Integer fileContent = (Integer) mark.get("fileContent");
			data.put("fileContent", fileContent);
		}
		if(Util.isNotEmpty(mark.get("fileContentDetail"))){
			String fileContentDetail = (String) mark.get("fileContentDetail");
			data.put("fileContentDetail", fileContentDetail);
		}
		if(Util.isNotEmpty(mark.get("fileContentReason"))){
			String fileContentReason = (String) mark.get("fileContentReason");
			data.put("fileContentReason", fileContentReason);
		}
		
		//核心-财务测算能力	moneyCalculate	moneyCalculateReason
		if(Util.isNotEmpty(mark.get("moneyCalculate"))){
			Integer moneyCalculate = (Integer) mark.get("moneyCalculate");
			data.put("moneyCalculate", moneyCalculate);
		}
		if(Util.isNotEmpty(mark.get("moneyCalculateDetail"))){
			String moneyCalculateDetail = (String) mark.get("moneyCalculateDetail");
			data.put("moneyCalculateDetail", moneyCalculateDetail);
		}
		if(Util.isNotEmpty(mark.get("moneyCalculateReason"))){
			String moneyCalculateReason = (String) mark.get("moneyCalculateReason");
			data.put("moneyCalculateReason", moneyCalculateReason);
		}
		
		//资料-准确性reviewFileAccuracy	reviewFileAccuracyReason
		if(Util.isNotEmpty(mark.get("reviewFileAccuracy"))){
			Integer reviewFileAccuracy = (Integer) mark.get("reviewFileAccuracy");
			data.put("reviewFileAccuracy", reviewFileAccuracy);
		}
		if(Util.isNotEmpty(mark.get("reviewFileAccuracyDetail"))){
			String reviewFileAccuracyDetail = (String) mark.get("reviewFileAccuracyDetail");
			data.put("reviewFileAccuracyDetail", reviewFileAccuracyDetail);
		}
		if(Util.isNotEmpty(mark.get("reviewFileAccuracyReason"))){
			String reviewFileAccuracyReason = (String) mark.get("reviewFileAccuracyReason");
			data.put("reviewFileAccuracyReason", reviewFileAccuracyReason);
		}
		
		//核心-风险识别及规避能力riskControl		riskControlReason
		if(Util.isNotEmpty(mark.get("riskControl"))){
			Integer riskControl = (Integer) mark.get("riskControl");
			data.put("riskControl", riskControl);
		}
		if(Util.isNotEmpty(mark.get("riskControlDetail"))){
			String riskControlDetail = (String) mark.get("riskControlDetail");
			data.put("riskControlDetail", riskControlDetail);
		}
		if(Util.isNotEmpty(mark.get("riskControlReason"))){
			String riskControlReason = (String) mark.get("riskControlReason");
			data.put("riskControlReason", riskControlReason);
		}
		
		//核心-方案设计能力planDesign	planDesignReason
		if(Util.isNotEmpty(mark.get("planDesign"))){
			Integer planDesign = (Integer) mark.get("planDesign");
			data.put("planDesign", planDesign);
		}
		if(Util.isNotEmpty(mark.get("planDesignDetail"))){
			String planDesignDetail = (String) mark.get("planDesignDetail");
			data.put("planDesignDetail", planDesignDetail);
		}
		if(Util.isNotEmpty(mark.get("planDesignReason"))){
			String planDesignReason = (String) mark.get("planDesignReason");
			data.put("planDesignReason", planDesignReason);
		}
		
		//资料-准确性	legalFileAccuracy	legalFileAccuracyReason
		if(Util.isNotEmpty(mark.get("legalFileAccuracy"))){
			Integer legalFileAccuracy = (Integer) mark.get("legalFileAccuracy");
			data.put("legalFileAccuracy", legalFileAccuracy);
		}
		if(Util.isNotEmpty(mark.get("legalFileAccuracyDetail"))){
			String legalFileAccuracyDetail = (String) mark.get("legalFileAccuracyDetail");
			data.put("legalFileAccuracyDetail", legalFileAccuracyDetail);
		}
		if(Util.isNotEmpty(mark.get("legalFileAccuracyReason"))){
			String legalFileAccuracyReason = (String) mark.get("legalFileAccuracyReason");
			data.put("legalFileAccuracyReason", legalFileAccuracyReason);
		}
		
		//核心-协议谈判能力	talks	talksReason
		if(Util.isNotEmpty(mark.get("talks"))){
			Integer talks = (Integer) mark.get("talks");
			data.put("talks", talks);
		}
		if(Util.isNotEmpty(mark.get("talksDetail"))){
			String talksDetail = (String) mark.get("talksDetail");
			data.put("talksDetail", talksDetail);
		}
		if(Util.isNotEmpty(mark.get("talksReason"))){
			String talksReason = (String) mark.get("talksReason");
			data.put("talksReason", talksReason);
		}
		
		/*// 合规-资料备案   fileCopy fileCopyReason
		if(Util.isNotEmpty(mark.get("fileCopy"))){
			Integer fileCopy = (Integer) mark.get("fileCopy");
			data.put("fileCopy", fileCopy);
		}
		if(Util.isNotEmpty(mark.get("fileCopyDetail"))){
			String fileCopyDetail = (String) mark.get("fileCopyDetail");
			data.put("fileCopyDetail", fileCopyDetail);
		}
		if(Util.isNotEmpty(mark.get("fileCopyReason"))){
			String fileCopyReason = (String) mark.get("fileCopyReason");
			data.put("fileCopyReason", fileCopyReason);
		}*/
		
		if(Util.isEmpty(queryMarks)){
			Integer fileCopy = 15;//暂不统计默认满分
			data.put("fileCopy", fileCopy);
			this.formalMarkMapper.save(data);
		}else{
			this.formalMarkMapper.update(data);
			Boolean heGuiTotalMarkFlag = false;
			Boolean fileTotalMarkFlag = false;
			Boolean heXinTotalMarkFlag = false;
			Map<String, Object> queryMarksForSum = this.formalMarkMapper.queryMarks(businessId);
			//计算合规能力总分：流程熟悉度+资料备案
			if(queryMarksForSum.get("FILECOPY") != null && queryMarksForSum.get("FLOWMARK") != null ){
				Integer fileCopy = Integer.parseInt(queryMarksForSum.get("FILECOPY").toString());
				Integer flowMark = Integer.parseInt(queryMarksForSum.get("FLOWMARK").toString());
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("heGuiTotalMark", fileCopy+flowMark);
				map.put("businessId", businessId);
				this.formalMarkMapper.update(map);
				heGuiTotalMarkFlag = true;
			}
			//计算资料质量总分：资料及时性+资料完整性+资料准确定
			if(queryMarksForSum.get("FILETIME") != null && queryMarksForSum.get("FILECONTENT") != null && 
					queryMarksForSum.get("REVIEWFILEACCURACY") != null &&
							queryMarksForSum.get("LEGALFILEACCURACY") != null ){
				Integer fileTime = Integer.parseInt(queryMarksForSum.get("FILETIME").toString());
				Integer fileContent = Integer.parseInt(queryMarksForSum.get("FILECONTENT").toString());
				Integer reviewFileAccuracy = Integer.parseInt(queryMarksForSum.get("REVIEWFILEACCURACY").toString());
				Integer legalFileAccuracy = Integer.parseInt(queryMarksForSum.get("LEGALFILEACCURACY").toString());
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("businessId", businessId);
				map.put("fileTotalMark", fileTime+fileContent+reviewFileAccuracy+legalFileAccuracy);
				this.formalMarkMapper.update(map);
				fileTotalMarkFlag = true;
			}
			//计算核心能力总分：风险识别及规避能力+财务测算能力+方案设计能力+协议谈判能力
			if(queryMarksForSum.get("RISKCONTROL") != null && queryMarksForSum.get("MONEYCALCULATE") != null &&
					queryMarksForSum.get("PLANDESIGN") != null &&
							queryMarksForSum.get("TALKS") != null ){
				Integer riskControl = Integer.parseInt(queryMarksForSum.get("RISKCONTROL").toString());
				Integer moneyCalculate = Integer.parseInt(queryMarksForSum.get("MONEYCALCULATE").toString());
				Integer planDesign = Integer.parseInt(queryMarksForSum.get("PLANDESIGN").toString());
				Integer talks = Integer.parseInt(queryMarksForSum.get("TALKS").toString());
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("businessId", businessId);
				map.put("heXinTotalMark", riskControl+moneyCalculate+planDesign+talks);
				this.formalMarkMapper.update(map);
				heXinTotalMarkFlag = true;
			}
			Map<String, Object> queryMarksForAllSum = this.formalMarkMapper.queryMarks(businessId);
			//算总数
			if(heGuiTotalMarkFlag && fileTotalMarkFlag && heXinTotalMarkFlag){
				Integer heGuiTotalMark = Integer.parseInt(queryMarksForAllSum.get("HEGUITOTALMARK").toString());
				Integer fileTotalMark = Integer.parseInt(queryMarksForAllSum.get("FILETOTALMARK").toString());
				Integer heXinTotalMark = Integer.parseInt(queryMarksForAllSum.get("HEXINTOTALMARK").toString());
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("businessId", businessId);
				map.put("allTotalMark", heGuiTotalMark+fileTotalMark+heXinTotalMark);
				this.formalMarkMapper.update(map);
			}
		}
	}
}
