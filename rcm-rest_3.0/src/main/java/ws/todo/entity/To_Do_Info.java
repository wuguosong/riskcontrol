package ws.todo.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 代办信息
 */
@XmlRootElement(name = "todo_info")
public class To_Do_Info {
    private String uniid;// Y,待办的唯一ID，在子系统内必须唯一
    private String title;// Y,待办的标题
    private String created;// Y,生成待办时间，格式例如2008-12-01 20:01:01
    private String url;// Y,点击该待办，打开的处理页面
    private String type;// Y,该字段标识传递的信息属于何种事件 1:待办 3.待阅
    private String status;// Y,任务的状态 1.待办 2.已办 3.待阅 4.已阅 5.删除
    private String owner;// Y,待办的处理人的唯一ID(主数据的pk_psndoc)
    private String sender;// N,起草人姓名(第一个发起人)
    private String depart;// N,起草人部门名称(第一个发起人)
    private String agree_url;// N,同意流程的URL，用户通过该链接来操作该流程，执行同意操作；如果没有，字段值为空。
    private String deny_url;// N,退回流程的URL，用户通过该链接来操作该流程，执行退回操作；如果没有，字段值为空。
    private String record_status;// N,待办记录状态 1.新增 2.状态修改
    private String priority;// N,紧急程度 1.低 2.中 3.高
    private String mobileUrl;// N,手机url

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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
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

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }
}
