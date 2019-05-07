package ws.agency.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Administrator on 2019/5/7 0007.
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
