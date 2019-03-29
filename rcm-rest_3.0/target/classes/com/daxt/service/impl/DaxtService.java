package com.daxt.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daxt.dao.IDaxtMapper;
import com.daxt.service.IDaxtService;
import com.yk.common.IBaseMongo;
import com.yk.power.service.IDictService;
import com.yk.power.service.IOrgService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.pre.service.IPreInfoService;
import com.yk.rcm.project.service.IJournalService;

import common.Constants;
import util.PropertiesUtil;
import util.ThreadLocalUtil;
import util.Util;

@Service
@Transactional(value="daxtTxManager",rollbackFor=java.lang.Exception.class)
public class DaxtService implements IDaxtService{

	@Resource
	private IDecisionService decisionService;
	
	@Resource
	private IOrgService orgService;
	
	@Resource
	private IDaxtMapper daxtMapper;
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@Resource
	private IBulletinInfoService bulletinInfoService;
	
	@Resource
	private IPreInfoService preInfoService;
	
	@Resource
	private IDictService dictService;
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IJournalService journalService;
	
	private static final Logger log = LoggerFactory.getLogger(DaxtService.class);
	
	/**
	 * 正式评审有合同签订的，在合同签订后（合同上传扫描件）推送
	 * @param businessId
	 */
	@Override
	public void SRSM001(Date today,String businessId) {
		String property = PropertiesUtil.getProperty("daxt.SRSM001");
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(today);
	    calendar.add(calendar.DATE, Integer.parseInt(property));
	    calendar.set(calendar.HOUR_OF_DAY, 23);
	    calendar.set(calendar.MINUTE, 0);
	    calendar.set(calendar.SECOND, 0);
	    Date date = calendar.getTime(); 
		formalAssessmentInfoService.startPigeonholeByBusinessId(businessId, date);
	}
	
	/**
	 * 正式评审小项目未过会的，办结后三个月推送
	 * @param businessId
	 */
	@Override
	public void SRSM002(Date today,String businessId) {
		String property = PropertiesUtil.getProperty("daxt.SRSM002");
		Calendar calendar  =  Calendar.getInstance();
	    calendar.setTime(today);
	    calendar.add(calendar.DATE, Integer.parseInt(property));
	    calendar.set(calendar.HOUR_OF_DAY, 23);
	    calendar.set(calendar.MINUTE, 0);
	    calendar.set(calendar.SECOND, 0);
	    Date date = calendar.getTime(); 
		formalAssessmentInfoService.startPigeonholeByBusinessId(businessId, date);
	}

	/**
	 * 正式评审无合同签订的，决策通知书下达后一年推送
	 * @param businessId
	 */
	@Override
	public void SRSM003(Date today,String businessId) {
		String property = PropertiesUtil.getProperty("daxt.SRSM003");
		Calendar calendar  =  Calendar.getInstance();
	    calendar.setTime(today);
	    calendar.add(calendar.DATE, Integer.parseInt(property));
	    calendar.set(calendar.HOUR_OF_DAY, 23);
	    calendar.set(calendar.MINUTE, 0);
	    calendar.set(calendar.SECOND, 0);
	    Date date = calendar.getTime(); 
		formalAssessmentInfoService.startPigeonholeByBusinessId(businessId, date);
	}
	
	/**
	 * 其他决策事项办结后一个月推送
	 * @param businessId
	 */
	@Override
	public void SRSM004(Date today,String businessId) {
		String property = PropertiesUtil.getProperty("daxt.SRSM004");
		Calendar calendar  =  Calendar.getInstance();
	    calendar.setTime(today);
	    calendar.add(calendar.DATE, Integer.parseInt(property));
	    calendar.set(calendar.HOUR_OF_DAY, 23);
	    calendar.set(calendar.MINUTE, 0);
	    calendar.set(calendar.SECOND, 0);
	    Date date = calendar.getTime(); 
		bulletinInfoService.startPigeonholeByBusinessId(businessId, date);
	}
	
