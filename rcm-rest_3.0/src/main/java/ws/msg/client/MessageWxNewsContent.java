
package ws.msg.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Message_Wx_News_Content complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="Message_Wx_News_Content"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="articles" type="{http://tempuri.org/}ArrayOfArticle" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Message_Wx_News_Content", propOrder = {
    "articles"
})
public class MessageWxNewsContent {

    protected ArrayOfArticle articles;

    /**
     * ��ȡarticles���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfArticle }
     *     
     */
    public ArrayOfArticle getArticles() {
        return articles;
    }

    /**
     * ����articles���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfArticle }
     *     
     */
    public void setArticles(ArrayOfArticle value) {
        this.articles = value;
    }

}
