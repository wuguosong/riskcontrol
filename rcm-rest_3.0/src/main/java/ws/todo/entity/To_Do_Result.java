package ws.todo.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 结果列表
 */
@XmlRootElement(name="result")
public class To_Do_Result {
    private String syscode;// 业务系统编号
    private String sysinstanceid;// 业务系统实例ID
    private String code;// 接收结果 0:接收成功 1:接收失败
    private String message;// 处理消息

    public String getSyscode() {
        return syscode;
    }

    public void setSyscode(String syscode) {
        this.syscode = syscode;
    }

    public String getSysinstanceid() {
        return sysinstanceid;
    }

    public void setSysinstanceid(String sysinstanceid) {
        this.sysinstanceid = sysinstanceid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
