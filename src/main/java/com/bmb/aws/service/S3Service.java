/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmb.aws.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.bmb.aws.dto.ResS3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author mares
 */
@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    public ResS3 fileUpload(String bucketName, MultipartFile file) {
        ResS3 resS3 = new ResS3();
        String fileKey = "";
        try {
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                resS3.setMessage("Error in service, bucket Not Exist");
                return resS3;
            }
            fileKey = UUID.randomUUID() + file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, fileKey, file.getInputStream(), metadata);

        } catch (SdkClientException | IOException e) {
            resS3.setMessage("Error in service: " + e);
            return resS3;
        }
        resS3.setMessage("Success save file");
        resS3.setFile(fileKey);
        return resS3;
    }

    public ResS3 fileUpdate(String bucketName, String fileKey, MultipartFile file) {
        ResS3 resS3 = new ResS3();
        try {
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                resS3.setMessage("Error in service, bucket Not Exist");
                return resS3;
            }
            if (!amazonS3.doesObjectExist(bucketName, fileKey)) {
                resS3.setMessage("Error in service, file Not Exist");
                return resS3;
            }
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, fileKey, file.getInputStream(), metadata);

        } catch (SdkClientException | IOException e) {
            resS3.setMessage("Error in service: " + e);
            return resS3;
        }
        resS3.setMessage("Success updated file");
        resS3.setFile(fileKey);
        return resS3;
    }

    public ResS3 getBucketfiles(String bucketName) {
        ResS3 resS3 = new ResS3();
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            resS3.setMessage("Error in service, bucket Not Exist");
            return resS3;
        }
        resS3.setMessage("Success search files");
        resS3.setFiles(amazonS3.listObjectsV2(bucketName).getObjectSummaries().stream()
                .collect(Collectors.toList()));
        return resS3;
    }

    public ResS3 deleteFile(String bucketName, String fileKey) {
        ResS3 resS3 = new ResS3();
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            resS3.setMessage("Error in service, bucket Not Exist");
            return resS3;
        }
        if (!amazonS3.doesObjectExist(bucketName, fileKey)) {
            resS3.setMessage("Error in service, file Not Exist");
            return resS3;
        }
        amazonS3.deleteObject(bucketName, fileKey);
        resS3.setMessage("Success deleted file: ");
        resS3.setFile(fileKey);
        return resS3;
    }

}
