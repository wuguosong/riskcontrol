
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
 *         &lt;element name="SendDT_TextResult" type="{http://tempuri.org/}MessageBack" minOccurs="0"/>
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
    "sendDTTextResult"
})
@XmlRootElement(name = "SendDT_TextResponse")
public class SendDTTextResponse {

    @XmlElement(name = "SendDT_TextResult")
    protected MessageBack sendDTTextResult;

    /**
     * Gets the value of the sendDTTextResult property.
     * 
     * @return
     *     possible object is
     *     {@link MessageBack }
     *     
     */
    public MessageBack getSendDTTextResult() {
        return sendDTTextResult;
    }

    /**
     * Sets the value of the sendDTTextResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageBack }
     *     
     */
    public void setSendDTTextResult(MessageBack value) {
        this.sendDTTextResult = value;
    }

}
