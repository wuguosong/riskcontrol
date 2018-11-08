package com.yk.ext.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.decision.serevice.IMeetingIssueService;

import common.Constants;

/**
 * 
 * 迁移已过会旧数据	到  oracle.RCM_MEETING_ISSUE
 * 1:迁移	
 * 2:根据MongoDb .formal_meeting_notice的数据，从meetingTime < 2017/6/12 往前推，以oracle.RCM_MEETING_ISSUE新系统 2017/06/12为第7次会议，会议期次依次往前推减
 * 
 * @author hubiao
 *
 */
@Service("refreshMeetingIssue")
@Transactional
public class RefreshMeetingIssue implements Initable {

	@Resource
	private IBaseMongo baseMongo;

	@Resource
	private IDecisionService decisionService;
	
	@Resource
	private IMeetingIssueService meetingIssueService;
	
	private static DateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd KK:mm aa",
			Locale.ENGLISH);

	@Override
	public void execute() {
		//查询
		MongoCollection<Document> meetingInfoCollection = baseMongo.getCollection(Constants.FORMAL_MEETING_INFO);
		FindIterable<Document> meetingInfoIterable = meetingInfoCollection.find();

		//-----------------------------------------------------
		//遍历结果集
		//组织会议期次信息
		//-----------------------------------------------------
		final Map<String, ArrayList<Map<String, Object>>> meetingIssues = new HashMap<String, ArrayList<Map<String, Object>>>();
		meetingInfoIterable.forEach(new Block<Document>(){
			private Date lastMeetingTime = null;
			@Override
			public void apply(Document document) {
				Object meetingTimeObject = document.get("meetingTime");
				if (null == meetingTimeObject)
					return;
				// 截止日期
				if(null == lastMeetingTime){
					lastMeetingTime = Util.parse("2017-06-12", "yyyy-MM-dd");
				}
					
				String meetingTimeStr = meetingTimeObject.toString();
				Date meetingTime = null;
				meetingTime = Util.parse(meetingTimeStr,
						"yyyy-MM-dd");
				if (lastMeetingTime.after(meetingTime)) {
					
					List<Map<String, Object>> meetingIssueQueryData = meetingIssueService.queryByMeetingTime(meetingTimeStr);
					if(null == meetingIssueQueryData || 0 == meetingIssueQueryData.size()){
						//System.out.println(String.format("%s %s 没有找到相应的会议期次信息",document.get("formalId"), meetingTimeStr));
						return;
					}
					Map<String, Object> meetingIssue = meetingIssueQueryData.get(0);
					
					//-----------------------------------------------------
					//组织表决项目
					//-----------------------------------------------------
					if(!meetingIssues.containsKey(meetingTimeStr)){
						meetingIssues.put(meetingTimeStr,  new ArrayList<Map<String, Object>>());
					}
					ArrayList<Map<String, Object>> decisions = meetingIssues.get(meetingTimeStr);
					
					Map<String, Object> decision = new HashMap<String,Object>();
					decision.put("id", Util.getUUID());
					decision.put("formal_type", 0);
					decision.put("formal_id", document.get("formalId"));
					decision.put("meeting_issue_id", meetingIssue.get("ID"));
					decision.put("vote_status", 2);
					decision.put("arrange_status", 2);
					decision.put("decision_result", 0);
					Map<String, Object> openMeetingPerson = (Map<String, Object>) document.get("openMeetingPerson");
					String openMeetingPersonId = "";
					if(null !=  openMeetingPerson && openMeetingPerson.size() > 0){
						openMeetingPersonId = openMeetingPerson.get("value").toString();
					}
					decision.put("open_meeting_person",openMeetingPersonId);
					
					Date decisionDate = null;
					try {
						String decisionDateStr = meetingTimeStr.concat(" ").concat(document.get("startTime").toString());
						decisionDate = formatFrom.parse(decisionDateStr);
					} catch (Exception e) {
						String decisionDateStr = meetingTimeStr.concat(" 9:50 AM");
						try {
							decisionDate = formatFrom.parse(decisionDateStr);
						} catch (Exception e1) {
							decisionDate = new Date();
						}
					}
					decision.put("decision_date", decisionDate);
					decisions.add(decision);
				}
			}
		});
	
		//-----------------------------------------------------
		//入库
		//-----------------------------------------------------
		Set<Entry<String, ArrayList<Map<String, Object>>>> entrySet = meetingIssues.entrySet();
		for (Entry<String, ArrayList<Map<String, Object>>> entry : entrySet) {
			ArrayList<Map<String, Object>> decisions = entry.getValue();
			
			Map<String, Object> decision = null;
			for (int j = 0,size = decisions.size(); j < size; j++) {
				decision = decisions.get(j);
				decision.put("order_index", j+1);
				decisionService.insertBeforeDelete(decision);
				decisionService.insert(decision);
			}
		}
	}
}
