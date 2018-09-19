package cn.itcast.springboot.controller;

/**
 * Date:2018-09-15
 * Author:Wanzi
 * Desc:
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理器
 */
@RestController
@RequestMapping("/mq")
public class MQController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @GetMapping("/send")
    public String sendMessage(){
        Map<String,Object> map = new HashMap<>();
        map.put("id",123);
        map.put("name","itcast");

        //默认使用的是队列模式
        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue",map);

        return "发送了map消息到队列:spring.boot.map.queue";
    }
    @GetMapping("/sendSms")
    public String sendSmsMessage(){
        Map<String,String> map = new HashMap<>();
        map.put("mobile","13750052741");
        map.put("signName","绿之源");
        map.put("templateCode","SMS_144851992");
        map.put("templateParam","{\"code\":\"7654321\"}");

        //默认使用的是队列模式
        jmsMessagingTemplate.convertAndSend("itcast_sms_queue",map);

        return "发送了map消息到队列:itcast_sms_queue";
    }
}
