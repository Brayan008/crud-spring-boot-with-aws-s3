/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
