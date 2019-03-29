
package ws.client.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Message_Wx_News_Content complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Message_Wx_News_Content">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="articles" type="{http://tempuri.org/}ArrayOfArticle" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
     * Gets the value of the articles property.
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
     * Sets the value of the articles property.
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
