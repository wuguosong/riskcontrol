package com.yk.rcm.reviewStatistics.service.impl;

import java.io.FileOutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;
import com.yk.rcm.reviewStatistics.dao.IReviewStatisticsformMapper;
import com.yk.rcm.reviewStatistics.service.IReviewStatisticsformService;

import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * 
 * @author lyc
 *         评审台账
 *
 */

@Service
@Transactional
public class ReviewStatisticsformService implements IReviewStatisticsformService {

	@Resource
	private IReviewStatisticsformMapper reviewStatisticsformMapper;
	@Resource
	private INoticeDecisionInfoService noticeDecisionInfoService;
	@Resource
	private IBaseMongo baseMongo;
	
	@Override
	public void queryCompleteFormalReportByPage(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		params.put("page", page);
		params.put("projectName", retMap.get("projectName"));
		params.put("createBy", retMap.get("createBy"));
		params.put("pertainareaname", retMap.get("pertainareaname"));
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " create_date desc ";
		}
		params.put("orderBy", orderBy);

		List<Map<String, Object>> list = this.reviewStatisticsformMapper.queryCompleteFormalReportByPage(params);
		page.setList(list);		
	}
	
	
	@Override
	public Result exportFormalReportInfo(HttpServletRequest request){
		Result resultt = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Map<String, Object> paramMap = page.getParamMap();
		List list = this.reviewStatisticsformMapper.queryReviewStatistics(paramMap);
		String resultName="系统提示：Excel文件导出成功！";    
		 //创建HSSFWorkbook对象(excel的文档对象)
		HSSFWorkbook wb = new HSSFWorkbook();
		/** ***************以下是EXCEL第一行列标题********************* */  
		//在excel中的第1行每列的参数
        String[] head0 = new String[] { "项目评审基本信息", "项目评审基本信息", "项目评审基本信息", "项目评审基本信息", 
        		"项目评审基本信息", "项目评审基本信息","项目评审基本信息", "项目评审基本信息" ,"项目评审信息","项目评审信息","项目评审信息","项目评审信息","项目评审信息",
        		"决策结果","审批阶段"};
        //在excel中的第2行每列（合并列）的参数
        String[] head1 = new String[] { "立项", "立项", "立项", "评审", "评审" };
        //在excel中的第3行每列（合并列）的参数
        String[] head2 = new String[] { "序号", "项目名称", "业务类型", "评审规模", "总投资额（万元）", "集团持股比例", "是否用基金", "预期收益率"
        		, "投资部门", "所属战区", "项目经理", "评审人员", "法务人员"};
        //对应excel中的行和列，下表从0开始{"开始行,结束行,开始列,结束列"}
        String[] headnum0 = new String[] { "0,1,0,7", "0,0,8,12", "0,2,13,13","0,2,14,14" };
        String[] headnum1 = new String[] { "1,1,8,10", "1,1,11,12" };
        //需要显示在excel中的参数对应的值，因为是用map存的，放的都是对应的key
        
        Map<String,String> map=new HashMap<String,String>();
        try {
        	//utils类需要用到的参数
			
			String fileName ="ReviewStatistics";
			String filepath=Constants.UPLOAD_DIR+"Excel"+fileName+".xls";
			reportMergeXls(wb ,request, list, "正式评审", head0,headnum0, head1, headnum1,head2,fileName,filepath);
			
			map.put("fileName", "Excel"+fileName+".xls");
			map.put("filePath", filepath);
			map.put("result", resultName);
			resultt.setResult_name(resultName);
			resultt.setResult_data(map);
		} catch (Exception e) {
			throw new RuntimeException(e);
//			e.printStackTrace();
//			resultName="系统提示：Excel文件导出失败！";
//			map.put("result", resultName);
//			resultt.setResult_name(resultName);
//			resultt.setSuccess(false);
		} 
		return resultt;
	}
	public Result exportFormaYghReportInfo(HttpServletRequest request) {
		Result resultt = new Result();
		List list = this.reviewStatisticsformMapper.queryReviewStatistics(null);
		String resultName="系统提示：Excel文件导出成功！";    
		//创建HSSFWorkbook对象(excel的文档对象)
		HSSFWorkbook wb = new HSSFWorkbook();
		/** ***************以下是EXCEL第一行列标题********************* */  
		//在excel中的第1行每列的参数
		String[] head0 = new String[] { "项目评审基本信息", "项目评审基本信息", "项目评审基本信息", "项目评审基本信息", 
				"项目评审基本信息", "项目评审基本信息","项目评审基本信息", "项目评审基本信息" ,"项目评审信息","项目评审信息","项目评审信息","项目评审信息","项目评审信息",
				"决策结果","待签署协议"};
		//在excel中的第2行每列（合并列）的参数
		String[] head1 = new String[] { "立项", "立项", "立项", "评审", "评审" };
		//在excel中的第3行每列（合并列）的参数
		String[] head2 = new String[] { "序号", "项目名称", "业务类型", "评审规模", "总投资额（万元）", "集团持股比例", "是否用基金", "预期收益率"
				, "投资部门", "所属战区", "项目经理", "评审人员", "法务人员"};
		//对应excel中的行和列，下表从0开始{"开始行,结束行,开始列,结束列"}
		String[] headnum0 = new String[] { "0,1,0,7", "0,0,8,12", "0,2,13,13","0,2,14,14" };
		String[] headnum1 = new String[] { "1,1,8,10", "1,1,11,12" };
		//需要显示在excel中的参数对应的值，因为是用map存的，放的都是对应的key
		
		Map<String,Object> map=new HashMap<String,Object>();
		try {
			//utils类需要用到的参数
			
			String fileName ="ReviewStatistics";
			String filepath=Constants.UPLOAD_DIR+"Excel"+fileName+".xls";
			reportMergeXls(wb ,request, list, "正式评审", head0,headnum0, head1, headnum1,head2,fileName,filepath);
			
			map.put("fileName", "Excel"+fileName+".xls");
			map.put("filePath", filepath);
//			map.put("result", resultName);
			resultt.setResult_name(resultName);
			resultt.setResult_data(map);
		} catch (Exception e) {
			throw new RuntimeException(e);
//			e.printStackTrace();
//			resultName="系统提示：Excel文件导出失败！";
////			map.put("result", resultName);
//			resultt.setResult_name(resultName);
//			resultt.setSuccess(false);
		} 
		return resultt;
	}
	
	


	 /**
     * 多行表头
     * dataList：导出的数据；sheetName：表头名称； head0：表头第一行列名；headnum0：第一行合并单元格的参数
     * head1：表头第二行列名；headnum1：第二行合并单元格的参数；
	 * @param wb 
     *
     */
    public void reportMergeXls(HSSFWorkbook workbook, HttpServletRequest request,  List<Map<String, Object>> dataList,
    			String sheetName, String[] head0, String[] headnum0,
    			String[] head1, String[] headnum1, String[] head2 ,String fileName,String filepath )
            throws Exception {
        HSSFSheet sheet = workbook.createSheet(sheetName);// 创建一个表
        // 表头标题样式
        HSSFFont headfont = workbook.createFont();
        headfont.setFontName("黑体");
        headfont.setFontHeightInPoints((short) 22);// 字体大小
        HSSFCellStyle headstyle = workbook.createCellStyle();
        headstyle.setFont(headfont);
        headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        headstyle.setLocked(true);    
        // 列名样式
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);// 字体大小
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        style.setFont(font);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        style.setLocked(true);
        // 普通单元格样式（中文）
        HSSFFont font2 = workbook.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        style2.setFont(font2);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        style2.setWrapText(true); // 换行
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        // 设置列宽  （第几列，宽度）
        sheet.setColumnWidth( 0, 1600);
        sheet.setColumnWidth( 1, 12600);  
        sheet.setColumnWidth( 2, 3800);  
        sheet.setColumnWidth( 3, 3800);  
        sheet.setColumnWidth( 4, 4800);  
        sheet.setColumnWidth( 5, 3800);
        sheet.setColumnWidth( 6, 4500);
        sheet.setColumnWidth( 7, 3600); 
        sheet.setColumnWidth( 8, 3800);
        sheet.setColumnWidth( 9, 3800);
        sheet.setColumnWidth( 10, 3800);
        sheet.setColumnWidth( 11, 3800);
        sheet.setColumnWidth( 12, 3800);
        sheet.setColumnWidth( 13, 3800);
        sheet.setColumnWidth( 14, 3800);
        
        sheet.setDefaultRowHeight((short)360);//设置行高
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        // 第1行表头列名
        // row = sheet.createRow(2);
        for (int i = 0; i < 15; i++) {
            cell = row.createCell(i);
            cell.setCellValue(head0[i]);
            cell.setCellStyle(style);
        }
        //动态合并单元格
        for (int i = 0; i < headnum0.length; i++) {
            String[] temp = headnum0[i].split(",");
            Integer startrow = Integer.parseInt(temp[0]);
            Integer overrow = Integer.parseInt(temp[1]);
            Integer startcol = Integer.parseInt(temp[2]);
            Integer overcol = Integer.parseInt(temp[3]);
            sheet.addMergedRegion(new CellRangeAddress(startrow, overrow, startcol, overcol));
        }
        //设置合并单元格的参数并初始化带边框的表头（这样做可以避免因为合并单元格后有的单元格的边框显示不出来）
        //第二行
        row = sheet.createRow(1);//
        for (int i = 0; i < head0.length; i++) { 
            cell = row.createCell(i);
            cell.setCellStyle(style);
            if(i > 8 && i< 12) {
                for (int j = 0; j < head1.length; j++) {
                    cell = row.createCell(j + 8);
                    cell.setCellStyle(style);//设置excel中第2行的列的边框
                    cell.setCellValue(head1[j]);//给excel中第2行的列赋值"立项", "立项", "立项", "评审", "评审"
                    
                } 
            } 
        }
        //动态合并单元格
        for (int i = 0; i < headnum1.length; i++) {
            String[] temp = headnum1[i].split(",");
            Integer startrow = Integer.parseInt(temp[0]);
            Integer overrow = Integer.parseInt(temp[1]);
            Integer startcol = Integer.parseInt(temp[2]);
            Integer overcol = Integer.parseInt(temp[3]);
            sheet.addMergedRegion(new CellRangeAddress(startrow, overrow,
                    startcol, overcol));
        }

        //各自的表头
        row = sheet.createRow(2);//
        for (int i = 0; i < head2.length; i++) { 
            cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(head2[i]);
        }
        // 设置列值-内容
        for (int i = 0; i < dataList.size(); i++) {
        	Map<String, Object> oracleMap = dataList.get(i);
        	//根据id查询mongo的数据
        	String id = (String)oracleMap.get("BUSINESSID");
        	Map<String, Object> formalAssessmentMongo = baseMongo.queryById(id, Constants.RCM_FORMALASSESSMENT_INFO);
            row = sheet.createRow(i + 3 );
            Map<String, Object> apply = (Map<String, Object>) formalAssessmentMongo.get("apply");
            Map<String, Object> taskallocation = (Map<String, Object>) formalAssessmentMongo.get("taskallocation");
            //第一列是序号
            cell = row.createCell(0);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(i+1);
            //第二列 名称
            cell = row.createCell(1);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(oracleMap.get("PROJECTNAME").toString());
                
            //第3列 业务类型
            cell = row.createCell(2);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            String serviceType = (String)oracleMap.get("SERVICETYPE");
            cell.setCellValue(serviceType == null?"":serviceType);
            //查出决策会材料信息
            BasicDBObject queryWhere = new BasicDBObject();
			queryWhere.put("projectFormalId", id);
			System.out.println("queryWhere +" + id);
			List<Map<String, Object>> noticedecisionInfoList = baseMongo.queryByCondition(queryWhere, Constants.RCM_NOTICEDECISION_INFO);
			if(Util.isNotEmpty(noticedecisionInfoList)){
			Map<String, Object> noticeDecisionInfoMongo = noticedecisionInfoList.get(0);
	            //第4列 评审规模
	            cell = row.createCell(3);
	            cell.setCellStyle(style2);
	            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	            cell.setCellValue(noticeDecisionInfoMongo.get("contractScale")!=null?noticeDecisionInfoMongo.get("contractScale").toString():"");
	            
	            //第5列 总投资额（万元
	            cell = row.createCell(4);
	            cell.setCellStyle(style2);
	            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	            cell.setCellValue(noticeDecisionInfoMongo.get("reviewOfTotalInvestment")!=null?noticeDecisionInfoMongo.get("reviewOfTotalInvestment").toString():"");
	            
	            //第6列 集团持股比例
	            cell = row.createCell(5);
	            cell.setCellStyle(style2);
	            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	            cell.setCellValue(noticeDecisionInfoMongo.get("equityRatio")!=null?noticeDecisionInfoMongo.get("equityRatio").toString()+"%":"");
			}else{
				//第4列 评审规模
				cell = row.createCell(3);
				cell.setCellStyle(style2);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("");
				
				//第5列 总投资额（万元
				cell = row.createCell(4);
				cell.setCellStyle(style2);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("");
				
				//第6列 集团持股比例
				cell = row.createCell(5);
				cell.setCellStyle(style2);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("");
				
			}
            //第7列 是否用基金
            cell = row.createCell(6);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("");//暂时没找到对应字段
            //第8列 预期收益率 rcm_formalassessment_report  RATEOFRETURN
            Map<String, Object> formalassessmentReport = this.reviewStatisticsformMapper.queryFormalassessmentReport(id);
            cell = row.createCell(7);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            if(Util.isEmpty(formalassessmentReport)){
            	cell.setCellValue("");
            }else{
            	cell.setCellValue(formalassessmentReport.get("RATEOFRETURN")!=null?formalassessmentReport.get("RATEOFRETURN").toString()+"%":null);
            }
            //第8列 投资部门/申报单位  reportingUnit
            cell = row.createCell(8);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            String reportUnitName = (String) oracleMap.get("REPORTUNITNAME");
            cell.setCellValue(reportUnitName == null?"":reportUnitName);
            
            //第9列 所属战区/大区  reportingUnit
            cell = row.createCell(9);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            String pertainArea = (String) oracleMap.get("PERTAINAREANAME");
            cell.setCellValue(pertainArea == null?"":pertainArea);
            
            
            //第10列 项目经理/投资经理  reportingUnit
            cell = row.createCell(10);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            String createBy = (String) oracleMap.get("CREATEBYNAME");
            cell.setCellValue(createBy == null?"":createBy);
            
            //第11列 评审人员  
            cell = row.createCell(11);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            String reviewLeader = (String) oracleMap.get("REVIEWLEADER");
            cell.setCellValue(reviewLeader == null?"":reviewLeader);
            
            //第12列 法务人员  
            cell = row.createCell(12);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            String legalReviewLeader = (String) oracleMap.get("LEGALLEADER");
            cell.setCellValue(legalReviewLeader);
            
            //第12列 决策结果 rcm_decision_resolution  DECISION_RESULT
            Map<String, Object> decisionResolutiont = this.reviewStatisticsformMapper.queryDecisionResolution(id);
            cell = row.createCell(13);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            
            String stage = (String) oracleMap.get("STAGE");
            String wf_state = (String) oracleMap.get("WF_STATE");
            if("6".equals(stage) || "7".equals(stage)){
                //最终结果(0:未处理,1:同意投资,2:不同意投资,3:有条件投资,4:择期上会)
            	Map<String, Object> noticeDecstion = this.noticeDecisionInfoService.getNoticeDecstionByBusinessId(id);
            	if(Util.isNotEmpty(noticeDecstion)){
            		String consenttoinvestment = (String) noticeDecstion.get("consentToInvestment");
    				if("1".equals(consenttoinvestment)){
    					cell.setCellValue("同意投资");
    				}else if("2".equals(consenttoinvestment)){
    					cell.setCellValue("不同意投资");
    				}else if("3".equals(consenttoinvestment)){
    					cell.setCellValue("有条件投资");
    				}else if("4".equals(consenttoinvestment)){
    					cell.setCellValue("择期上会");
    				}else{
    	             	cell.setCellValue("未决策");
    	            }
    			}else{
					cell.setCellValue("未决策");
				}
             }else{
             	cell.setCellValue("未决策");
             }
            
            //第13列 审批阶段
            cell = row.createCell(14);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            //暂时为空
            
            //终止、申请、跟进、已决策
            if("3".equals(wf_state)){
            	cell.setCellValue("已终止");
            }else if(("0".equals(wf_state) || "1".equals(wf_state)) && ("1".equals(stage) || "2".equals(stage))){
            	cell.setCellValue("申请中");
            }else if( ("1".equals(wf_state) ||  "2".equals(wf_state))  && "3,3.5,3.7,3.9,4,5".contains(stage) ){
            	cell.setCellValue("跟进中");
            }else if("2".equals(wf_state) && "6,7,9".contains(stage)){
            	cell.setCellValue("已决策");
            }
            
        }
		FileOutputStream out =new FileOutputStream(filepath);
		workbook.write(out); 
		out.close();
		
		
    }

	@Override
	public void queryTenderReportByPage(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		params.put("page", page);
		params.put("projectName", retMap.get("projectName"));
		params.put("createBy", retMap.get("createBy"));
		params.put("pertainareaname", retMap.get("pertainareaname"));
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " create_date desc ";
		}
		params.put("orderBy", orderBy);

		List<Map<String, Object>> list = this.reviewStatisticsformMapper.queryTenderReportByPage(params);
		page.setList(list);
		
	}


	/**
	 * 导出投标评审台账
	 */
	@Override
	public Map<String, String> exportPreInfo(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Map<String, Object> paramMap = page.getParamMap();
		List<Map<String, Object>> list = this.reviewStatisticsformMapper.queryTenderStatistics(paramMap);
		String result="系统提示：Excel文件导出成功！";    
		 //创建HSSFWorkbook对象(excel的文档对象)
		HSSFWorkbook wb = new HSSFWorkbook();
		/** ***************以下是EXCEL第一行列标题********************* */  
		//在excel中的第1行每列的参数
        String[] head0 = new String[] { "项目评审基本信息", "项目评审基本信息", "项目评审基本信息", "项目评审基本信息", 
        			"项目评审信息", "项目评审信息","项目评审信息"};
        //在excel中的第2行每列（合并列）的参数
        String[] head1 = new String[] { "立项", "立项", "评审" };
        //在excel中的第3行每列（合并列）的参数
        String[] head2 = new String[] { "序号", "项目名称", "是否中标","业务类型", "投资部门", "项目经理",  "评审人员"};
        //对应excel中的行和列，下表从0开始{"开始行,结束行,开始列,结束列"}
        String[] headnum0 = new String[] { "0,1,0,3", "0,0,4,6"};
        String[] headnum1 = new String[] { "1,1,4,5", "1,1,6,6" };
        //需要显示在excel中的参数对应的值，因为是用map存的，放的都是对应的key
        //数据的key 因为数据复杂，此次无用
//        String[] colName = new String[] { "PROJECTNAME", "SERVICETYPE_ID", "natureTem","natureHum", "adjustTem", "adjustHum", "remark", "creator" };
        
        Map<String,String> map=new HashMap<String,String>();
        try {
			String fileName ="ReviewStatistic";
			String filepath=Constants.UPLOAD_DIR+"Excel"+fileName+".xls";
			reportTenderMergeXls(wb ,request, list, "投标评审", head0,headnum0, head1, headnum1,head2,fileName,filepath);
			map.put("fileName", "Excel"+fileName+".xls");
			map.put("filePath", filepath);
			map.put("result", result);
		} catch (Exception e) {
			throw new RuntimeException(e);
//			e.printStackTrace();
//			result="系统提示：Excel文件导出失败！";
//			map.put("result", result);
		} 
		return map;
	}
	
	 /**
     * 多行表头投标评审
     * dataList：导出的数据；sheetName：表头名称； head0：表头第一行列名；headnum0：第一行合并单元格的参数
     * head1：表头第二行列名；headnum1：第二行合并单元格的参数；
	 * @param wb 
     *
     */
    public void reportTenderMergeXls(HSSFWorkbook workbook, HttpServletRequest request,  List<Map<String, Object>> dataList,
    			String sheetName, String[] head0, String[] headnum0,
    			String[] head1, String[] headnum1, String[] head2 ,String fileName,String filepath )
            throws Exception {
        HSSFSheet sheet = workbook.createSheet(sheetName);// 创建一个表
        // 表头标题样式
        HSSFFont headfont = workbook.createFont();
        headfont.setFontName("黑体");
        headfont.setFontHeightInPoints((short) 22);// 字体大小
        HSSFCellStyle headstyle = workbook.createCellStyle();
        headstyle.setFont(headfont);
        headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        headstyle.setLocked(true);    
        // 列名样式
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);// 字体大小
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        style.setFont(font);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        style.setLocked(true);
        // 普通单元格样式（中文）
        HSSFFont font2 = workbook.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        style2.setFont(font2);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        style2.setWrapText(true); // 换行
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        // 设置列宽  （第几列，宽度）
        sheet.setColumnWidth( 0, 1600);
        sheet.setColumnWidth( 1, 12600);  
        sheet.setColumnWidth( 2, 3800);  
        sheet.setColumnWidth( 3, 3800);  
        sheet.setColumnWidth( 4, 4800);  
        sheet.setColumnWidth( 5, 3800);
        sheet.setColumnWidth( 6, 4500);
        
        sheet.setDefaultRowHeight((short)360);//设置行高
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        // 第1行表头列名
        // row = sheet.createRow(2);
        for (int i = 0;i < head0.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(head0[i]);
            cell.setCellStyle(headstyle);
        }
        //动态合并单元格
        for (int i = 0; i < headnum0.length; i++) {
            String[] temp = headnum0[i].split(",");
            Integer startrow = Integer.parseInt(temp[0]);
            Integer overrow = Integer.parseInt(temp[1]);
            Integer startcol = Integer.parseInt(temp[2]);
            Integer overcol = Integer.parseInt(temp[3]);
            sheet.addMergedRegion(new CellRangeAddress(startrow, overrow,
                    startcol, overcol));
        }
        //设置合并单元格的参数并初始化带边框的表头（这样做可以避免因为合并单元格后有的单元格的边框显示不出来）
        //第二行
        row = sheet.createRow(1);//
        for (int i = 0; i < head0.length; i++) { 
            cell = row.createCell(i);
            cell.setCellStyle(style);
            if(i > 4 && i< 7) {
                for (int j = 0; j < head1.length; j++) {
                    cell = row.createCell(j + 4);
                    cell.setCellStyle(style);//设置excel中第2行的列的边框
                    cell.setCellValue(head1[j]);//给excel中第2行的列赋值"立项", "立项", "立项", "评审"
                    
                } 
            } 
        }
        //动态合并单元格
        for (int i = 0; i < headnum1.length; i++) {
            String[] temp = headnum1[i].split(",");
            Integer startrow = Integer.parseInt(temp[0]);
            Integer overrow = Integer.parseInt(temp[1]);
            Integer startcol = Integer.parseInt(temp[2]);
            Integer overcol = Integer.parseInt(temp[3]);
            sheet.addMergedRegion(new CellRangeAddress(startrow, overrow,
                    startcol, overcol));
        }

        //各自的表头
        row = sheet.createRow(2);//
        for (int i = 0; i < head2.length; i++) { 
            cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(head2[i]);
        }
        // 设置列值-内容
        for (int i = 0; i < dataList.size(); i++) {
        	Map<String, Object> oracleMap = dataList.get(i);
        	//根据id查询mongo的数据
        	String id = (String)oracleMap.get("BUSINESSID");
        	Map<String, Object> formalAssessmentMongo = baseMongo.queryById(id, Constants.RCM_PRE_INFO);
            row = sheet.createRow(i + 3 );//因为表头有三行，所以+3
            Map<String, Object> apply = (Map<String, Object>) formalAssessmentMongo.get("apply");
            Map<String, Object> taskallocation = (Map<String, Object>) formalAssessmentMongo.get("taskallocation");
            //第一列是序号
            cell = row.createCell(0);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(i+1);
            //第二列 名称
            cell = row.createCell(1);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(oracleMap.get("PROJECTNAME").toString());
                
            //第3列 是否中标
            cell = row.createCell(2);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
           // cell.setCellValue(serviceTypeList.get(0).get("VALUE").toString());
            //第4列 业务类型
            cell = row.createCell(3);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            List<Map<String, Object>> serviceTypeList = (List<Map<String, Object>>) apply.get("serviceType");
            String serviceTypename = (String) oracleMap.get("SERVICETYPENAME"); 
            cell.setCellValue(serviceTypename == null?"":serviceTypename);
            //第5列 投资部门
            cell = row.createCell(4);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            //reportuntiname
            String reportuntiname = (String) oracleMap.get("REPORTUNTINAME");
            cell.setCellValue(reportuntiname == null ? "":reportuntiname);
            //第6列 项目经理
            cell = row.createCell(5);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            String createBy = (String) oracleMap.get("CREATEBY");
	        cell.setCellValue(createBy == null ? "":createBy);
            //第7列 评审人员
            cell = row.createCell(6);
            cell.setCellStyle(style2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            String reviewLeader = (String) oracleMap.get("REVIEWPERSONNAME");
            cell.setCellValue(reviewLeader == null ?"": reviewLeader);
        }
		FileOutputStream out =new FileOutputStream(filepath);
		workbook.write(out); 
		out.close();
		
		
    }
    
    /**
	 * 导出未过会投标评审
	 */
	@Override
	public Result exportPreReportInfo(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Map<String, Object> paramMap = page.getParamMap();
		List<Map<String, Object>> list = this.reviewStatisticsformMapper.queryTenderStatisticsWgh(paramMap);
		String resultName="系统提示：Excel文件导出成功！";    
		 //创建HSSFWorkbook对象(excel的文档对象)
		HSSFWorkbook wb = new HSSFWorkbook();
		/** ***************以下是EXCEL第一行列标题********************* */  
		//在excel中的第1行每列的参数
        String[] head0 = new String[] { "项目评审基本信息", "项目评审基本信息", "项目评审基本信息", "项目评审基本信息", 
        			"项目评审信息", "项目评审信息","项目评审信息"};
        //在excel中的第2行每列（合并列）的参数
        String[] head1 = new String[] { "立项", "立项", "评审" };
        //在excel中的第3行每列（合并列）的参数
        String[] head2 = new String[] { "序号", "项目名称", "是否中标","业务类型", "投资部门", "项目经理",  "评审人员"};
        //对应excel中的行和列，下表从0开始{"开始行,结束行,开始列,结束列"}
        String[] headnum0 = new String[] { "0,1,0,3", "0,0,4,6"};
        String[] headnum1 = new String[] { "1,1,4,5", "1,1,6,6" };
        //需要显示在excel中的参数对应的值，因为是用map存的，放的都是对应的key
        //数据的key 因为数据复杂，此次无用
//        String[] colName = new String[] { "PROJECTNAME", "SERVICETYPE_ID", "natureTem","natureHum", "adjustTem", "adjustHum", "remark", "creator" };
        
        Map<String,String> map=new HashMap<String,String>();
        try {
			String fileName ="ReviewStatistic";
			String filepath=Constants.UPLOAD_DIR+"Excel"+fileName+".xls";
			reportTenderMergeXls(wb ,request, list, "投标评审", head0,headnum0, head1, headnum1,head2,fileName,filepath);
			map.put("fileName", "Excel"+fileName+".xls");
			map.put("filePath", filepath);
			map.put("result", resultName);
			result.setResult_name(resultName);
			result.setResult_data(map);
		} catch (Exception e) {
			throw new RuntimeException(e);
//			e.printStackTrace();
//			resultName="系统提示：Excel文件导出失败！";
//			result.setResult_name(resultName);
//			result.setSuccess(false);
//			map.put("result", resultName);
		} 
		return result;
	}


	@Override
	public Map<String, String> exportBulletinReportInfo(
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}


}
