## Spring Boot With AWS S3 ##
Simple REST API for file upload with Spring Boot and AWS S3

USE:
> SpringBoot

> Springframework

> Lombok

> AWS Java JDK S3


## Account Configuration ##
You need create a bucket and configure it. Very important create a ACCESS KEY
![Alt text](/images/1.png "Configuration")
![Alt text](/images/2.png "Key Access")
![Alt text](/images/3.png "Bucket Policy")
## Test with postman ##
![Alt text](/images/4.png "4")
![Alt text](/images/5.png "5")
![Alt text](/images/6.png "6")
![Alt text](/images/7.png "7")
![Alt text](/images/8.png "8")
![Alt text](/images/9.png "9")

## Preview Service and Controller ##
```
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
```

```
package com.bmb.aws.controller;

import com.bmb.aws.dto.ResS3;
import com.bmb.aws.service.S3Service;
import com.bmb.demo.handler.ResponseHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author mares
 */
@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @GetMapping(value = "/list/{bucketName}")
    public ResponseEntity<Object> list(@PathVariable String bucketName) {
        try {
            return ResponseHandler.generateResponse("Success", HttpStatus.OK, s3Service.getBucketfiles(bucketName));
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Error", HttpStatus.MULTI_STATUS, e);
        }
    }

    @PutMapping("/{bucketName}/{fileKey}")
    public ResponseEntity<?> put(@PathVariable String bucketName,
            @PathVariable String fileKey,
            MultipartFile file) {
        try {
            return ResponseHandler.generateResponse("Success", HttpStatus.OK, s3Service.fileUpdate(bucketName, fileKey, file));
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Error", HttpStatus.MULTI_STATUS, e);
        }
    }

    @PostMapping(value = "/{bucketName}")
    public ResponseEntity<?> post(@PathVariable String bucketName, MultipartFile file) {
        try {
            return ResponseHandler.generateResponse("Success", HttpStatus.OK, s3Service.fileUpload(bucketName, file));
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Error", HttpStatus.MULTI_STATUS, e);
        }
    }

    @DeleteMapping("/{bucketName}/{fileKey}")
    public ResponseEntity<?> delete(@PathVariable String bucketName, @PathVariable String fileKey) {
        try {
            return ResponseHandler.generateResponse("Success", HttpStatus.OK, s3Service.deleteFile(bucketName, fileKey));
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Error", HttpStatus.MULTI_STATUS, e);
        }
    }

}

```


Inspired by **[faizakram](https://github.com/faizakram/spring-boot-with-aws-s3)**
