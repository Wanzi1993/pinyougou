package com.pinyougou.manage.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Date:2018-09-02
 * Author:Wanzi
 * Desc:
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @GetMapping("getUsername")
    public Map<String,String> getUsername() {
        Map<String,String> map = new HashMap<>();
        //根据security可以获取当前登陆的用户信息
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);

        return map;
    }
}
