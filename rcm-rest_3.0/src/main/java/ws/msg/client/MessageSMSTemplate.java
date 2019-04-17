
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_SMS_Template complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="Message_SMS_Template"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://tempuri.org/}Message_SMS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="templates" type="{http://tempuri.org/}Message_SMS_Template_Content" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message_SMS_Template", propOrder = {
    "templates"
})
public class MessageSMSTemplate
    extends MessageSMS
{

    protected MessageSMSTemplateContent templates;

    /**
     * ��ȡtemplates���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link MessageSMSTemplateContent }
     *     
     */
    public MessageSMSTemplateContent getTemplates() {
        return templates;
    }

    /**
     * ����templates���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link MessageSMSTemplateContent }
     *     
     */
    public void setTemplates(MessageSMSTemplateContent value) {
        this.templates = value;
    }

}
