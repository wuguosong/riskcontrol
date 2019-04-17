
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="link_message" type="{http://tempuri.org/}Message_DT_Link" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "linkMessage"
})
@XmlRootElement(name = "SendDT_Link")
public class SendDTLink {

    @XmlElement(name = "link_message")
    protected MessageDTLink linkMessage;

    /**
     * 获取linkMessage属性的值。
     * 
     * @return
     *     possible object is
     *     {@link MessageDTLink }
     *     
     */
    public MessageDTLink getLinkMessage() {
        return linkMessage;
    }

    /**
     * 设置linkMessage属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link MessageDTLink }
     *     
     */
    public void setLinkMessage(MessageDTLink value) {
        this.linkMessage = value;
    }

}
