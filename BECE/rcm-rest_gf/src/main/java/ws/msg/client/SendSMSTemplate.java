
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="template" type="{http://tempuri.org/}Message_SMS_Template" minOccurs="0"/&gt;
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
    "template"
})
@XmlRootElement(name = "SendSMS_Template")
public class SendSMSTemplate {

    protected MessageSMSTemplate template;

    /**
     * 获取template属性的值。
     * 
     * @return
     *     possible object is
     *     {@link MessageSMSTemplate }
     *     
     */
    public MessageSMSTemplate getTemplate() {
        return template;
    }

    /**
     * 设置template属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link MessageSMSTemplate }
     *     
     */
    public void setTemplate(MessageSMSTemplate value) {
        this.template = value;
    }

}
