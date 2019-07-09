package ws.tz.client;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class was generated by Apache CXF 3.3.1
 * 2019-07-05T11:05:37.825+08:00
 * Generated source version: 3.3.1
 *
 */
@WebServiceClient(name = "ProjectServiceForTzSoap",
                  wsdlLocation = "http://localhost/rcm-rest/webservice/wsForTz?wsdl",
                  targetNamespace = "http://service.tz.ws/")
public class ProjectServiceForTzSoap extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://service.tz.ws/", "ProjectServiceForTzSoap");
    public final static QName ProjectServiceForTzImplPort = new QName("http://service.tz.ws/", "ProjectServiceForTzImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost/rcm-rest/webservice/wsForTz?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ProjectServiceForTzSoap.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "http://localhost/rcm-rest/webservice/wsForTz?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ProjectServiceForTzSoap(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ProjectServiceForTzSoap(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ProjectServiceForTzSoap() {
        super(WSDL_LOCATION, SERVICE);
    }

    public ProjectServiceForTzSoap(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public ProjectServiceForTzSoap(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public ProjectServiceForTzSoap(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns ProjectServiceForTz
     */
    @WebEndpoint(name = "ProjectServiceForTzImplPort")
    public ProjectServiceForTz getProjectServiceForTzImplPort() {
        return super.getPort(ProjectServiceForTzImplPort, ProjectServiceForTz.class);
    }

    /**
     *
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ProjectServiceForTz
     */
    @WebEndpoint(name = "ProjectServiceForTzImplPort")
    public ProjectServiceForTz getProjectServiceForTzImplPort(WebServiceFeature... features) {
        return super.getPort(ProjectServiceForTzImplPort, ProjectServiceForTz.class, features);
    }

}