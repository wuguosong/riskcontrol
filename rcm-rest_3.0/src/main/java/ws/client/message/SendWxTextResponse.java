
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
 *         &lt;element name="SendWx_TextResult" type="{http://tempuri.org/}MessageBack" minOccurs="0"/>
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
    "sendWxTextResult"
})
@XmlRootElement(name = "SendWx_TextResponse")
public class SendWxTextResponse {

    @XmlElement(name = "SendWx_TextResult")
    protected MessageBack sendWxTextResult;

    /**
     * Gets the value of the sendWxTextResult property.
     * 
     * @return
     *     possible object is
     *     {@link MessageBack }
     *     
     */
    public MessageBack getSendWxTextResult() {
        return sendWxTextResult;
    }

    /**
     * Sets the value of the sendWxTextResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageBack }
     *     
     */
    public void setSendWxTextResult(MessageBack value) {
        this.sendWxTextResult = value;
    }

}
