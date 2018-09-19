package cn.itcast.springboot.controller;

/**
 * Date:2018-09-15
 * Author:Wanzi
 * Desc:
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理器
 */
@RestController
@RequestMapping
public class HelloWorldController {

    @Autowired
    private Environment environment;

    @GetMapping("/info")
    public String hello(){

        System.out.println("url:" + environment.getProperty("url"));
        return "hello springboot";
    }
}
