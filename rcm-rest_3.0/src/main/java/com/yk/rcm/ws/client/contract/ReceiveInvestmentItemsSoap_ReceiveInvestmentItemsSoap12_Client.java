
package com.yk.rcm.ws.client.contract;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.0.12
 * 2017-04-26T19:31:28.094+08:00
 * Generated source version: 3.0.12
 * 
 */
public final class ReceiveInvestmentItemsSoap_ReceiveInvestmentItemsSoap12_Client {

    private static final QName SERVICE_NAME = new QName("http://tempuri.org/", "ReceiveInvestmentItems");

    private ReceiveInvestmentItemsSoap_ReceiveInvestmentItemsSoap12_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = ReceiveInvestmentItems.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        ReceiveInvestmentItems ss = new ReceiveInvestmentItems(wsdlURL, SERVICE_NAME);
        ReceiveInvestmentItemsSoap port = ss.getReceiveInvestmentItemsSoap12();  
        
        {
        System.out.println("Invoking getInvestmentItemsMessage...");
        java.lang.String _getInvestmentItemsMessage_requestJsonStr = "";
        java.lang.String _getInvestmentItemsMessage__return = port.getInvestmentItemsMessage(_getInvestmentItemsMessage_requestJsonStr);
        System.out.println("getInvestmentItemsMessage.result=" + _getInvestmentItemsMessage__return);


        }

        System.exit(0);
    }

}
