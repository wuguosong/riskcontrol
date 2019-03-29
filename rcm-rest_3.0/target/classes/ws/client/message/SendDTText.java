
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
 *         &lt;element name="text_message" type="{http://tempuri.org/}Message_DT_Text" minOccurs="0"/>
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
@XmlRootElement(name = "SendDT_Text")
public class SendDTText {

    @XmlElement(name = "text_message")
    protected MessageDTText textMessage;

    /**
     * Gets the value of the textMessage property.
     * 
     * @return
     *     possible object is
     *     {@link MessageDTText }
     *     
     */
    public MessageDTText getTextMessage() {
        return textMessage;
    }

    /**
     * Sets the value of the textMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageDTText }
     *     
     */
    public void setTextMessage(MessageDTText value) {
        this.textMessage = value;
    }

}
