package ws.todo.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 代办列表头信息
 */
@XmlRootElement(name="header")
public class Header {
    private String syscode;

    public String getSyscode() {
        return syscode;
    }

    public void setSyscode(String syscode) {
        this.syscode = syscode;
    }
}
