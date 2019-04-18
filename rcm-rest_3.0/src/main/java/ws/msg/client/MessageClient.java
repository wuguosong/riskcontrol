package ws.msg.client;

import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;
import org.apache.commons.lang3.StringUtils;

/**
 * 统一消息平台接口客户端
 */
public class MessageClient {
    private Prop prop = PropKit.use("dev_db.properties");
    public String _URL = prop.get("message.share.url") + prop.get("message.request.mapping");
    public String _CONTENT = prop.get("message.share.content");
    public String _TITLE = prop.get("message.share.title");
    private String SYS_CODE = prop.get("message.ws.sys.code");
    private BEWGMessageServiceSoap soap = new BEWGMessageService().getBEWGMessageServiceSoap();

    public MessageClient() {
    }

    /**
     * 发送短信
     *
     * @param target     接收方手机号码。如：13123456789
     * @param content    短信内容
     * @param targetTime 计划发送短信的时间，如果为空则为立即发送。如：2017/7/18 15:22:46
     * @return MessageBack
     */
    public MessageBack sendSms(String target, String content, String targetTime) {
        if (StringUtils.isAnyBlank(target, content)) {
            throw new RuntimeException("参数[target/content]不能为空！");
        }
        MessageSMS sms = new MessageSMS();
        sms.setSysCode(SYS_CODE);
        sms.setTarget(target);
        sms.setContent(content);
        return soap.sendSMS(sms);
    }

    /**
     * 发送钉钉消息
     *
     * @param toParty 部门id列表，多个接收者用|分隔。toUser或者toParty 二者有一个必填。如：PartyID1|PartyID2|PartyID3
     * @param toUser  员工id列表（消息接收者，多个接收者用|分隔）。如：UserID1|UserID2|UserID3
     * @param content 消息内容
     * @return MessageBack
     */
    public MessageBack sendDtText(String toParty, String toUser, String content) {
        if (StringUtils.isAnyBlank(content)) {
            throw new RuntimeException("参数[content]不能为空！");
        }
        if (StringUtils.isBlank(toParty) && StringUtils.isBlank(toUser)) {
            throw new RuntimeException("参数[toParty/toUser]其中一个不能为空！");
        }
        MessageDTText dtText = new MessageDTText();
        dtText.setSysCode(SYS_CODE);
        dtText.setToparty(toParty);
        dtText.setTouser(toUser);
        MessageDTTextContent dtContent = new MessageDTTextContent();
        dtContent.setContent(content);
        dtText.setText(dtContent);
        return soap.sendDTText(dtText);
    }

    /**
     * 发送钉钉链接
     *
     * @param toParty 部门id列表，多个接收者用|分隔。toUser或者toParty 二者有一个必填。如：PartyID1|PartyID2|PartyID3
     * @param toUser  员工id列表（消息接收者，多个接收者用|分隔）。如：UserID1|UserID2|UserID3
     * @param link    链接内容
     * @return MessageBack
     */
    public MessageBack sendDtLink(String toParty, String toUser, String link) {
        if (StringUtils.isAnyBlank(link)) {
            throw new RuntimeException("参数[link]不能为空！");
        }
        if (StringUtils.isBlank(toParty) && StringUtils.isBlank(toUser)) {
            throw new RuntimeException("参数[toParty/toUser]其中一个不能为空！");
        }
        MessageDTLink dtLink = new MessageDTLink();
        dtLink.setSysCode(SYS_CODE);
        dtLink.setToparty(toParty);
        dtLink.setTouser(toUser);
        MessageDTLinkContent dtContent = new MessageDTLinkContent();
        dtLink.setLink(dtContent);
        return soap.sendDTLink(dtLink);
    }

    /**
     * 发送微信消息
     *
     * @param toParty 部门id列表，多个接收者用|分隔。toUser或者toParty 二者有一个必填。如：PartyID1|PartyID2|PartyID3
     * @param toUser  员工id列表（消息接收者，多个接收者用|分隔）。如：UserID1|UserID2|UserID3
     * @param toTag   标签ID列表，多个接收者用‘|’分隔，最多支持100个。当toUser为@all时忽略本参数。TagID|TagID1|TagID2
     * @param safe    表示是否是保密消息，0表示否，1表示是，默认0。
     * @param content 微信内容
     * @return MessageBack
     */
    public MessageBack sendWxText(String toParty, String toUser, String toTag, short safe, String content) {
        if (StringUtils.isAnyBlank(content)) {
            throw new RuntimeException("参数[content]不能为空！");
        }
        if (StringUtils.isBlank(toParty) && StringUtils.isBlank(toUser)) {
            throw new RuntimeException("参数[toParty/toUser]其中一个不能为空！");
        }
        MessageWxText wxText = new MessageWxText();
        wxText.setSysCode(SYS_CODE);
        wxText.setToparty(toParty);
        wxText.setTouser(toUser);
        wxText.setTotag(toTag);
        wxText.setSafe(safe);
        MessageWxTextContent wxContent = new MessageWxTextContent();
        wxContent.setContent(content);
        wxText.setText(wxContent);
        return soap.sendWxText(wxText);
    }

    /**
     * 发送邮箱
     *
     * @param sender  发送方Email。如：auto@126.com
     * @param target  接收方Email。CXF@163.com
     * @param cc      抄送联系人Email。 如：goog@163.com;good@126.com
     * @param title   邮件标题
     * @param content 消息内容
     * @return MessageBack
     */
    public MessageBack sendEmail(String sender, String target, String cc, String title, String content) {
        if (StringUtils.isAnyBlank(sender, target, title, content)) {
            throw new RuntimeException("参数[sender/target/title/content]不能为空！");
        }
        MessageEmail email = new MessageEmail();
        email.setSysCode(SYS_CODE);
        email.setSender(sender);
        email.setTarget(target);
        email.setCC(cc);
        email.setTitle(title);
        email.setContent(content);
        return soap.sendEmail(email);
    }

    /**
     * 查询邮箱发送状态
     *
     * @param id 消息唯一识别编号，在提交发送请求成功后接口返回的消息唯一识别编号。如：4b0171db-8085-4aa1-b06d-7fd82b26a538
     * @return MessageStatus
     */
    public MessageStatus getEmailStatus(String id) {
        return soap.getEmailStatus(id, SYS_CODE);
    }

    /**
     * 查询短信发送状态
     *
     * @param id 消息唯一识别编号，在提交发送请求成功后接口返回的消息唯一识别编号。如：4b0171db-8085-4aa1-b06d-7fd82b26a538
     * @return MessageStatus
     */
    public MessageStatus getSmsStatus(String id) {
        return soap.getSmsStatus(id, SYS_CODE);
    }

    /**
     * 查询钉钉发送状态
     *
     * @param id 消息唯一识别编号，在提交发送请求成功后接口返回的消息唯一识别编号。如：4b0171db-8085-4aa1-b06d-7fd82b26a538
     * @return MessageStatus
     */
    public MessageStatus getDtStatus(String id) {
        return soap.getDtStatus(id, SYS_CODE);
    }


    /**
     * 查询微信发送状态
     *
     * @param id 消息唯一识别编号，在提交发送请求成功后接口返回的消息唯一识别编号。如：4b0171db-8085-4aa1-b06d-7fd82b26a538
     * @return MessageStatus
     */
    public MessageStatus getWxStatus(String id) {
        return soap.getWxStatus(id, SYS_CODE);
    }
}
