
package ws.client.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Message_DT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Message_DT">
 *   &lt;complexContent>
 *     &lt;extension base="{http://tempuri.org/}QueryBase">
 *       &lt;sequence>
 *         &lt;element name="touser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="toparty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message_DT", propOrder = {
    "touser",
    "toparty"
})
@XmlSeeAlso({
    MessageDTText.class,
    MessageDTLink.class
})
public abstract class MessageDT
    extends QueryBase
{

    protected String touser;
    protected String toparty;

    /**
     * Gets the value of the touser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTouser() {
        return touser;
    }

    /**
     * Sets the value of the touser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTouser(String value) {
        this.touser = value;
    }

    /**
     * Gets the value of the toparty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToparty() {
        return toparty;
    }

    /**
     * Sets the value of the toparty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToparty(String value) {
        this.toparty = value;
    }

}
