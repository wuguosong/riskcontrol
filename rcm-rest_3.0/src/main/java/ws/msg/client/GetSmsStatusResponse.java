
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
 *         &lt;element name="GetSmsStatusResult" type="{http://tempuri.org/}MessageStatus" minOccurs="0"/&gt;
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
    "getSmsStatusResult"
})
@XmlRootElement(name = "GetSmsStatusResponse")
public class GetSmsStatusResponse {

    @XmlElement(name = "GetSmsStatusResult")
    protected MessageStatus getSmsStatusResult;

    /**
     * ��ȡgetSmsStatusResult���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link MessageStatus }
     *     
     */
    public MessageStatus getGetSmsStatusResult() {
        return getSmsStatusResult;
    }

    /**
     * ����getSmsStatusResult���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link MessageStatus }
     *     
     */
    public void setGetSmsStatusResult(MessageStatus value) {
        this.getSmsStatusResult = value;
    }

}
