package com.lagou.service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;
import com.lagou.bean.OssResult;
import com.lagou.config.AliyunConfig;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class OssService {

    @Autowired
    private AliyunConfig aliyunConfig;

    @Autowired
    private OSSClient ossClient;

    // 允许上传的格式
    private static final String[] IMAGE_TYPE = new String[]{".jpeg", ".jpg", ".png"};

    public OssResult upload(MultipartFile multipartFile) {
        // 校验图片格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(multipartFile.getOriginalFilename(), type)) {
                isLegal = true;
                break;
            }
        }

        OssResult ossResult = new OssResult();
        if (!isLegal) {
            ossResult.setStatus("error");
            return ossResult;
        }

        // 校验图片大小
        long fileSize = aliyunConfig.getFileSize() * 1024 * 1024;
        if(multipartFile.getSize() > fileSize) {
            ossResult.setStatus("error");
            return ossResult;
        }

        String fileName = multipartFile.getOriginalFilename();
        String filePath = getFilePath(fileName);

        try {
            ossClient.putObject(aliyunConfig.getBucketName(), filePath,
                    new ByteArrayInputStream(multipartFile.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
            // 上传失败
            ossResult.setStatus("error");
            return ossResult;
        }
        ossResult.setStatus("done");
        ossResult.setName(aliyunConfig.getUrlPrefix() + filePath);
        ossResult.setUid(filePath);
        return ossResult;
    }

    public OssResult download(String fileName, String downloadPath, String downloadFileName) {

        OssResult ossResult = new OssResult();

        if (StringUtils.isEmpty(downloadFileName)) {
            downloadFileName = fileName;
        }

        try {
            ossClient.getObject(new GetObjectRequest(aliyunConfig.getBucketName(), fileName),
                    new File(downloadPath + "/" + downloadFileName));
        } catch (OSSException | ClientException e) {
            e.printStackTrace();
            ossResult.setStatus("error");
            return ossResult;
        }
        ossResult.setStatus("done");
        ossResult.setName(aliyunConfig.getUrlPrefix() + fileName);
        ossResult.setUid(fileName);
        return ossResult;
    }

    public OssResult delete(String fileName) {
        OssResult ossResult = new OssResult();
        try {
            ossClient.deleteObject(aliyunConfig.getBucketName(), fileName);
        } catch (OSSException | ClientException e) {
            e.printStackTrace();
            ossResult.setStatus("error");
            return ossResult;
        }
        ossResult.setStatus("done");
        ossResult.setName(aliyunConfig.getUrlPrefix() + fileName);
        ossResult.setUid(fileName);
        return ossResult;

    }

    // 生成不重复的文件路径和文件名
    private String getFilePath(String sourceFileName) {
        DateTime dateTime = new DateTime();
        return "images/" + dateTime.toString("yyyy")
                + "/" + dateTime.toString("MM") + "/"
                + dateTime.toString("dd") + "/" + UUID.randomUUID().toString() + "." +
                StringUtils.substringAfterLast(sourceFileName, ".");
    }
}
