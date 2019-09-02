
package ws.client.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="news_message" type="{http://tempuri.org/}Message_Wx_News" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
     * Gets the value of the newsMessage property.
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
     * Sets the value of the newsMessage property.
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
