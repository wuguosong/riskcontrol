
package ws.client.tz;

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
 *         &lt;element name="GetPreDecisionInfoResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getPreDecisionInfoResult"
})
@XmlRootElement(name = "GetPreDecisionInfoResponse")
public class GetPreDecisionInfoResponse {

    @XmlElement(name = "GetPreDecisionInfoResult")
    protected String getPreDecisionInfoResult;

    /**
     * Gets the value of the getPreDecisionInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetPreDecisionInfoResult() {
        return getPreDecisionInfoResult;
    }

    /**
     * Sets the value of the getPreDecisionInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetPreDecisionInfoResult(String value) {
        this.getPreDecisionInfoResult = value;
    }

}
