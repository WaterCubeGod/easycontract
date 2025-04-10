package com.easycontract.service;

import com.easycontract.entity.dto.UserDTO;
import com.easycontract.entity.po.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByResetToken(String resetToken);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    UserDTO createUser(User user, List<String> roles);
    UserDTO updateUser(User user);
    void updatePassword(User user, String newPassword);
    boolean deleteUser(Long id);
    boolean assignRoles(Long userId, List<String> roleCodes);
}
