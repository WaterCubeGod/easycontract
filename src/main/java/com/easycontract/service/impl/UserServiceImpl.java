package com.easycontract.service.impl;

import com.easycontract.entity.dto.UserDTO;
import com.easycontract.entity.po.Role;
import com.easycontract.entity.po.User;
import com.easycontract.entity.po.UserRole;
import com.easycontract.mapper.UserMapper;
import com.easycontract.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            user.setRoles(userMapper.findRolesByUserId(user.getId()));
        }
        return user;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return null;
        }

        List<Role> roles = userMapper.findRolesByUserId(id);
        return convertToDTO(user, roles);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userMapper.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            List<Role> roles = userMapper.findRolesByUserId(user.getId());
            userDTOs.add(convertToDTO(user, roles));
        }

        return userDTOs;
    }

    @Override
    @Transactional
    public UserDTO createUser(User user, List<String> roleCodes) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1); // 默认启用

        userMapper.insert(user);

        // 分配角色
        if (roleCodes != null && !roleCodes.isEmpty()) {
            for (String roleCode : roleCodes) {
                Role role = userMapper.findRoleByCode(roleCode);
                if (role != null) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(user.getId());
                    userRole.setRoleId(role.getId());
                    userMapper.insertUserRole(userRole);
                }
            }
        }

        List<Role> roles = userMapper.findRolesByUserId(user.getId());
        return convertToDTO(user, roles);
    }

    @Override
    @Transactional
    public UserDTO updateUser(User user) {
        // 如果密码不为空，则加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userMapper.update(user);

        List<Role> roles = userMapper.findRolesByUserId(user.getId());
        return convertToDTO(user, roles);
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        userMapper.deleteUserRoleByUserId(id);
        return userMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean assignRoles(Long userId, List<String> roleCodes) {
        // 先删除用户所有角色
        userMapper.deleteUserRoleByUserId(userId);

        // 重新分配角色
        if (roleCodes != null && !roleCodes.isEmpty()) {
            for (String roleCode : roleCodes) {
                Role role = userMapper.findRoleByCode(roleCode);
                if (role != null) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(role.getId());
                    userMapper.insertUserRole(userRole);
                }
            }
        }

        return true;
    }

    // 将User实体转换为UserDTO
    private UserDTO convertToDTO(User user, List<Role> roles) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());

        if (roles != null) {
            dto.setRoles(roles.stream().map(Role::getCode).collect(Collectors.toList()));
        }

        return dto;
    }
}
