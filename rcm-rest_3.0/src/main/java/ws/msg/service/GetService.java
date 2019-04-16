package ws.msg.service;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by Administrator on 2019/4/16 0016.
 */
@WebService
public interface GetService {
    public String getMsg(@WebParam(name="msgId") String msgId);
}
