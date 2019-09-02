package ws.todo.entity;

/**
 * 代办数据
 */
public class TodoInfo {
    /*
    * 以下字段为必需字段
    * */
    private String businessId;// 业务单据ID
    private String title;// 代办标题
    private String createdTime;// 生成代办的时间，格式：yyyy-MM-dd HH:mm:ss
    private String url;// 代办处理页面
    private String type;// 事件类型 1:待办 3.待阅
    private String status;// 1.待办 2.已办 3.待阅 4.已阅 5.删除
    private String owner;// 代办处理人,主数据的pk_psndoc
    /*
    * 以下为非必需字段
    * */
    private String sender;// 流程发起人
    private String depart;// 流程发起部门
    private String agreeUrl;// 同意流程的url
    private String denyUrl;// 退回流程的url
    private String recordStatus;// 办记录状态 1.新增 2.状态修改
    private String priority;// 紧急程度 1.低 2.中 3.高
    private String mobileUrl;// 手机url

    public TodoInfo(){}

    public TodoInfo(String businessId, String title, String createdTime, String url, String owner, String sender) {
        this.businessId = businessId;
        this.title = title;
        this.createdTime = createdTime;
        this.url = url;
        this.owner = owner;
        this.sender = sender;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getAgreeUrl() {
        return agreeUrl;
    }

    public void setAgreeUrl(String agreeUrl) {
        this.agreeUrl = agreeUrl;
    }

    public String getDenyUrl() {
        return denyUrl;
    }

    public void setDenyUrl(String denyUrl) {
        this.denyUrl = denyUrl;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }
}
