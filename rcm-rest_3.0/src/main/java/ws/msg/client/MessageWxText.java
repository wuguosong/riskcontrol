
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_Wx_Text complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="Message_Wx_Text"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://tempuri.org/}Message_Wx"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="text" type="{http://tempuri.org/}Message_Wx_Text_Content" minOccurs="0"/&gt;
 *         &lt;element name="safe" type="{http://www.w3.org/2001/XMLSchema}unsignedByte"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
     * ��ȡtext���Ե�ֵ��
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
     * ����text���Ե�ֵ��
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
     * ��ȡsafe���Ե�ֵ��
     * 
     */
    public short getSafe() {
        return safe;
    }

    /**
     * ����safe���Ե�ֵ��
     * 
     */
    public void setSafe(short value) {
        this.safe = value;
    }

}
