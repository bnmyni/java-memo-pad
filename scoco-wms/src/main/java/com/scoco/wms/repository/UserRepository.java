package com.scoco.wms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scoco.wms.entity.User;

/**
 * 项目名称: 伟明丰查询系统 包名称: com.scoco.wms.repository 类名称: UserRepository.java 类描述: 系统用户dao 创建人: sunke 版本号:
 * 1.0.0.0 创建时间: 2018/11/22 20:47
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT u.* FROM t_user u WHERE status <> 'DELETE' AND expire_date> NOW()" +
            "and password_expire_date> now() and (lock_date is null or lock_date < now()) and user_name=?1 and " +
            "password=?2", nativeQuery = true)
    User login(String userName, String password);

}