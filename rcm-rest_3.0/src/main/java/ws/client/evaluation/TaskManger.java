
package ws.client.evaluation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import util.PropertiesUtil;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "TaskManger", targetNamespace = "http://tempuri.org/", wsdlLocation = "http://10.10.20.86:8898/HeTaskMangerWeb/services/TaskManger?wsdl")
public class TaskManger
    extends Service
{

    private final static URL TASKMANGER_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(ws.client.evaluation.TaskManger.class.getName());

    static {
        URL url = null;
        String clientUrl = PropertiesUtil.getProperty("ws_hpg_send_addr");
        try {
            URL baseUrl;
            baseUrl = ws.client.evaluation.TaskManger.class.getResource(".");
            url = new URL(baseUrl, clientUrl);
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: '"+clientUrl+"', retrying as a local file");
            logger.warning(e.getMessage());
        }
        TASKMANGER_WSDL_LOCATION = url;
    }

    public TaskManger(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TaskManger() {
        super(TASKMANGER_WSDL_LOCATION, new QName("http://tempuri.org/", "TaskManger"));
    }

    /**
     * 
     * @return
     *     returns TaskMangerPortType
     */
    @WebEndpoint(name = "TaskMangerHttpPort")
    public TaskMangerPortType getTaskMangerHttpPort() {
        return super.getPort(new QName("http://tempuri.org/", "TaskMangerHttpPort"), TaskMangerPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns TaskMangerPortType
     */
    @WebEndpoint(name = "TaskMangerHttpPort")
    public TaskMangerPortType getTaskMangerHttpPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "TaskMangerHttpPort"), TaskMangerPortType.class, features);
    }

}
