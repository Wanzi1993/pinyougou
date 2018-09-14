package com.pinyougou.item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * 在运营商管理后台中如果删除商品之后应该发送商品id集合到MQ,
 * 然后商品详情系统接收商品id并根据商品id删除指定路径下的静态页面
 */
public class ItemDeleteTopicMessageListener extends AbstractAdaptableMessageListener {

    //读取配置文件中的配置项
    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //1、接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;

        Long[] ids = (Long[]) objectMessage.getObject();

        //2、处理消息（根据商品spu id删除指定路径下具体的静态页面）
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                File file = new File(ITEM_HTML_PATH + id + ".html");
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
}
