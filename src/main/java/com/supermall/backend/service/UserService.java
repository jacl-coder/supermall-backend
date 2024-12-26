package com.supermall.backend.service;

import com.supermall.backend.dto.PasswordUpdateDTO;
import com.supermall.backend.dto.RegisterDTO;
import com.supermall.backend.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void register(RegisterDTO registerDTO);
    User getUserByUsername(String username);
    void updatePassword(String username, PasswordUpdateDTO passwordDTO);
    String updateAvatar(String username, MultipartFile file);
} 