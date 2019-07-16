package com.owater.security_jwt.service;

import com.owater.security_jwt.entity.User;

/**
 * @Author Owater
 * @Date 2019/7/14
 **/
public interface UserService {
    User register(User user);
    String login(String username, String password);
}
