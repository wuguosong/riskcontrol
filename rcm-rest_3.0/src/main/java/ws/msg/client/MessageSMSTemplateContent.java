
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_SMS_Template_Content complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Message_SMS_Template_Content"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Params" type="{http://tempuri.org/}ArrayOfString" minOccurs="0"/&gt;
 *         &lt;element name="TemplateId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Sign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message_SMS_Template_Content", propOrder = {
    "params",
    "templateId",
    "sign"
})
public class MessageSMSTemplateContent {

    @XmlElement(name = "Params")
    protected ArrayOfString params;
    @XmlElement(name = "TemplateId")
    protected int templateId;
    @XmlElement(name = "Sign")
    protected String sign;

    /**
     * 获取params属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getParams() {
        return params;
    }

    /**
     * 设置params属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setParams(ArrayOfString value) {
        this.params = value;
    }

    /**
     * 获取templateId属性的值。
     * 
     */
    public int getTemplateId() {
        return templateId;
    }

    /**
     * 设置templateId属性的值。
     * 
     */
    public void setTemplateId(int value) {
        this.templateId = value;
    }

    /**
     * 获取sign属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSign() {
        return sign;
    }

    /**
     * 设置sign属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSign(String value) {
        this.sign = value;
    }

}
