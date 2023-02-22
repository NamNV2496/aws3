package com.example.aws3.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.IOUtils;
import com.example.aws3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {
    @Value("${fptcloud.s3.bucket}")
    private String bucketName;

    @Value("${fptcloud.s3.region}")
    private String region;

    @Value("${fptcloud.s3.expTime}")
    private long expTime;

    private final AmazonS3 s3Client;

    @Override
    public String saveToS3(long fileSize, InputStream inputStream) {
        String fileName = "Output" + "_" + Instant.now().toEpochMilli();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        s3Client.putObject(bucketName, fileName, inputStream, metadata);
        return fileName;
    }

    @Override
    public String downloadLinkGenerate(String fileName) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.GET)
                .withExpiration(new Date(Instant.now().toEpochMilli() + expTime));

        URL presignedUrl = s3Client.generatePresignedUrl(request);

        if (presignedUrl == null) {
            log.warn("[generatePresignedUrl] - Generate error");
            return null;
        }
        return presignedUrl.toString();
    }

    @SneakyThrows
    @Override
    public byte[] downloadFile(String fileName) {
        log.info("Downloading with key: {}", fileName);
        return IOUtils.toByteArray(s3Client.getObject(bucketName, fileName).getObjectContent());
    }


}
