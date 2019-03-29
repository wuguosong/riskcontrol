
package ws.client.evaluation;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ws.client.evaluation package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ws.client.evaluation
     * 
     */
    public ObjectFactory() {
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
     * Create an instance of {@link AftFileResponse }
     * 
     */
    public AftFileResponse createAftFileResponse() {
        return new AftFileResponse();
    }

    /**
     * Create an instance of {@link AftFile }
     * 
     */
    public AftFile createAftFile() {
        return new AftFile();
    }

    /**
     * Create an instance of {@link AftReview }
     * 
     */
    public AftReview createAftReview() {
        return new AftReview();
    }

    /**
     * Create an instance of {@link AftHand }
     * 
     */
    public AftHand createAftHand() {
        return new AftHand();
    }

}
