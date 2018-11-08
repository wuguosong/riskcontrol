
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
 *         &lt;element name="UpdateRiskAuditInfoResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "updateRiskAuditInfoResult"
})
@XmlRootElement(name = "UpdateRiskAuditInfoResponse")
public class UpdateRiskAuditInfoResponse {

    @XmlElement(name = "UpdateRiskAuditInfoResult")
    protected String updateRiskAuditInfoResult;

    /**
     * Gets the value of the updateRiskAuditInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateRiskAuditInfoResult() {
        return updateRiskAuditInfoResult;
    }

    /**
     * Sets the value of the updateRiskAuditInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateRiskAuditInfoResult(String value) {
        this.updateRiskAuditInfoResult = value;
    }

}
