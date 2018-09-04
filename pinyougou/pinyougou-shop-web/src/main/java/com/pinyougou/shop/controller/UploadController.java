package com.pinyougou.shop.controller;

import com.pinyougou.common.util.FastDFSClient;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Date:2018-09-03
 * Author:Wanzi
 * Desc:
 */
@RequestMapping
@RestController
public class UploadController {

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile multipartFile){
        Result result = new Result();

        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastdfs/tracker.conf");
            //上传图片

            //后缀名
            String file_ext_name = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")+ 1);
            String url = fastDFSClient.uploadFile(multipartFile.getBytes(), file_ext_name);

            //上传成功
            result = Result.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
