
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
 *         &lt;element name="HandleEmailServerResult" type="{http://tempuri.org/}MessageBack" minOccurs="0"/&gt;
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
    "handleEmailServerResult"
})
@XmlRootElement(name = "HandleEmailServerResponse")
public class HandleEmailServerResponse {

    @XmlElement(name = "HandleEmailServerResult")
    protected MessageBack handleEmailServerResult;

    /**
     * ��ȡhandleEmailServerResult���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link MessageBack }
     *     
     */
    public MessageBack getHandleEmailServerResult() {
        return handleEmailServerResult;
    }

    /**
     * ����handleEmailServerResult���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link MessageBack }
     *     
     */
    public void setHandleEmailServerResult(MessageBack value) {
        this.handleEmailServerResult = value;
    }

}
