package com.musiclogin.dao;

import com.musiclogin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问层 - JPA 自动实现 CRUD
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /** 根据用户名和密码查询用户（登录） */
    User findByUsernameAndPassword(String username, String password);

    /** 根据用户名查询用户 */
    User findByUsername(String username);

    /** 根据邮箱查询用户 */
    User findByEmail(String email);

    /** 判断用户名是否存在 */
    boolean existsByUsername(String username);

    /** 判断邮箱是否存在 */
    boolean existsByEmail(String email);
}
