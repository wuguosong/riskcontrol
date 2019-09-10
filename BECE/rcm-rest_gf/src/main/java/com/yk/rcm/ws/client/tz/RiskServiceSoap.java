package com.yk.rcm.ws.client.tz;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.0.12
 * 2017-04-26T19:10:19.336+08:00
 * Generated source version: 3.0.12
 * 
 */
@WebService(targetNamespace = "http://tempuri.org/", name = "RiskServiceSoap")
@XmlSeeAlso({ObjectFactory.class})
public interface RiskServiceSoap {

    @WebResult(name = "HelloWorldResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "HelloWorld", targetNamespace = "http://tempuri.org/", className = "com.yk.rcm.ws.client.tz.HelloWorld")
    @WebMethod(operationName = "HelloWorld", action = "http://tempuri.org/HelloWorld")
    @ResponseWrapper(localName = "HelloWorldResponse", targetNamespace = "http://tempuri.org/", className = "com.yk.rcm.ws.client.tz.HelloWorldResponse")
    public java.lang.String helloWorld();

    @WebResult(name = "SynchronousResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "Synchronous", targetNamespace = "http://tempuri.org/", className = "com.yk.rcm.ws.client.tz.Synchronous")
    @WebMethod(operationName = "Synchronous", action = "http://tempuri.org/Synchronous")
    @ResponseWrapper(localName = "SynchronousResponse", targetNamespace = "http://tempuri.org/", className = "com.yk.rcm.ws.client.tz.SynchronousResponse")
    public java.lang.String synchronous();

    @WebResult(name = "UpdateRiskAuditInfoResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "UpdateRiskAuditInfo", targetNamespace = "http://tempuri.org/", className = "com.yk.rcm.ws.client.tz.UpdateRiskAuditInfo")
    @WebMethod(operationName = "UpdateRiskAuditInfo", action = "http://tempuri.org/UpdateRiskAuditInfo")
    @ResponseWrapper(localName = "UpdateRiskAuditInfoResponse", targetNamespace = "http://tempuri.org/", className = "com.yk.rcm.ws.client.tz.UpdateRiskAuditInfoResponse")
    public java.lang.String updateRiskAuditInfo(
        @WebParam(name = "CustomerId", targetNamespace = "http://tempuri.org/")
        java.lang.String customerId,
        @WebParam(name = "RiskStatus", targetNamespace = "http://tempuri.org/")
        java.lang.String riskStatus,
        @WebParam(name = "AuditReport", targetNamespace = "http://tempuri.org/")
        java.lang.String auditReport
    );
    
    /**
     * 
     * @param riskJsonStr
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetPreDecisionInfo", action = "http://tempuri.org/GetPreDecisionInfo")
    @WebResult(name = "GetPreDecisionInfoResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetPreDecisionInfo", targetNamespace = "http://tempuri.org/", className = "ws.client.tz.GetPreDecisionInfo")
    @ResponseWrapper(localName = "GetPreDecisionInfoResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.tz.GetPreDecisionInfoResponse")
    public String getPreDecisionInfo(
        @WebParam(name = "RiskJsonStr", targetNamespace = "http://tempuri.org/")
        String riskJsonStr);

    /**
     * 
     * @param riskJsonStr
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetPfrDecisionInfo", action = "http://tempuri.org/GetPfrDecisionInfo")
    @WebResult(name = "GetPfrDecisionInfoResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetPfrDecisionInfo", targetNamespace = "http://tempuri.org/", className = "ws.client.tz.GetPfrDecisionInfo")
    @ResponseWrapper(localName = "GetPfrDecisionInfoResponse", targetNamespace = "http://tempuri.org/", className = "ws.client.tz.GetPfrDecisionInfoResponse")
    public String getPfrDecisionInfo(
        @WebParam(name = "RiskJsonStr", targetNamespace = "http://tempuri.org/")
        String riskJsonStr);
}