package ws.msg.service;

import javax.xml.ws.Endpoint;

/**
 * Created by Administrator on 2019/4/16 0016.
 */
public class MainRun {
    public static void main(String[] args) {
        String address = "http://localhost:8080/rcm-rest/webservice/getService";
        Endpoint.publish(address, new GetServiceImpl());
        System.out.println("wsdl已经发布：" + address + "?wsdl");
    }
}
