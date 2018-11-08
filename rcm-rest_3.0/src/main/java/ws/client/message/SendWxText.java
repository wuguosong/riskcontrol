
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
 *         &lt;element name="text_message" type="{http://tempuri.org/}Message_Wx_Text" minOccurs="0"/>
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
    "textMessage"
})
@XmlRootElement(name = "SendWx_Text")
public class SendWxText {

    @XmlElement(name = "text_message")
    protected MessageWxText textMessage;

    /**
     * Gets the value of the textMessage property.
     * 
     * @return
     *     possible object is
     *     {@link MessageWxText }
     *     
     */
    public MessageWxText getTextMessage() {
        return textMessage;
    }

    /**
     * Sets the value of the textMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageWxText }
     *     
     */
    public void setTextMessage(MessageWxText value) {
        this.textMessage = value;
    }

}
