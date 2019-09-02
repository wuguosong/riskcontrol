
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
 *         &lt;element name="text_message" type="{http://tempuri.org/}Message_DT_Text" minOccurs="0"/&gt;
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
    "textMessage"
})
@XmlRootElement(name = "SendDT_Text")
public class SendDTText {

    @XmlElement(name = "text_message")
    protected MessageDTText textMessage;

    /**
     * 获取textMessage属性的值。
     * 
     * @return
     *     possible object is
     *     {@link MessageDTText }
     *     
     */
    public MessageDTText getTextMessage() {
        return textMessage;
    }

    /**
     * 设置textMessage属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link MessageDTText }
     *     
     */
    public void setTextMessage(MessageDTText value) {
        this.textMessage = value;
    }

}
