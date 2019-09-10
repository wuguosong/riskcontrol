
package ws.client.message;

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
@WebService(name = "BEWG_Message_ServiceSoap", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface BEWGMessageServiceSoap {


    /**
     * 
     * @param message
     * @return
     *     returns ws.client.message.MessageBack
     */
    @WebMethod(operationName = "SendSMS", action = "http://tempuri.org/SendSMS")
    @WebResult(name = "SendSMSResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SendSMS", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendSMS")
    @ResponseWrapper(localName = "SendSMSResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendSMSResponse")
    public MessageBack sendSMS(
        @WebParam(name = "message", targetNamespace = "http://tempuri.org/")
        MessageSMS message);

    /**
     * 
     * @param id
     * @param code
     * @return
     *     returns ws.client.message.MessageStatus
     */
    @WebMethod(operationName = "GetSmsStatus", action = "http://tempuri.org/GetSmsStatus")
    @WebResult(name = "GetSmsStatusResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetSmsStatus", targetNamespace = "http://tempuri.org/", className = "ws.client.message.GetSmsStatus")
    @ResponseWrapper(localName = "GetSmsStatusResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.GetSmsStatusResponse")
    public MessageStatus getSmsStatus(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        String id,
        @WebParam(name = "code", targetNamespace = "http://tempuri.org/")
        String code);

    /**
     * 
     * @param message
     * @return
     *     returns ws.client.message.MessageBack
     */
    @WebMethod(operationName = "SendDT", action = "http://tempuri.org/SendDT")
    @WebResult(name = "SendDTResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SendDT", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendDT")
    @ResponseWrapper(localName = "SendDTResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendDTResponse")
    public MessageBack sendDT(
        @WebParam(name = "message", targetNamespace = "http://tempuri.org/")
        MessageDT message);

    /**
     * 
     * @param textMessage
     * @return
     *     returns ws.client.message.MessageBack
     */
    @WebMethod(operationName = "SendDT_Text", action = "http://tempuri.org/SendDT_Text")
    @WebResult(name = "SendDT_TextResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SendDT_Text", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendDTText")
    @ResponseWrapper(localName = "SendDT_TextResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendDTTextResponse")
    public MessageBack sendDTText(
        @WebParam(name = "text_message", targetNamespace = "http://tempuri.org/")
        MessageDTText textMessage);

    /**
     * 
     * @param linkMessage
     * @return
     *     returns ws.client.message.MessageBack
     */
    @WebMethod(operationName = "SendDT_Link", action = "http://tempuri.org/SendDT_Link")
    @WebResult(name = "SendDT_LinkResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SendDT_Link", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendDTLink")
    @ResponseWrapper(localName = "SendDT_LinkResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendDTLinkResponse")
    public MessageBack sendDTLink(
        @WebParam(name = "link_message", targetNamespace = "http://tempuri.org/")
        MessageDTLink linkMessage);

    /**
     * 
     * @param id
     * @param code
     * @return
     *     returns ws.client.message.MessageStatus
     */
    @WebMethod(operationName = "GetDtStatus", action = "http://tempuri.org/GetDtStatus")
    @WebResult(name = "GetDtStatusResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetDtStatus", targetNamespace = "http://tempuri.org/", className = "ws.client.message.GetDtStatus")
    @ResponseWrapper(localName = "GetDtStatusResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.GetDtStatusResponse")
    public MessageStatus getDtStatus(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        String id,
        @WebParam(name = "code", targetNamespace = "http://tempuri.org/")
        String code);

    /**
     * 
     * @param message
     * @return
     *     returns ws.client.message.MessageBack
     */
    @WebMethod(operationName = "SendWx", action = "http://tempuri.org/SendWx")
    @WebResult(name = "SendWxResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SendWx", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendWx")
    @ResponseWrapper(localName = "SendWxResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendWxResponse")
    public MessageBack sendWx(
        @WebParam(name = "message", targetNamespace = "http://tempuri.org/")
        MessageWx message);

    /**
     * 
     * @param newsMessage
     * @return
     *     returns ws.client.message.MessageBack
     */
    @WebMethod(operationName = "SendWx_News", action = "http://tempuri.org/SendWx_News")
    @WebResult(name = "SendWx_NewsResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SendWx_News", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendWxNews")
    @ResponseWrapper(localName = "SendWx_NewsResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendWxNewsResponse")
    public MessageBack sendWxNews(
        @WebParam(name = "news_message", targetNamespace = "http://tempuri.org/")
        MessageWxNews newsMessage);

    /**
     * 
     * @param textMessage
     * @return
     *     returns ws.client.message.MessageBack
     */
    @WebMethod(operationName = "SendWx_Text", action = "http://tempuri.org/SendWx_Text")
    @WebResult(name = "SendWx_TextResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SendWx_Text", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendWxText")
    @ResponseWrapper(localName = "SendWx_TextResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendWxTextResponse")
    public MessageBack sendWxText(
        @WebParam(name = "text_message", targetNamespace = "http://tempuri.org/")
        MessageWxText textMessage);

    /**
     * 
     * @param id
     * @param code
     * @return
     *     returns ws.client.message.MessageStatus
     */
    @WebMethod(operationName = "GetWxStatus", action = "http://tempuri.org/GetWxStatus")
    @WebResult(name = "GetWxStatusResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetWxStatus", targetNamespace = "http://tempuri.org/", className = "ws.client.message.GetWxStatus")
    @ResponseWrapper(localName = "GetWxStatusResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.GetWxStatusResponse")
    public MessageStatus getWxStatus(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        String id,
        @WebParam(name = "code", targetNamespace = "http://tempuri.org/")
        String code);

    /**
     * 
     * @param message
     * @return
     *     returns ws.client.message.MessageBack
     */
    @WebMethod(operationName = "SendEmail", action = "http://tempuri.org/SendEmail")
    @WebResult(name = "SendEmailResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SendEmail", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendEmail")
    @ResponseWrapper(localName = "SendEmailResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.SendEmailResponse")
    public MessageBack sendEmail(
        @WebParam(name = "message", targetNamespace = "http://tempuri.org/")
        MessageEmail message);

    /**
     * 
     * @param id
     * @param code
     * @return
     *     returns ws.client.message.MessageStatus
     */
    @WebMethod(operationName = "GetEmailStatus", action = "http://tempuri.org/GetEmailStatus")
    @WebResult(name = "GetEmailStatusResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetEmailStatus", targetNamespace = "http://tempuri.org/", className = "ws.client.message.GetEmailStatus")
    @ResponseWrapper(localName = "GetEmailStatusResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.GetEmailStatusResponse")
    public MessageStatus getEmailStatus(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        String id,
        @WebParam(name = "code", targetNamespace = "http://tempuri.org/")
        String code);

    /**
     * 
     * @param token
     * @return
     *     returns ws.client.message.MessageBack
     */
    @WebMethod(operationName = "RefreshCache", action = "http://tempuri.org/RefreshCache")
    @WebResult(name = "RefreshCacheResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "RefreshCache", targetNamespace = "http://tempuri.org/", className = "ws.client.message.RefreshCache")
    @ResponseWrapper(localName = "RefreshCacheResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.message.RefreshCacheResponse")
    public MessageBack refreshCache(
        @WebParam(name = "token", targetNamespace = "http://tempuri.org/")
        String token);

}