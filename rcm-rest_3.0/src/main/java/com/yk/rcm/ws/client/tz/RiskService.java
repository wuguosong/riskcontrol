package com.yk.rcm.ws.client.tz;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

import util.PropertiesUtil;

/**
 * This class was generated by Apache CXF 3.0.12
 * 2017-04-26T19:10:19.357+08:00
 * Generated source version: 3.0.12
 * 
 */
@WebServiceClient(name = "RiskService", 
                  wsdlLocation = "http://ipm.bewg.net.cn/RiskWebService/RiskService.asmx?wsdl",
                  targetNamespace = "http://tempuri.org/") 
public class RiskService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://tempuri.org/", "RiskService");
    public final static QName RiskServiceSoap12 = new QName("http://tempuri.org/", "RiskServiceSoap12");
    public final static QName RiskServiceSoap = new QName("http://tempuri.org/", "RiskServiceSoap");
    static {
        URL url = null;
        String clientUrl = PropertiesUtil.getProperty("ws_tz_risk_addr");
        try {
            url = new URL(clientUrl);
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(RiskService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", clientUrl);
        }
        WSDL_LOCATION = url;
    }

    public RiskService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RiskService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RiskService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns RiskServiceSoap
     */
    @WebEndpoint(name = "RiskServiceSoap12")
    public RiskServiceSoap getRiskServiceSoap12() {
        return super.getPort(RiskServiceSoap12, RiskServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RiskServiceSoap
     */
    @WebEndpoint(name = "RiskServiceSoap12")
    public RiskServiceSoap getRiskServiceSoap12(WebServiceFeature... features) {
        return super.getPort(RiskServiceSoap12, RiskServiceSoap.class, features);
    }
    /**
     *
     * @return
     *     returns RiskServiceSoap
     */
    @WebEndpoint(name = "RiskServiceSoap")
    public RiskServiceSoap getRiskServiceSoap() {
        return super.getPort(RiskServiceSoap, RiskServiceSoap.class);
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
        return super.getPort(RiskServiceSoap, RiskServiceSoap.class, features);
    }

}
