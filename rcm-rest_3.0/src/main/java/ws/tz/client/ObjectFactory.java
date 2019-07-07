
package ws.tz.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ws.tz.client package. 
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

    private final static QName _CreatePfr_QNAME = new QName("http://service.tz.ws/", "createPfr");
    private final static QName _CreatePfrResponse_QNAME = new QName("http://service.tz.ws/", "createPfrResponse");
    private final static QName _CreatePre_QNAME = new QName("http://service.tz.ws/", "createPre");
    private final static QName _CreatePreFeedBack_QNAME = new QName("http://service.tz.ws/", "createPreFeedBack");
    private final static QName _CreatePreFeedBackResponse_QNAME = new QName("http://service.tz.ws/", "createPreFeedBackResponse");
    private final static QName _CreatePreResponse_QNAME = new QName("http://service.tz.ws/", "createPreResponse");
    private final static QName _DeletePfr_QNAME = new QName("http://service.tz.ws/", "deletePfr");
    private final static QName _DeletePfrResponse_QNAME = new QName("http://service.tz.ws/", "deletePfrResponse");
    private final static QName _DeletePre_QNAME = new QName("http://service.tz.ws/", "deletePre");
    private final static QName _DeletePreResponse_QNAME = new QName("http://service.tz.ws/", "deletePreResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ws.tz.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreatePfr }
     * 
     */
    public CreatePfr createCreatePfr() {
        return new CreatePfr();
    }

    /**
     * Create an instance of {@link CreatePfrResponse }
     * 
     */
    public CreatePfrResponse createCreatePfrResponse() {
        return new CreatePfrResponse();
    }

    /**
     * Create an instance of {@link CreatePre }
     * 
     */
    public CreatePre createCreatePre() {
        return new CreatePre();
    }

    /**
     * Create an instance of {@link CreatePreFeedBack }
     * 
     */
    public CreatePreFeedBack createCreatePreFeedBack() {
        return new CreatePreFeedBack();
    }

    /**
     * Create an instance of {@link CreatePreFeedBackResponse }
     * 
     */
    public CreatePreFeedBackResponse createCreatePreFeedBackResponse() {
        return new CreatePreFeedBackResponse();
    }

    /**
     * Create an instance of {@link CreatePreResponse }
     * 
     */
    public CreatePreResponse createCreatePreResponse() {
        return new CreatePreResponse();
    }

    /**
     * Create an instance of {@link DeletePfr }
     * 
     */
    public DeletePfr createDeletePfr() {
        return new DeletePfr();
    }

    /**
     * Create an instance of {@link DeletePfrResponse }
     * 
     */
    public DeletePfrResponse createDeletePfrResponse() {
        return new DeletePfrResponse();
    }

    /**
     * Create an instance of {@link DeletePre }
     * 
     */
    public DeletePre createDeletePre() {
        return new DeletePre();
    }

    /**
     * Create an instance of {@link DeletePreResponse }
     * 
     */
    public DeletePreResponse createDeletePreResponse() {
        return new DeletePreResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatePfr }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreatePfr }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "createPfr")
    public JAXBElement<CreatePfr> createCreatePfr(CreatePfr value) {
        return new JAXBElement<CreatePfr>(_CreatePfr_QNAME, CreatePfr.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatePfrResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreatePfrResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "createPfrResponse")
    public JAXBElement<CreatePfrResponse> createCreatePfrResponse(CreatePfrResponse value) {
        return new JAXBElement<CreatePfrResponse>(_CreatePfrResponse_QNAME, CreatePfrResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatePre }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreatePre }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "createPre")
    public JAXBElement<CreatePre> createCreatePre(CreatePre value) {
        return new JAXBElement<CreatePre>(_CreatePre_QNAME, CreatePre.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatePreFeedBack }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreatePreFeedBack }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "createPreFeedBack")
    public JAXBElement<CreatePreFeedBack> createCreatePreFeedBack(CreatePreFeedBack value) {
        return new JAXBElement<CreatePreFeedBack>(_CreatePreFeedBack_QNAME, CreatePreFeedBack.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatePreFeedBackResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreatePreFeedBackResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "createPreFeedBackResponse")
    public JAXBElement<CreatePreFeedBackResponse> createCreatePreFeedBackResponse(CreatePreFeedBackResponse value) {
        return new JAXBElement<CreatePreFeedBackResponse>(_CreatePreFeedBackResponse_QNAME, CreatePreFeedBackResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatePreResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreatePreResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "createPreResponse")
    public JAXBElement<CreatePreResponse> createCreatePreResponse(CreatePreResponse value) {
        return new JAXBElement<CreatePreResponse>(_CreatePreResponse_QNAME, CreatePreResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletePfr }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DeletePfr }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "deletePfr")
    public JAXBElement<DeletePfr> createDeletePfr(DeletePfr value) {
        return new JAXBElement<DeletePfr>(_DeletePfr_QNAME, DeletePfr.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletePfrResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DeletePfrResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "deletePfrResponse")
    public JAXBElement<DeletePfrResponse> createDeletePfrResponse(DeletePfrResponse value) {
        return new JAXBElement<DeletePfrResponse>(_DeletePfrResponse_QNAME, DeletePfrResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletePre }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DeletePre }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "deletePre")
    public JAXBElement<DeletePre> createDeletePre(DeletePre value) {
        return new JAXBElement<DeletePre>(_DeletePre_QNAME, DeletePre.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletePreResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DeletePreResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.tz.ws/", name = "deletePreResponse")
    public JAXBElement<DeletePreResponse> createDeletePreResponse(DeletePreResponse value) {
        return new JAXBElement<DeletePreResponse>(_DeletePreResponse_QNAME, DeletePreResponse.class, null, value);
    }

}
