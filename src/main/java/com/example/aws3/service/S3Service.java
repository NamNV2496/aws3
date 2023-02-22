package com.example.aws3.service;

import java.io.InputStream;

public interface S3Service {
    String saveToS3(long fileSize, InputStream inputStream);
    String downloadLinkGenerate(String fileName);
    byte[] downloadFile(String fileName);
}
