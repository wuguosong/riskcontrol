
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
 *         &lt;element name="RefreshCacheResult" type="{http://tempuri.org/}MessageBack" minOccurs="0"/>
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
    "refreshCacheResult"
})
@XmlRootElement(name = "RefreshCacheResponse")
public class RefreshCacheResponse {

    @XmlElement(name = "RefreshCacheResult")
    protected MessageBack refreshCacheResult;

    /**
     * Gets the value of the refreshCacheResult property.
     * 
     * @return
     *     possible object is
     *     {@link MessageBack }
     *     
     */
    public MessageBack getRefreshCacheResult() {
        return refreshCacheResult;
    }

    /**
     * Sets the value of the refreshCacheResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageBack }
     *     
     */
    public void setRefreshCacheResult(MessageBack value) {
        this.refreshCacheResult = value;
    }

}
