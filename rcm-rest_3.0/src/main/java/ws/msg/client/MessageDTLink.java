
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_DT_Link complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Message_DT_Link"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://tempuri.org/}Message_DT"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="link" type="{http://tempuri.org/}Message_DT_Link_Content" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
     * 获取link属性的值。
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
     * 设置link属性的值。
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
