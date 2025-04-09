package com.easycontract.controller;

import com.easycontract.entity.dto.LoginRequest;
import com.easycontract.entity.dto.LoginResponse;
import com.easycontract.entity.po.User;
import com.easycontract.entity.vo.Response;
import com.easycontract.security.JwtUtils;
import com.easycontract.security.UserDetailsImpl;
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
}
