
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
 *         &lt;element name="SendWx_NewsResult" type="{http://tempuri.org/}MessageBack" minOccurs="0"/&gt;
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
    "sendWxNewsResult"
})
@XmlRootElement(name = "SendWx_NewsResponse")
public class SendWxNewsResponse {

    @XmlElement(name = "SendWx_NewsResult")
    protected MessageBack sendWxNewsResult;

    /**
     * 获取sendWxNewsResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link MessageBack }
     *     
     */
    public MessageBack getSendWxNewsResult() {
        return sendWxNewsResult;
    }

    /**
     * 设置sendWxNewsResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link MessageBack }
     *     
     */
    public void setSendWxNewsResult(MessageBack value) {
        this.sendWxNewsResult = value;
    }

}