	/**
	 * 投标评审留最终版及评审意见，办结后一年推送
	 */
	@Override
	public void SRSM005(Date today,String businessId) {
		String property = PropertiesUtil.getProperty("daxt.SRSM005");
		Calendar calendar  =  Calendar.getInstance();
	    calendar.setTime(today);
	    calendar.add(calendar.DATE, Integer.parseInt(property));
	    calendar.set(calendar.HOUR_OF_DAY, 23);
	    calendar.set(calendar.MINUTE, 0);
	    calendar.set(calendar.SECOND, 0);
	    Date date = calendar.getTime(); 
		preInfoService.startPigeonholeByBusinessId(businessId, date);
	}
	
	/**
	 * 正式评审流程终止一年推送
	 */
	@Override
	public void SRSM006(Date today,String businessId) {
		String property = PropertiesUtil.getProperty("daxt.SRSM006");
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(today);
	    calendar.add(calendar.DATE, Integer.parseInt(property));
	    calendar.set(calendar.HOUR_OF_DAY, 23);
	    calendar.set(calendar.MINUTE, 0);
	    calendar.set(calendar.SECOND, 0);
	    Date date = calendar.getTime(); 
	    formalAssessmentInfoService.startPigeonholeByBusinessId(businessId, date);
	}
	
	/**
	 * 投标评审流程终止一年推送
	 */
	@Override
	public void SRSM007(Date today,String businessId) {
		String property = PropertiesUtil.getProperty("daxt.SRSM007");
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(today);
	    calendar.add(calendar.DATE, Integer.parseInt(property));
	    calendar.set(calendar.HOUR_OF_DAY, 23);
	    calendar.set(calendar.MINUTE, 0);
	    calendar.set(calendar.SECOND, 0);
	    Date date = calendar.getTime(); 
		preInfoService.startPigeonholeByBusinessId(businessId, date);
	}
	
	/**
	 * 其他评审流程终止一年推送
	 */
	@Override
	public void SRSM008(Date today,String businessId) {
		String property = PropertiesUtil.getProperty("daxt.SRSM008");
		Calendar calendar  =  Calendar.getInstance();
	    calendar.setTime(today);
	    calendar.add(calendar.DATE, Integer.parseInt(property));
	    calendar.set(calendar.HOUR_OF_DAY, 23);
	    calendar.set(calendar.MINUTE, 0);
	    calendar.set(calendar.SECOND, 0);
	    Date date = calendar.getTime(); 
	    bulletinInfoService.startPigeonholeByBusinessId(businessId, date);
	}

