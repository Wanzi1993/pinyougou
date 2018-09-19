package cn.itcast.springboot;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Date:2018-09-15
 * Author:Wanzi
 * Desc:
 */

/**
 * springboot必须要有引导类,必须要有SpringBootApplicatio注解,使用main方法
 * 该注解是一个组合注解,默认扫描本类及其子包的那些注解
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
        //去logo启动
    //    SpringApplication springApplication = new SpringApplication(Application.class);

    //    springApplication.setBannerMode(Banner.Mode.OFF);

      //  springApplication.run(args);
    }
}
