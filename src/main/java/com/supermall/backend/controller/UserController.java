package com.supermall.backend.controller;

import com.supermall.backend.common.response.Result;
import com.supermall.backend.dto.UserLoginDTO;
import com.supermall.backend.dto.UserRegisterDTO;
import com.supermall.backend.dto.UserUpdateDTO;
import com.supermall.backend.dto.PasswordUpdateDTO;
import com.supermall.backend.entity.User;
import com.supermall.backend.service.UserService;
import com.supermall.backend.vo.UserLoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserLoginVO> register(@Valid @RequestBody UserRegisterDTO userDTO) {
        return Result.success(userService.register(userDTO));
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    @GetMapping("/check/username/{username}")
    public Result<Boolean> checkUsername(@PathVariable String username) {
        return Result.success(userService.checkUsernameExists(username));
    }

    @GetMapping("/check/email/{email}")
    public Result<Boolean> checkEmail(@PathVariable String email) {
        return Result.success(userService.checkEmailExists(email));
    }

    @GetMapping("/check/phone/{phone}")
    public Result<Boolean> checkPhone(@PathVariable String phone) {
        return Result.success(userService.checkPhoneExists(phone));
    }

    @GetMapping("/profile")
    public Result<User> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userService.getUserByUsername(username);
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@Valid @RequestBody UserUpdateDTO updateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User updatedUser = userService.updateProfile(username, updateDTO);
        return Result.success(updatedUser);
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO passwordDTO) {
        // 暂时使用固定用户ID
        Long userId = 1L;
        userService.updatePassword(String.valueOf(userId), passwordDTO);
        return Result.success(null);
    }

    @PostMapping("/avatar")
    public Result<String> updateAvatar(@RequestParam("file") MultipartFile file) {
        // 暂时使用固定用户ID
        Long userId = 1L;
        return Result.success(userService.updateAvatar(String.valueOf(userId), file));
    }
} 