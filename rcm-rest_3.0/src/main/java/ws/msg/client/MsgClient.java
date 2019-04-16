package ws.msg.client;

import com.alibaba.fastjson.JSON;
import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;
import ws.client.message.*;

/**
 * Created by Administrator on 2019/4/16 0016.
 */
public class MsgClient {
    private static Prop prop = PropKit.use("dev_db.properties");
    private static String id = "10086";
    private static String to = "3283751";
    private static String url = prop.get("message.share.url") + prop.get("message.request.mapping") + id;
    private static String content = prop.get("message.share.content");
    private static String title = prop.get("message.share.title");
    private static String sys = prop.get("message.ws.sys.code");

    public static void main(String[] args) {
        sendDTLink();
    }

    // 发送钉钉消息
    public static void sendDTText(){
        // 初始化SOAP客户端
        BEWGMessageService client = new BEWGMessageService();
        BEWGMessageServiceSoap bewgMessageServiceSoap = client.getBEWGMessageServiceSoap();
        MessageDTText messageDTText = new MessageDTText();
        messageDTText.setSysCode(sys);
        MessageDTTextContent messageDTTextContent = new MessageDTTextContent();
        messageDTTextContent.setContent(content);
        messageDTText.setText(messageDTTextContent);
        messageDTText.setTouser(to);
        MessageBack messageBack = bewgMessageServiceSoap.sendDTText(messageDTText);
        System.out.println(JSON.toJSON(messageBack));
    }

    // 发送钉钉链接
    public static void sendDTLink(){
        BEWGMessageService client = new BEWGMessageService();
        BEWGMessageServiceSoap bewgMessageServiceSoap = client.getBEWGMessageServiceSoap();
        MessageDTLink messageDTLink = new MessageDTLink();
        messageDTLink.setSysCode(sys);
        MessageDTLinkContent messageDTLinkContent = new MessageDTLinkContent();
        messageDTLinkContent.setMessageUrl(url);
        messageDTLinkContent.setPicUrl(url);
        messageDTLinkContent.setText(content);
        messageDTLinkContent.setTitle(title);
        messageDTLink.setLink(messageDTLinkContent);
        messageDTLink.setTouser(to);
        MessageBack messageBack = bewgMessageServiceSoap.sendDTLink(messageDTLink);
        System.out.println(JSON.toJSON(messageBack));
    }
}
