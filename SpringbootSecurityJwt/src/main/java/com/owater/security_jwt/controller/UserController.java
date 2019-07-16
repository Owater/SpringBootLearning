package com.owater.security_jwt.controller;

import com.owater.security_jwt.entity.Result;
import com.owater.security_jwt.entity.User;
import com.owater.security_jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Owater
 * @Date 2019/7/14
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService authService;

    @PostMapping(value = "/login")
    public Result login(String username, String password) throws AuthenticationException {
        return new Result(authService.login(username, password));
    }

    @PostMapping(value = "/register")
    public Result register(@RequestBody User user) throws AuthenticationException {
        authService.register(user);
        return new Result("success");
    }
}
