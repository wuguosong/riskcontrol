
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_Wx complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="Message_Wx"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://tempuri.org/}QueryBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="touser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="toparty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="totag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message_Wx", propOrder = {
    "touser",
    "toparty",
    "totag"
})
@XmlSeeAlso({
    MessageWxNews.class,
    MessageWxText.class
})
public abstract class MessageWx
    extends QueryBase
{

    protected String touser;
    protected String toparty;
    protected String totag;

    /**
     * ��ȡtouser���Ե�ֵ��
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
     * ����touser���Ե�ֵ��
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
     * ��ȡtoparty���Ե�ֵ��
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
     * ����toparty���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToparty(String value) {
        this.toparty = value;
    }

    /**
     * ��ȡtotag���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotag() {
        return totag;
    }

    /**
     * ����totag���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotag(String value) {
        this.totag = value;
    }

}
