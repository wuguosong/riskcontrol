package ws.client.contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.PropertiesUtil;
import util.Util;
import ws.client.IWebClient;

import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.rcm.project.service.IWsCallService;

import common.Constants;

public class SendProjectThread implements Runnable, IWebClient{
	private static Logger logger = LoggerFactory.getLogger(SendProjectThread.class);
	
	private String objectId;
	
	public SendProjectThread(){
	}
	
	public boolean resend(String json){
		try {
			ReceiveInvestmentItems contactSys = new ReceiveInvestmentItems();
			ReceiveInvestmentItemsSoap  soap = contactSys.getReceiveInvestmentItemsSoap();
			soap.getInvestmentItemsMessage(json);
		} catch (Exception e) {
			logger.error(Util.parseException(e));
			return false;
		}
		return true;
	}
	
	public void resendByNoticeOfDecisionId(String id){
		this.objectId = id;
		sendProjectData(getFormalId());
	}
	
	
	/**
	 * 
	 * @param objectId : _id of Table Constants.RCM_NOTICEDECISION_INFO
	 */
	public SendProjectThread(String objectId){
		this.objectId = objectId;
	}
	
	@Override
	public void run() {
		sendProjectData(getFormalId());
	}
	
	private String getFormalId(){
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		Map<String, Object> decisionInfo = baseMongo.queryById(objectId, Constants.RCM_NOTICEDECISION_INFO);
		return (String)decisionInfo.get("projectFormalId");
	}
	
