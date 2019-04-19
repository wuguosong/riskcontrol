
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
 *         &lt;element name="SendDT_LinkResult" type="{http://tempuri.org/}MessageBack" minOccurs="0"/&gt;
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
    "sendDTLinkResult"
})
@XmlRootElement(name = "SendDT_LinkResponse")
public class SendDTLinkResponse {

    @XmlElement(name = "SendDT_LinkResult")
    protected MessageBack sendDTLinkResult;

    /**
     * 获取sendDTLinkResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link MessageBack }
     *     
     */
    public MessageBack getSendDTLinkResult() {
        return sendDTLinkResult;
    }

    /**
     * 设置sendDTLinkResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link MessageBack }
     *     
     */
    public void setSendDTLinkResult(MessageBack value) {
        this.sendDTLinkResult = value;
    }

}
