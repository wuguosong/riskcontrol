
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
 *         &lt;element name="news_message" type="{http://tempuri.org/}Message_Wx_News" minOccurs="0"/&gt;
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
    "newsMessage"
})
@XmlRootElement(name = "SendWx_News")
public class SendWxNews {

    @XmlElement(name = "news_message")
    protected MessageWxNews newsMessage;

    /**
     * ��ȡnewsMessage���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link MessageWxNews }
     *     
     */
    public MessageWxNews getNewsMessage() {
        return newsMessage;
    }

    /**
     * ����newsMessage���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link MessageWxNews }
     *     
     */
    public void setNewsMessage(MessageWxNews value) {
        this.newsMessage = value;
    }

}
