package com.km.service;

import com.km.entity.User;

import java.util.List;

public interface UserService {

    List<User> list();

    User findUserById(Long id);

    User findInfoById(Long id);

    void update(User user);

    void remove(Long id);

}
