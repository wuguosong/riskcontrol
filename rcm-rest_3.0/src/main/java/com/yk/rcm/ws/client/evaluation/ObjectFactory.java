
package com.yk.rcm.ws.client.evaluation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.ws.service package. 
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

    private final static QName _AftFileResponse_QNAME = new QName("http://service.ws.com/", "Aft_fileResponse");
    private final static QName _AftHand_QNAME = new QName("http://service.ws.com/", "Aft_Hand");
    private final static QName _AftFile_QNAME = new QName("http://service.ws.com/", "Aft_file");
    private final static QName _AftHandResponse_QNAME = new QName("http://service.ws.com/", "Aft_HandResponse");
    private final static QName _AftReview_QNAME = new QName("http://service.ws.com/", "Aft_Review");
    private final static QName _AftReviewResponse_QNAME = new QName("http://service.ws.com/", "Aft_ReviewResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ws.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AftHand }
     * 
     */
    public AftHand createAftHand() {
        return new AftHand();
    }

    /**
     * Create an instance of {@link AftFile }
     * 
     */
    public AftFile createAftFile() {
        return new AftFile();
    }

    /**
     * Create an instance of {@link AftFileResponse }
     * 
     */
    public AftFileResponse createAftFileResponse() {
        return new AftFileResponse();
    }

    /**
     * Create an instance of {@link AftReviewResponse }
     * 
     */
    public AftReviewResponse createAftReviewResponse() {
        return new AftReviewResponse();
    }

    /**
     * Create an instance of {@link AftHandResponse }
     * 
     */
    public AftHandResponse createAftHandResponse() {
        return new AftHandResponse();
    }

    /**
     * Create an instance of {@link AftReview }
     * 
     */
    public AftReview createAftReview() {
        return new AftReview();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AftFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.com/", name = "Aft_fileResponse")
    public JAXBElement<AftFileResponse> createAftFileResponse(AftFileResponse value) {
        return new JAXBElement<AftFileResponse>(_AftFileResponse_QNAME, AftFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AftHand }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.com/", name = "Aft_Hand")
    public JAXBElement<AftHand> createAftHand(AftHand value) {
        return new JAXBElement<AftHand>(_AftHand_QNAME, AftHand.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AftFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.com/", name = "Aft_file")
    public JAXBElement<AftFile> createAftFile(AftFile value) {
        return new JAXBElement<AftFile>(_AftFile_QNAME, AftFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AftHandResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.com/", name = "Aft_HandResponse")
    public JAXBElement<AftHandResponse> createAftHandResponse(AftHandResponse value) {
        return new JAXBElement<AftHandResponse>(_AftHandResponse_QNAME, AftHandResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AftReview }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.com/", name = "Aft_Review")
    public JAXBElement<AftReview> createAftReview(AftReview value) {
        return new JAXBElement<AftReview>(_AftReview_QNAME, AftReview.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AftReviewResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.com/", name = "Aft_ReviewResponse")
    public JAXBElement<AftReviewResponse> createAftReviewResponse(AftReviewResponse value) {
        return new JAXBElement<AftReviewResponse>(_AftReviewResponse_QNAME, AftReviewResponse.class, null, value);
    }

}
