package ws.client.portal.dto;

import java.util.Date;

public class PortalClientModel {
	

	
//	private static final String sysCode = PropertiesUtil.getProperty("ws.sysCode");
	private String uniid;//待办的唯一ID		在子系统内必须唯一
	private String title;//待办标题
	private String sender;//起草人姓名
	private String depart;//起草人部门名称
	private String url;//链接		点击该待办，打开的处理页面  hello/view.do
	/**
	 *  手机端url
	 */
	private String mobileUrl; 
	private String agree_url;//同意流程的URL	同意流程的URL，用户通过该链接来操作该流程，执行同意操作；如果没有，字段值为空。
	private String deny_url;//退回流程的URL		退回流程的URL，用户通过该链接来操作该流程，执行退回操作；如果没有，字段值为空
	private String owner;//处理者唯一ID		待办的处理人的唯一ID（主数据的psnPKvalue）
	private Date created;//生成待办时间		生成待办时间，格式例如2008-12-01 20:01:01
	private String type;//类型			该字段标识传递的信息属于何种事件	1:待办3.待阅
	private String status;//待办状态，任务的状态 1.待办 2.已办3.待阅4.已阅5.删除
	private String record_status;//待办记录状态1.新增  2.状态修改
	private String priority;//紧急程度	1、低2、中3、高
	
	public String getUniid() {
		return uniid;
	}
	public void setUniid(String uniid) {
		this.uniid = uniid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getDepart() {
		return depart;
	}
	public void setDepart(String depart) {
		this.depart = depart;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAgree_url() {
		return agree_url;
	}
	public void setAgree_url(String agree_url) {
		this.agree_url = agree_url;
	}
	public String getDeny_url() {
		return deny_url;
	}
	public void setDeny_url(String deny_url) {
		this.deny_url = deny_url;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRecord_status() {
		return record_status;
	}
	public void setRecord_status(String record_status) {
		this.record_status = record_status;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
//	public static String getSyscode() {
//		return sysCode;
//	}
	public String getMobileUrl() {
		return mobileUrl;
	}
	public void setMobileUrl(String mobileUrl) {
		this.mobileUrl = mobileUrl;
	}
	
}
