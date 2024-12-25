package com.supermall.backend.controller;

import com.supermall.backend.common.response.Result;
import com.supermall.backend.util.FileUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {
        return Result.success(FileUtils.uploadFile(file, uploadPath, type));
    }

    @PostMapping("/batch-upload")
    public Result<List<String>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("type") String type) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(FileUtils.uploadFile(file, uploadPath, type));
        }
        return Result.success(urls);
    }
} 