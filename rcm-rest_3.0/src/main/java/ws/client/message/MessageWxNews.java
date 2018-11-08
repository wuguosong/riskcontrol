
package ws.client.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Message_Wx_News complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Message_Wx_News">
 *   &lt;complexContent>
 *     &lt;extension base="{http://tempuri.org/}Message_Wx">
 *       &lt;sequence>
 *         &lt;element name="news" type="{http://tempuri.org/}Message_Wx_News_Content" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
     * Gets the value of the news property.
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
     * Sets the value of the news property.
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
