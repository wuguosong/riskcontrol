
package com.yk.rcm.ws.client.contract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GetInvestmentItemsMessageResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getInvestmentItemsMessageResult"
})
@XmlRootElement(name = "GetInvestmentItemsMessageResponse")
public class GetInvestmentItemsMessageResponse {

    @XmlElement(name = "GetInvestmentItemsMessageResult")
    protected String getInvestmentItemsMessageResult;

    /**
     * ��ȡgetInvestmentItemsMessageResult���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetInvestmentItemsMessageResult() {
        return getInvestmentItemsMessageResult;
    }

    /**
     * ����getInvestmentItemsMessageResult���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetInvestmentItemsMessageResult(String value) {
        this.getInvestmentItemsMessageResult = value;
    }

}
