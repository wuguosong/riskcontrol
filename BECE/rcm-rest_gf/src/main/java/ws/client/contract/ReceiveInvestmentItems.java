package ws.client.contract;

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
//@WebServiceClient(name = "ReceiveInvestmentItems", targetNamespace = "http://tempuri.org/", wsdlLocation = "http://10.10.20.20:8045/asmx/ReceiveInvestmentItems.asmx?WSDL")
@WebServiceClient(name = "ReceiveInvestmentItems", targetNamespace = "http://tempuri.org/", wsdlLocation = "http://10.10.2.49/ContractInterface/asmx/ReceiveInvestmentItems.asmx?wsdl")
class ReceiveInvestmentItems
    extends Service
{

    private final static URL RECEIVEINVESTMENTITEMS_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(ReceiveInvestmentItems.class.getName());

    static {
        URL url = null;
        String clientUrl = PropertiesUtil.getProperty("ws_ht_send_addr");
        try {
            URL baseUrl;
            baseUrl = ReceiveInvestmentItems.class.getResource(".");
            url = new URL(baseUrl, clientUrl);
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: '"+clientUrl+"', retrying as a local file");
            logger.warning(e.getMessage());
        }
        RECEIVEINVESTMENTITEMS_WSDL_LOCATION = url;
    }

    public ReceiveInvestmentItems(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ReceiveInvestmentItems() {
        super(RECEIVEINVESTMENTITEMS_WSDL_LOCATION, new QName("http://tempuri.org/", "ReceiveInvestmentItems"));
    }

    /**
     * 
     * @return
     *     returns ReceiveInvestmentItemsSoap
     */
    @WebEndpoint(name = "ReceiveInvestmentItemsSoap")
    public ReceiveInvestmentItemsSoap getReceiveInvestmentItemsSoap() {
        return super.getPort(new QName("http://tempuri.org/", "ReceiveInvestmentItemsSoap"), ReceiveInvestmentItemsSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ReceiveInvestmentItemsSoap
     */
    @WebEndpoint(name = "ReceiveInvestmentItemsSoap")
    public ReceiveInvestmentItemsSoap getReceiveInvestmentItemsSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "ReceiveInvestmentItemsSoap"), ReceiveInvestmentItemsSoap.class, features);
    }

}
