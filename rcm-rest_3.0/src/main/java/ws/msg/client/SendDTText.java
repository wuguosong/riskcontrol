
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
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
     * ��ȡtextMessage���Ե�ֵ��
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
     * ����textMessage���Ե�ֵ��
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
