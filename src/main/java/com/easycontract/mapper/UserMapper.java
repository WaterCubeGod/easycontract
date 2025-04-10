package com.easycontract.mapper;

import com.easycontract.entity.po.Role;
import com.easycontract.entity.po.User;
import com.easycontract.entity.po.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    // 用户相关
    User findByUsername(String username);
    User findById(Long id);
    User findByEmail(String email);
    User findByResetToken(String resetToken);
    List<User> findAll();
    int insert(User user);
    int update(User user);
    int deleteById(Long id);

    // 角色相关
    List<Role> findRolesByUserId(Long userId);
    Role findRoleById(Long id);
    Role findRoleByCode(String code);

    // 用户角色关联
    int insertUserRole(UserRole userRole);
    int deleteUserRoleByUserId(Long userId);
}
