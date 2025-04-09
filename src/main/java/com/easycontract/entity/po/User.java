package com.easycontract.entity.po;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer status; // 0-禁用，1-启用
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 非数据库字段，用于存储用户角色
    private transient List<Role> roles;
}
