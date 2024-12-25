package com.supermall.backend.common.utils;

import com.supermall.backend.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class FileUtils {

    /**
     * 上传文件
     * @param file 文件
     * @param uploadPath 上传路径
     * @param subDir 子目录
     * @return 文件访问URL
     */
    public static String uploadFile(MultipartFile file, String uploadPath, String subDir) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new BusinessException("文件名不能为空");
            }

            // 获取项目根目录
            String projectPath = System.getProperty("user.dir");
            File uploadDir = new File(projectPath, uploadPath + "/" + subDir);
            
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    throw new BusinessException("创建上传目录失败");
                }
            }
            
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            File destFile = new File(uploadDir, filename);
            file.transferTo(destFile);
            
            return "/uploads/" + subDir + "/" + filename;
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new BusinessException("上传文件失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @param uploadPath 上传路径
     */
    public static void deleteFile(String fileUrl, String uploadPath) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String projectPath = System.getProperty("user.dir");
            String filePath = projectPath + "/" + uploadPath + fileUrl.substring(8); // 去掉 "/uploads/"
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.warn("Failed to delete file: {}", filePath);
                }
            }
        } catch (Exception e) {
            log.error("Failed to delete file", e);
        }
    }
} 