	/**
	 * send sendingDoc to Contract System
	 * @param formalId
	 * @param sendingDoc
	 * @return
	 */
	private static void sendProjectData(String formalId){
		boolean success = true;
		String sendingDoc = "{\"formalId\":\""+formalId+"\"}";
		Map<String, Object> data = new HashMap<String, Object>();
		String receive = "";
		try {
			sendingDoc = dataPrepare(formalId);
			ReceiveInvestmentItems contactSys = new ReceiveInvestmentItems();
			ReceiveInvestmentItemsSoap  soap = contactSys.getReceiveInvestmentItemsSoap();
			receive = soap.getInvestmentItemsMessage(sendingDoc);
		} catch(JSONException e){
			sendingDoc = "";
			success = false;
		}catch (Exception e) {
			receive = Util.parseException(e);
			success = false;
		}
		data.put("receive", receive);
		data.put("content", sendingDoc);
		data.put("success", success);
		data.put("type", "HTTS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
	}
	
	/**
	 * prepare JSON in regular format
	 * @param formalId
	 * @return
	 */
	private static String dataPrepare(String formalId) throws JSONException{
		JSONObject investmentitem = new JSONObject();
		JSONObject investmentitems = new JSONObject();
		JSONObject sendingDoc = new JSONObject();
		
		try{
			investmentitem.put("messageid", Util.getUUID());
			investmentitem.put("datatype", "Add");
			investmentitem.put("investmentiteminfo", getInvestmentItem(formalId));
			
			investmentitems.put("investmentitem", investmentitem);
			sendingDoc.put("investmentitems", investmentitems);
		} catch (JSONException e) {
			throw e;
		}
		
		return sendingDoc.toString();
	}
	
	private static String getLegalPersonnel(String formalId){
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		Map<String, Object> formalMongo = baseMongo.queryById(formalId, Constants.RCM_FORMALASSESSMENT_INFO);
		Map<String, Object> taskallocation = (Map<String, Object>) formalMongo.get("taskallocation");
		Map<String, Object> legalReviewLeader = (Map<String, Object>) taskallocation.get("legalReviewLeader");
		
		if(Util.isNotEmpty(legalReviewLeader) && Util.isNotEmpty(legalReviewLeader.get("VALUE"))){
			String LegalPersonnel = (String) legalReviewLeader.get("VALUE");
			return LegalPersonnel;
		}
		
		return "";
	}
	
	/**
	 * get data from DB
	 * @param formalId
	 * @return
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	private static JSONObject getInvestmentItem(String formalId) throws JSONException{
	    JSONObject projectData = new JSONObject();  
	    try {
	    	BasicDBObject queryObjectId =new BasicDBObject();
			queryObjectId.put("_id", new ObjectId(formalId));
			
			IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
			Map<String, Object> applicationInfo = baseMongo.queryById(formalId, Constants.RCM_FORMALASSESSMENT_INFO);
			Document apply = (Document) applicationInfo.get("apply");
			projectData.put("projectname", apply.get("projectName"));
			projectData.put("projectno", apply.get("projectNo"));
			
			
	    	BasicDBObject queryFormalId =new BasicDBObject();
			queryFormalId.put("projectFormalId", formalId);
			Map<String, Object> decisionInfo = baseMongo.queryByCondition(queryFormalId, Constants.RCM_NOTICEDECISION_INFO).get(0);
	    	projectData.put("addreview", decisionInfo.get("additionalReview"));
	    	projectData.put("implementationmatters", decisionInfo.get("implementationMatters"));
	    	projectData.put("implementationrequirements", decisionInfo.get("implementationRequirements"));
	    	
	    	Map<String, Object> reportInfo = baseMongo.queryByCondition(queryFormalId, Constants.RCM_FORMALREPORT_INFO).get(0);
	    	projectData.put("createby", reportInfo.get("create_by"));
	    	//http://localhost:81/html/index.html#/NoticeDecisionConfirmDetailView/1/59143d9622ddf21cc1343d08/JTI1MjMlMkZOb3RpY2VEZWNpc2lvbkNvbmZpcm1MaXN0JTJGMQ==
	    		
	    	String serverPath = PropertiesUtil.getProperty("domain.allow");
			StringBuffer url = new StringBuffer(serverPath+"/html/index.html#/");
			url.append("NoticeDecisionConfirmDetailView/1/");
			url.append(formalId);
			url.append("/nothing");
	    	projectData.put("notice_decision_url", url.toString());
	    	
	    	//----------- prepare 'pfrbusinessunitcommit' data-----------
	    	List<Document> newPfrBusinessUnitCommits = new ArrayList<Document>();
	    	List<Document> pfrBusinessUnitCommits = (List<Document>) reportInfo.get("pfrBusinessUnitCommit");
	    	if(pfrBusinessUnitCommits != null){
	    		for(Document pfrBusinessUnitCommit : pfrBusinessUnitCommits){
		    		Document newPfrBusinessUnitCommit = new Document();
		    		newPfrBusinessUnitCommit.put("specificcommitments", pfrBusinessUnitCommit.get("specificCommitments"));
		    		newPfrBusinessUnitCommit.put("executionnode", pfrBusinessUnitCommit.get("executionNode"));
		    		Document responsibilityDept = (Document)pfrBusinessUnitCommit.get("responsibilityDept");
		    		newPfrBusinessUnitCommit.put("responsibilitydept", responsibilityDept.get("value"));
		    		newPfrBusinessUnitCommits.add(newPfrBusinessUnitCommit);
		    	}
	    	}
	    	projectData.put("pfrbusinessunitcommit", newPfrBusinessUnitCommits);
	    	
	    	//----------- prepare 'legalreview' data-----------
	    	List<Document> newLegalReviews = new ArrayList<Document>();
	    	List<Document> legalReviews = (List<Document>) reportInfo.get("lawList");
	    	if(legalReviews != null){
	    		for(Document legalReview : legalReviews){
		    		Document newLegalReview = new Document();
		    		newLegalReview.put("lawcontent", legalReview.get("lawOpinion"));
		    		newLegalReview.put("pendingconfirmation", legalReview.get("pendingConfirmation"));
		    		newLegalReview.put("cannotbemodified", legalReview.get("canNotBeModified"));
		    		newLegalReviews.add(newLegalReview);
		    	}
	    	}
	    	projectData.put("legalreview", newLegalReviews);
	    	
	    	projectData.put("legalpersonnel", getLegalPersonnel(formalId));
		} catch (JSONException e) {
			throw e;
		}  
	    return projectData;
	}

}
