
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
     * ��ȡtemplate���Ե�ֵ��
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
     * ����template���Ե�ֵ��
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
