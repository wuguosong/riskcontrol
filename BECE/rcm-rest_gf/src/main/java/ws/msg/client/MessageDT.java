
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_DT complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Message_DT"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://tempuri.org/}QueryBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="touser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="toparty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
     * 获取touser属性的值。
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
     * 设置touser属性的值。
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
     * 获取toparty属性的值。
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
     * 设置toparty属性的值。
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
