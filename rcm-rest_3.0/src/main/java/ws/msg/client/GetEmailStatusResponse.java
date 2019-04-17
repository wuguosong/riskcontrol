
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
 *         &lt;element name="GetEmailStatusResult" type="{http://tempuri.org/}MessageStatus" minOccurs="0"/&gt;
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
    "getEmailStatusResult"
})
@XmlRootElement(name = "GetEmailStatusResponse")
public class GetEmailStatusResponse {

    @XmlElement(name = "GetEmailStatusResult")
    protected MessageStatus getEmailStatusResult;

    /**
     * 获取getEmailStatusResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link MessageStatus }
     *     
     */
    public MessageStatus getGetEmailStatusResult() {
        return getEmailStatusResult;
    }

    /**
     * 设置getEmailStatusResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link MessageStatus }
     *     
     */
    public void setGetEmailStatusResult(MessageStatus value) {
        this.getEmailStatusResult = value;
    }

}
