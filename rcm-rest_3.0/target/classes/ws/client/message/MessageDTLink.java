
package ws.client.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Message_DT_Link complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Message_DT_Link">
 *   &lt;complexContent>
 *     &lt;extension base="{http://tempuri.org/}Message_DT">
 *       &lt;sequence>
 *         &lt;element name="link" type="{http://tempuri.org/}Message_DT_Link_Content" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message_DT_Link", propOrder = {
    "link"
})
public class MessageDTLink
    extends MessageDT
{

    protected MessageDTLinkContent link;

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link MessageDTLinkContent }
     *     
     */
    public MessageDTLinkContent getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageDTLinkContent }
     *     
     */
    public void setLink(MessageDTLinkContent value) {
        this.link = value;
    }

}
