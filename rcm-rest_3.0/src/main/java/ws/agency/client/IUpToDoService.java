package ws.agency.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.3.1
 * 2019-04-25T16:03:35.061+08:00
 * Generated source version: 3.3.1
 *
 */
@WebService(targetNamespace = "http://service.ws.com/", name = "IUpToDoService")
@XmlSeeAlso({ObjectFactory.class})
public interface IUpToDoService {

    @WebMethod
    @RequestWrapper(localName = "todoList", targetNamespace = "http://service.ws.com/", className = "ws.todo.client.TodoList")
    @ResponseWrapper(localName = "todoListResponse", targetNamespace = "http://service.ws.com/", className = "ws.todo.client.TodoListResponse")
    @WebResult(name = "return", targetNamespace = "")
    public String todoList(
            @WebParam(name = "arg0", targetNamespace = "")
                    String arg0
    );
}
