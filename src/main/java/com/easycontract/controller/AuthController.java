package com.easycontract.controller;

import com.easycontract.entity.dto.LoginRequest;
import com.easycontract.entity.dto.LoginResponse;
import com.easycontract.entity.po.User;
import com.easycontract.entity.vo.Response;
import com.easycontract.security.JwtUtils;
import com.easycontract.security.UserDetailsImpl;
import com.easycontract.service.EmailService;
import com.easycontract.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public Response<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return Response.success(new LoginResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles));
        } catch (Exception e) {
            return Response.fail("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public Response<?> register(@RequestBody User user) {
        try {
            // 检查用户名是否已存在
            if (userService.findByUsername(user.getUsername()) != null) {
                return Response.fail("用户名已存在");
            }

            // 默认注册为普通用户
            List<String> roles = new ArrayList<>();
            roles.add("ROLE_USER");

            return Response.success(userService.createUser(user, roles));
        } catch (Exception e) {
            return Response.fail("注册失败: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public Response<?> forgotPassword(@RequestParam String email) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                return Response.fail("该邮箱未注册");
            }

            // 生成重置令牌
            String resetToken = UUID.randomUUID().toString();

            // 设置令牌过期时间（24小时）
            user.setResetToken(resetToken);
            user.setResetTokenExpireTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            userService.updateUser(user);

            // 发送重置密码邮件
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

            return Response.success("重置密码链接已发送到您的邮箱");
        } catch (Exception e) {
            return Response.fail("密码重置失败: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public Response<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            User user = userService.findByResetToken(token);
            if (user == null) {
                return Response.fail("无效的重置令牌");
            }

            // 检查令牌是否过期
            if (user.getResetTokenExpireTime() < System.currentTimeMillis()) {
                return Response.fail("重置令牌已过期");
            }

            // 更新密码
            userService.updatePassword(user, newPassword);

            // 清除重置令牌
            user.setResetToken(null);
            user.setResetTokenExpireTime(null);
            userService.updateUser(user);

            return Response.success("密码重置成功");
        } catch (Exception e) {
            return Response.fail("密码重置失败: " + e.getMessage());
        }
    }
}
