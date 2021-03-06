package ws.tz.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.3.1
 * 2019-07-05T11:05:37.731+08:00
 * Generated source version: 3.3.1
 *
 */
@WebService(targetNamespace = "http://service.tz.ws/", name = "ProjectServiceForTz")
@XmlSeeAlso({ObjectFactory.class})
public interface ProjectServiceForTz {

    @WebMethod
    @RequestWrapper(localName = "deletePfr", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.DeletePfr")
    @ResponseWrapper(localName = "deletePfrResponse", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.DeletePfrResponse")
    @WebResult(name = "return", targetNamespace = "")
    public String deletePfr(
            @WebParam(name = "json", targetNamespace = "")
                    String json
    );

    @WebMethod
    @RequestWrapper(localName = "createPreFeedBack", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.CreatePreFeedBack")
    @ResponseWrapper(localName = "createPreFeedBackResponse", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.CreatePreFeedBackResponse")
    @WebResult(name = "return", targetNamespace = "")
    public String createPreFeedBack(
            @WebParam(name = "json", targetNamespace = "")
                    String json
    );

    @WebMethod
    @RequestWrapper(localName = "deletePre", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.DeletePre")
    @ResponseWrapper(localName = "deletePreResponse", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.DeletePreResponse")
    @WebResult(name = "return", targetNamespace = "")
    public String deletePre(
            @WebParam(name = "json", targetNamespace = "")
                    String json
    );

    @WebMethod
    @RequestWrapper(localName = "createPre", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.CreatePre")
    @ResponseWrapper(localName = "createPreResponse", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.CreatePreResponse")
    @WebResult(name = "return", targetNamespace = "")
    public String createPre(
            @WebParam(name = "json", targetNamespace = "")
                    String json
    );

    @WebMethod
    @RequestWrapper(localName = "createPfr", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.CreatePfr")
    @ResponseWrapper(localName = "createPfrResponse", targetNamespace = "http://service.tz.ws/", className = "ws.tz.client.CreatePfrResponse")
    @WebResult(name = "return", targetNamespace = "")
    public String createPfr(
            @WebParam(name = "json", targetNamespace = "")
                    String json
    );
}
