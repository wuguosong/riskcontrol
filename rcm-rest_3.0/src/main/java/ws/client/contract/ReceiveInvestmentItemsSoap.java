package ws.client.contract;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "ReceiveInvestmentItemsSoap", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
interface ReceiveInvestmentItemsSoap {


    /**
     * 
     * @param requestJsonStr
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetInvestmentItemsMessage", action = "http://tempuri.org/GetInvestmentItemsMessage")
    @WebResult(name = "GetInvestmentItemsMessageResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetInvestmentItemsMessage", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GetInvestmentItemsMessage")
    @ResponseWrapper(localName = "GetInvestmentItemsMessageResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GetInvestmentItemsMessageResponse")
    public String getInvestmentItemsMessage(
        @WebParam(name = "requestJsonStr", targetNamespace = "http://tempuri.org/")
        String requestJsonStr);

}
