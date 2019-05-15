package ws.todo.entity;

/**
 * 代办返回数据
 */
public class TodoBack {
    public static final String CODE_SUCCESS = "0";
    private String sysCode;// 业务系统编号
    private String sysInstanceId;// 业务系统实例ID
    private String code;// 接收结果 0:接收成功 1:接收失败
    private String message;// 处理消息

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    public String getSysInstanceId() {
        return sysInstanceId;
    }

    public void setSysInstanceId(String sysInstanceId) {
        this.sysInstanceId = sysInstanceId;
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
