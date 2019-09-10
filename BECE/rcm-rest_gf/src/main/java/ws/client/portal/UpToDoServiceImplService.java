
package ws.client.portal;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

import util.PropertiesUtil;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "UpToDoServiceImplService", targetNamespace = "http://impl.service.ws.com/", wsdlLocation = "http://10.10.20.34:9082/ws/upToDo?wsdl")
public class UpToDoServiceImplService
    extends Service
{

    private final static URL UPTODOSERVICEIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException UPTODOSERVICEIMPLSERVICE_EXCEPTION;
    private final static QName UPTODOSERVICEIMPLSERVICE_QNAME = new QName("http://impl.service.ws.com/", "UpToDoServiceImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL(PropertiesUtil.getProperty("ws.portal"));
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        UPTODOSERVICEIMPLSERVICE_WSDL_LOCATION = url;
        UPTODOSERVICEIMPLSERVICE_EXCEPTION = e;
    }

    public UpToDoServiceImplService() {
        super(__getWsdlLocation(), UPTODOSERVICEIMPLSERVICE_QNAME);
    }

    public UpToDoServiceImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), UPTODOSERVICEIMPLSERVICE_QNAME);
    }

    public UpToDoServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, UPTODOSERVICEIMPLSERVICE_QNAME);
    }

    public UpToDoServiceImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, UPTODOSERVICEIMPLSERVICE_QNAME);
    }

    public UpToDoServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public UpToDoServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName);
    }

    /**
     * 
     * @return
     *     returns IUpToDoService
     */
    @WebEndpoint(name = "todoPort")
    public IUpToDoService getTodoPort() {
        return super.getPort(new QName("http://impl.service.ws.com/", "todoPort"), IUpToDoService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IUpToDoService
     */
    @WebEndpoint(name = "todoPort")
    public IUpToDoService getTodoPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://impl.service.ws.com/", "todoPort"), IUpToDoService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (UPTODOSERVICEIMPLSERVICE_EXCEPTION!= null) {
            throw UPTODOSERVICEIMPLSERVICE_EXCEPTION;
        }
        return UPTODOSERVICEIMPLSERVICE_WSDL_LOCATION;
    }

}