	@Override
	public void prfStart(final String businessId) {
		final String userId = ThreadLocalUtil.getUserId();
		new Thread(){
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				try {
					Map<String, Object> itemData = dictService.queryItemByFuCoZiName(Constants.DICT_INTERFACE_TYPE,Constants.DICT_INTE_TYPE_DAXT);
					String itemCode = itemData.get("ITEM_CODE").toString();
					if(!"1".equals(itemCode)){
						return;
					}
					
					Map<String, Object> prfOracleMap = formalAssessmentInfoService.getOracleByBusinessId(businessId);
					if(prfOracleMap == null){
						throw new RuntimeException("prf基本信息表,找不到businessId为".concat(businessId));
					}
					
					//流程终止
					if("3".equals(prfOracleMap.get("WF_STATE"))){
						SRSM006(new Date(),businessId);
					}
					//无需上会
					else if("9".equals(prfOracleMap.get("STAGE"))){
						SRSM002(new Date(),businessId);
					}
					//确认决策通知书
					else if("7".equals(prfOracleMap.get("STAGE"))){
						//不需要签订合同
						Map<String, Object> decisionInfo = decisionService.queryByBusinessId(businessId);
						String decisionResult = decisionInfo.get("DECISION_RESULT").toString();
						if("2".equals(decisionResult) || "4".equals(decisionResult)){
							SRSM002(new Date(),businessId);
						}else{
							//需要签订，并且已签订合同
							List<Map<String, Object>> projects = formalAssessmentInfoService.queryContractByBusinessId(businessId);
							if(Util.isNotEmpty(projects)){
								SRSM001(new Date(),businessId);
							}
							else{
								//需要签订，但还没签订合同
								SRSM003(new Date(),businessId);
							}
						}
					}
				} catch (Exception e) {
					String error = Util.parseException(e).replace(" ", "");
					log.error(error);
					Map<String, Object> journal = new HashMap<String, Object>();
					journal.put("requestUrl", "DaxtService.prfStart");
					journal.put("accesstime", Util.getTime());
					journal.put("content", error);
					journal.put("type", "DAXT_ERROR");
					journal.put("requestParams", businessId);
					journal.put("requestUserId", userId);
					journalService.save(journal);
				}
			}
		}.start();
	}
	
	@Override
	public void prfStop(String businessId) {
		formalAssessmentInfoService.cancelPigeonholeByBusinessId(businessId);
	}

	@Override
	public void preStart(final String businessId) {
		final String userId = ThreadLocalUtil.getUserId();
		new Thread(){
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				try {
					Map<String, Object> itemData = dictService.queryItemByFuCoZiName(Constants.DICT_INTERFACE_TYPE,Constants.DICT_INTE_TYPE_DAXT);
					String itemCode = itemData.get("ITEM_CODE").toString();
					if(!"1".equals(itemCode)){
						return;
					}
					Map<String, Object> preMap = preInfoService.getOracleByBusinessId(businessId);
					if(preMap == null){
						throw new RuntimeException("pre基本信息表,找不到businessId为".concat(businessId));
					}
					//流程终止
					if("3".equals(preMap.get("WF_STATE"))){
						SRSM007(new Date(),businessId);
					//已表决
					}else if("6".equals(preMap.get("STAGE")) || "9".equals(preMap.get("STAGE"))){
						SRSM005(new Date(),businessId);
					}
				} catch (Exception e) {
					String error = Util.parseException(e).replace(" ", "");
					log.error(error);
					Map<String, Object> journal = new HashMap<String, Object>();
					journal.put("requestUrl", "DaxtService.preStart");
					journal.put("accesstime", Util.getTime());
					journal.put("content", error);
					journal.put("type", "DAXT_ERROR");
					journal.put("requestParams", businessId);
					journal.put("requestUserId", userId);
					journalService.save(journal);
				}
			};
		}.start();
	}
	
	@Override
	public void preStop(String businessId) {
		preInfoService.cancelPigeonholeByBusinessId(businessId);
	}

	@Override
	public void bulletinStart(final String businessId) {
		final String userId = ThreadLocalUtil.getUserId();
		new Thread(){
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				try {
					Map<String, Object> itemData = dictService.queryItemByFuCoZiName(Constants.DICT_INTERFACE_TYPE,Constants.DICT_INTE_TYPE_DAXT);
					String itemCode = itemData.get("ITEM_CODE").toString();
					if(!"1".equals(itemCode)){
						return;
					}
					Map<String, Object> bulletinMap = bulletinInfoService.queryByBusinessId(businessId);
					if(bulletinMap == null){
						throw new RuntimeException("bulletin基本信息表,找不到businessId为".concat(businessId));
					}
					//流程终止
					if("3".equals(bulletinMap.get("AUDITSTATUS"))){
						SRSM008(new Date(),businessId);
					//出会议纪要
					}else if("5".equals(bulletinMap.get("STAGE")) || "9".equals(bulletinMap.get("STAGE"))){
						SRSM004(new Date(),businessId);
					}
				} catch (Exception e) {
					String error = Util.parseException(e).replace(" ", "");
					log.error(error);
					Map<String, Object> journal = new HashMap<String, Object>();
					journal.put("requestUrl", "DaxtService.bulletinStart");
					journal.put("accesstime", Util.getTime());
					journal.put("content", error);
					journal.put("type", "DAXT_ERROR");
					journal.put("requestParams", businessId);
					journal.put("requestUserId", userId);
					journalService.save(journal);
				}
			}
		}.start();
	}
	
	@Override
	public void bulletinStop(String businessId) {
		bulletinInfoService.cancelPigeonholeByBusinessId(businessId);
	}
}
