package cn.itcast.cas.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:2018-09-17
 * Author:Wanzi
 * Desc:
 */
@RequestMapping("/user")
@RestController
public class UserController {
    @GetMapping("/getUsername")
    public String getUsername(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return username;
    }
}
