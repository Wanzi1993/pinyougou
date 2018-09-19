package cn.itcast.springboot.activemq.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Date:2018-09-15
 * Author:Wanzi
 * Desc:
 */

@Component
public class MessageListener {

    //监听注解
    @JmsListener(destination = "spring.boot.map.queue")
    public void receiveMsg(Map<String,Object> map){
        System.out.println("接收到的消息:" + map);
    }
}
