package com.lagou.controller;

import com.lagou.bean.OssResult;
import com.lagou.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/oss")
public class OssController {

    @Autowired
    private OssService ossService;


    @PostMapping("/upload")
    public OssResult upload(@RequestParam("file") MultipartFile multipartFile) {
        return ossService.upload(multipartFile);
    }

    @GetMapping("/download")
    public OssResult download(String fileName, String downloadPath, String downloadFileName) {
        return ossService.download(fileName, downloadPath, downloadFileName);
    }

    @DeleteMapping("/delete")
    public OssResult delete(String fileName) {
        return ossService.delete(fileName);
    }

}
