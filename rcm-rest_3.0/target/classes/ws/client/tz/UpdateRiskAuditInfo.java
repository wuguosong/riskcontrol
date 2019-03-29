
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
 *         &lt;element name="CustomerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RiskStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AuditReport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "customerId",
    "riskStatus",
    "auditReport"
})
@XmlRootElement(name = "UpdateRiskAuditInfo")
public class UpdateRiskAuditInfo {

    @XmlElement(name = "CustomerId")
    protected String customerId;
    @XmlElement(name = "RiskStatus")
    protected String riskStatus;
    @XmlElement(name = "AuditReport")
    protected String auditReport;

    /**
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerId(String value) {
        this.customerId = value;
    }

    /**
     * Gets the value of the riskStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiskStatus() {
        return riskStatus;
    }

    /**
     * Sets the value of the riskStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiskStatus(String value) {
        this.riskStatus = value;
    }

    /**
     * Gets the value of the auditReport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuditReport() {
        return auditReport;
    }

    /**
     * Sets the value of the auditReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuditReport(String value) {
        this.auditReport = value;
    }

}
