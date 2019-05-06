package ws.agency.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/25 0025.
 */
@XmlRootElement(name="todo_list")
public class todo_list {
    @XmlElement(name="xmlname")
    private header header;
    private List<todo_info> todolist = new ArrayList<todo_info>();

    public todo_list.header getHeader() {
        return header;
    }

    public void setHeader(todo_list.header header) {
        this.header = header;
    }

    public List<todo_info> getTodolist() {
        return todolist;
    }

    public void setTodolist(List<todo_info> todolist) {
        this.todolist = todolist;
    }

    public static class header {
        private String syscode;

        public String getSyscode() {
            return syscode;
        }

        public void setSyscode(String syscode) {
            this.syscode = syscode;
        }
    }

    public static class todo_info {
        private String uniid;
        private String title;
        private String sender;
        private String depart;
        private String url;
        private String agreeurl;
        private String denyurl;
        private String owner;
        private String created;
        private String type;
        private String status;
        private String record_status;
        private String priority;
        private String mobileUrl;

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

        public String getAgreeurl() {
            return agreeurl;
        }

        public void setAgreeurl(String agreeurl) {
            this.agreeurl = agreeurl;
        }

        public String getDenyurl() {
            return denyurl;
        }

        public void setDenyurl(String denyurl) {
            this.denyurl = denyurl;
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
}
