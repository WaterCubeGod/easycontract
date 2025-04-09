package com.easycontract.service;

import com.easycontract.entity.dto.UserDTO;
import com.easycontract.entity.po.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    UserDTO createUser(User user, List<String> roles);
    UserDTO updateUser(User user);
    boolean deleteUser(Long id);
    boolean assignRoles(Long userId, List<String> roleCodes);
}
