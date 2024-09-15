package com.springboot.publicplace.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3UploadService {

    public String saveFile(MultipartFile multipartFile) throws IOException;
}
