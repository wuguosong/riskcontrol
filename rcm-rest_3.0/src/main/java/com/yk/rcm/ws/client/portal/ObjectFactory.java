
package com.yk.rcm.ws.client.portal;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.yk.rcm.ws.client.portal package. 
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

    private final static QName _TodoList_QNAME = new QName("http://service.ws.com/", "todoList");
    private final static QName _TodoListResponse_QNAME = new QName("http://service.ws.com/", "todoListResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.yk.rcm.ws.client.portal
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TodoList }
     * 
     */
    public TodoList createTodoList() {
        return new TodoList();
    }

    /**
     * Create an instance of {@link TodoListResponse }
     * 
     */
    public TodoListResponse createTodoListResponse() {
        return new TodoListResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TodoList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.com/", name = "todoList")
    public JAXBElement<TodoList> createTodoList(TodoList value) {
        return new JAXBElement<TodoList>(_TodoList_QNAME, TodoList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TodoListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.com/", name = "todoListResponse")
    public JAXBElement<TodoListResponse> createTodoListResponse(TodoListResponse value) {
        return new JAXBElement<TodoListResponse>(_TodoListResponse_QNAME, TodoListResponse.class, null, value);
    }

}
