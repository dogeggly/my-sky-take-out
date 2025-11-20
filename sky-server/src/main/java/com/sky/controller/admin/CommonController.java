package com.sky.controller.admin;

import com.aliyuncs.exceptions.ClientException;
import com.sky.constant.MessageConstant;
import com.sky.exception.UploadException;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/admin/common")
@Slf4j
@RestController
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException, ClientException {
        log.info("文件上传：{}", file);
        if (file == null || file.getOriginalFilename() == null) {
            throw new UploadException(MessageConstant.UPLOAD_FAILED);
        }
        String url = aliOssUtil.upload(file.getBytes(), file.getOriginalFilename());
        return Result.success(url);
    }
}
