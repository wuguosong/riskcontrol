
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_Wx_News complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
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
     * 获取news属性的值。
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
     * 设置news属性的值。
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
