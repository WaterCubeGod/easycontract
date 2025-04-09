package com.easycontract.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer status;
    private List<String> roles;
}
