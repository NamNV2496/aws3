package com.example.aws3.controller;

import com.example.aws3.service.S3Service;
import com.google.common.io.ByteSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @Value("classpath:file/test.docx")
    Resource resourceFile;

    @SneakyThrows
    @GetMapping("/test")
    public String uploadFile() {
//        ============== WAY 1 ==============
        String filePath = "/path/to/file";
        // file to byte[], Path
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
//        ============== WAY 2 ==============
        // file to byte[], File -> Path
//        File file = new File(filePath);
//        byte[] bytes = Files.readAllBytes(file.toPath());

        String fileName = s3Service.saveToS3(bytes.length, ByteSource.wrap(bytes).openStream());
        String shortLink = s3Service.downloadLinkGenerate(fileName);

        s3Service.downloadFile(fileName);
        return "fileName: " + fileName + " ShortLink: " + shortLink;
    }
}
