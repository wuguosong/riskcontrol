
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
     * ��ȡgetEmailStatusResult���Ե�ֵ��
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
     * ����getEmailStatusResult���Ե�ֵ��
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
