
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
 *         &lt;element name="GetWxStatusResult" type="{http://tempuri.org/}MessageStatus" minOccurs="0"/>
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
    "getWxStatusResult"
})
@XmlRootElement(name = "GetWxStatusResponse")
public class GetWxStatusResponse {

    @XmlElement(name = "GetWxStatusResult")
    protected MessageStatus getWxStatusResult;

    /**
     * Gets the value of the getWxStatusResult property.
     * 
     * @return
     *     possible object is
     *     {@link MessageStatus }
     *     
     */
    public MessageStatus getGetWxStatusResult() {
        return getWxStatusResult;
    }

    /**
     * Sets the value of the getWxStatusResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageStatus }
     *     
     */
    public void setGetWxStatusResult(MessageStatus value) {
        this.getWxStatusResult = value;
    }

}
