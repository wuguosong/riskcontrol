package ws.todo.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.3.1
 * 2019-04-25T16:03:35.120+08:00
 * Generated source version: 3.3.1
 *
 */
@WebServiceClient(name = "UpToDoServiceImplService",
                  wsdlLocation = "http://10.10.20.49:9116/ws/upToDo?wsdl",
                  targetNamespace = "http://impl.service.ws.com/")
public class UpToDoServiceImplService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://impl.service.ws.com/", "UpToDoServiceImplService");
    public final static QName TodoPort = new QName("http://impl.service.ws.com/", "todoPort");
    static {
        URL url = null;
        try {
            url = new URL("http://10.10.20.49:9116/ws/upToDo?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(UpToDoServiceImplService.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "http://10.10.20.49:9116/ws/upToDo?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public UpToDoServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public UpToDoServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public UpToDoServiceImplService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public UpToDoServiceImplService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public UpToDoServiceImplService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public UpToDoServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns IUpToDoService
     */
    @WebEndpoint(name = "todoPort")
    public IUpToDoService getTodoPort() {
        return super.getPort(TodoPort, IUpToDoService.class);
    }

    /**
     *
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IUpToDoService
     */
    @WebEndpoint(name = "todoPort")
    public IUpToDoService getTodoPort(WebServiceFeature... features) {
        return super.getPort(TodoPort, IUpToDoService.class, features);
    }

}