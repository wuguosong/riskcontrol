
package com.yk.rcm.ws.client.tz;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.yk.rcm.ws.client.tz package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.yk.rcm.ws.client.tz
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HelloWorld }
     * 
     */
    public HelloWorld createHelloWorld() {
        return new HelloWorld();
    }

    /**
     * Create an instance of {@link HelloWorldResponse }
     * 
     */
    public HelloWorldResponse createHelloWorldResponse() {
        return new HelloWorldResponse();
    }

    /**
     * Create an instance of {@link UpdateRiskAuditInfo }
     * 
     */
    public UpdateRiskAuditInfo createUpdateRiskAuditInfo() {
        return new UpdateRiskAuditInfo();
    }

    /**
     * Create an instance of {@link UpdateRiskAuditInfoResponse }
     * 
     */
    public UpdateRiskAuditInfoResponse createUpdateRiskAuditInfoResponse() {
        return new UpdateRiskAuditInfoResponse();
    }

    /**
     * Create an instance of {@link Synchronous }
     * 
     */
    public Synchronous createSynchronous() {
        return new Synchronous();
    }

    /**
     * Create an instance of {@link SynchronousResponse }
     * 
     */
    public SynchronousResponse createSynchronousResponse() {
        return new SynchronousResponse();
    }

}
