
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_Wx_News complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="Message_Wx_News"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://tempuri.org/}Message_Wx"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="news" type="{http://tempuri.org/}Message_Wx_News_Content" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message_Wx_News", propOrder = {
    "news"
})
public class MessageWxNews
    extends MessageWx
{

    protected MessageWxNewsContent news;

    /**
     * ��ȡnews���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link MessageWxNewsContent }
     *     
     */
    public MessageWxNewsContent getNews() {
        return news;
    }

    /**
     * ����news���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link MessageWxNewsContent }
     *     
     */
    public void setNews(MessageWxNewsContent value) {
        this.news = value;
    }

}
