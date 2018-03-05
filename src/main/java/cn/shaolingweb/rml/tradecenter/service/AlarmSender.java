package cn.shaolingweb.rml.tradecenter.service;

import cn.shaolingweb.rml.tradecenter.domain.Hq;

public interface AlarmSender {

    public boolean weixin();//发送微信
    public boolean openFile();//打开文件
    public boolean callPhone();//打电话
    public boolean sendMsg();//发短信
    public boolean sendEmail();//发邮件

    public boolean alerm(Hq hq, String msg);
}
