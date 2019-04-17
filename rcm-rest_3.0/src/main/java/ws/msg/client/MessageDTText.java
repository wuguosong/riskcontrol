
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_DT_Text complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Message_DT_Text"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://tempuri.org/}Message_DT"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="text" type="{http://tempuri.org/}Message_DT_Text_Content" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
     * 获取text属性的值。
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
     * 设置text属性的值。
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
