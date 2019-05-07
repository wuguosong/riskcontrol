package message;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import ws.agency.client.TodoClient;
import ws.msg.client.MessageBack;
import ws.msg.client.MessageClient;
import ws.msg.client.MessageStatus;

import java.util.List;

/**
 * Created by Administrator on 2019/4/18 0018.
 */
public class ClientTest {
    MessageClient client = new MessageClient();
    MessageBack back;
    MessageStatus status;

    /**
     * 邮箱发送
     * 1.提交成功
     * 2.未处理
     * 3.失败
     */
    @Test
    public void testEmail() {
        back = client.sendEmail("wjsxhclj@sina.com", "490785256@qq.com", "", client._TITLE, client._CONTENT + client._URL + "/10481");
        System.out.println(JSON.toJSONString(back));
        status = client.getEmailStatus(back.getData());
        System.out.println(JSON.toJSONString(status));
    }

    /**
     * 邮箱查询
     */
    @Test
    public void testEmailStatus() {
        status = client.getEmailStatus("d894f17a-fc79-4355-8f9b-1c3ec871eb94");
        System.out.println(JSON.toJSONString(status));
    }

    /**
     * 钉钉链接发送
     * 1.提交成功
     * 2.未处理
     * 3.未处理
     */
    @Test
    public void testDtLink() {
        back = client.sendDtLink("", "wuguosong", client._URL + 10481, client._TITLE, client._URL + 10481, client._CONTENT);
        System.out.println(JSON.toJSONString(back));
        status = client.getDtStatus(back.getData());
        System.out.println(JSON.toJSONString(status));
    }

    /**
     * 钉钉查询
     */
    @Test
    public void testDtLinkStatus() {
        status = client.getDtStatus("2729a5e1-029a-4597-a827-51bae8b234c4");
        System.out.println(JSON.toJSONString(status));
    }

    /**
     * 钉钉文本发送
     * 1.提交成功
     * 2.未处理
     * 3.未处理
     */
    @Test
    public void testDtText() {
        back = client.sendDtText("", "wuguosong", client._CONTENT + client._URL + "10496");
        System.out.println(JSON.toJSONString(back));
        status = client.getDtStatus(back.getData());
        System.out.println(JSON.toJSONString(status));
    }

    /**
     * 钉钉查询
     */
    @Test
    public void testDtTextStatus() {
        status = client.getDtStatus("c2fc1843-2408-4655-9f51-9869c1ae4c15");
        System.out.println(JSON.toJSONString(status));
    }

    /**
     * 微信发送
     * 1.提交成功
     * 2.未处理
     * 3.待重试
     */
    @Test
    public void testWx() {
        back = client.sendWxText("", "wuguosong", "", (short) 0, client._CONTENT + client._URL + "10496");
        System.out.println(JSON.toJSONString(back));
        status = client.getDtStatus(back.getData());
        System.out.println(JSON.toJSONString(status));
    }

    /**
     * 微信查询
     */
    @Test
    public void testWxStatus() {
        status = client.getWxStatus("6fac1887-73c2-4d9a-87b9-8849722a629a");
        System.out.println(JSON.toJSONString(status));
    }

    /**
     * 短信发送
     * 1.提交成功
     * 2.未处理
     * 3.失败
     */
    @Test
    public void testSms() {
        back = client.sendSms("18220216186", client._CONTENT + client._URL + "/10496", "");
        System.out.println(JSON.toJSONString(back));
        status = client.getSmsStatus(back.getData());
        System.out.println(JSON.toJSONString(status));
    }

    /**
     * 短信查询
     */
    @Test
    public void testSmsStatus() {
        status = client.getSmsStatus("43098be5-6f67-49d1-a572-d0217af0473d");
        System.out.println(JSON.toJSONString(status));
    }

    @Test
    public void testSub(){
        String str = "123,456,789,";
        str = str.substring(0, str.lastIndexOf(","));
        System.out.println(str);
        str = str.replace(",", "|");
        System.out.println(str);
        System.out.println("hi".replace(",", "|"));
    }

    @Test
    public void test(){
    }
}
