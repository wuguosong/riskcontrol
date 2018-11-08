
package ws.client.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Message_Wx_Text complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Message_Wx_Text">
 *   &lt;complexContent>
 *     &lt;extension base="{http://tempuri.org/}Message_Wx">
 *       &lt;sequence>
 *         &lt;element name="text" type="{http://tempuri.org/}Message_Wx_Text_Content" minOccurs="0"/>
 *         &lt;element name="safe" type="{http://www.w3.org/2001/XMLSchema}unsignedByte"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message_Wx_Text", propOrder = {
    "text",
    "safe"
})
public class MessageWxText
    extends MessageWx
{

    protected MessageWxTextContent text;
    @XmlSchemaType(name = "unsignedByte")
    protected short safe;

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link MessageWxTextContent }
     *     
     */
    public MessageWxTextContent getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageWxTextContent }
     *     
     */
    public void setText(MessageWxTextContent value) {
        this.text = value;
    }

    /**
     * Gets the value of the safe property.
     * 
     */
    public short getSafe() {
        return safe;
    }

    /**
     * Sets the value of the safe property.
     * 
     */
    public void setSafe(short value) {
        this.safe = value;
    }

}
