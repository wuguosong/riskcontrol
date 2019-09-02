package ws.msg.service;

import javax.jws.WebService;

/**
 * Created by Administrator on 2019/4/16 0016.
 */
@WebService(endpointInterface = "ws.msg.service.GetService", serviceName = "getService")
public class GetServiceImpl implements GetService {

    @Override
    public String getMsg(String msgId) {
        System.out.println("参数：" + msgId);
        return msgId;
    }
}
