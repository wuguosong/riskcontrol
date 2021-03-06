
package ws.client.tz;

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
@WebServiceClient(name = "RiskService", targetNamespace = "http://tempuri.org/", wsdlLocation = "http://ipm.bewg.net.cn/RiskWebService/RiskService.asmx?wsdl")
public class RiskService
    extends Service
{

    private final static URL RISKSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(ws.client.tz.RiskService.class.getName());

    static {
        URL url = null;
        String clientUrl = PropertiesUtil.getProperty("ws_tz_risk_addr");
        try {
            URL baseUrl;
            baseUrl = ws.client.tz.RiskService.class.getResource(".");
            url = new URL(baseUrl, clientUrl);
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: '"+clientUrl+"', retrying as a local file");
            logger.warning(e.getMessage());
        }
        RISKSERVICE_WSDL_LOCATION = url;
    }

    public RiskService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RiskService() {
        super(RISKSERVICE_WSDL_LOCATION, new QName("http://tempuri.org/", "RiskService"));
    }

    /**
     * 
     * @return
     *     returns RiskServiceSoap
     */
    @WebEndpoint(name = "RiskServiceSoap")
    public RiskServiceSoap getRiskServiceSoap() {
        return super.getPort(new QName("http://tempuri.org/", "RiskServiceSoap"), RiskServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RiskServiceSoap
     */
    @WebEndpoint(name = "RiskServiceSoap")
    public RiskServiceSoap getRiskServiceSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "RiskServiceSoap"), RiskServiceSoap.class, features);
    }

}
