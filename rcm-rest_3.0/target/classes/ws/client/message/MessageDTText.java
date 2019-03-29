
package ws.client.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Message_DT_Text complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Message_DT_Text">
 *   &lt;complexContent>
 *     &lt;extension base="{http://tempuri.org/}Message_DT">
 *       &lt;sequence>
 *         &lt;element name="text" type="{http://tempuri.org/}Message_DT_Text_Content" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message_DT_Text", propOrder = {
    "text"
})
public class MessageDTText
    extends MessageDT
{

    protected MessageDTTextContent text;

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link MessageDTTextContent }
     *     
     */
    public MessageDTTextContent getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageDTTextContent }
     *     
     */
    public void setText(MessageDTTextContent value) {
        this.text = value;
    }

}
