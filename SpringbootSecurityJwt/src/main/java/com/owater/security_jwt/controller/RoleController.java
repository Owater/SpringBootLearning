package com.owater.security_jwt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Owater
 * @Date 2019/7/14
 **/
@RestController
public class RoleController {

    /**
     * 测试普通权限
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ROLE_NORMAL')")
    @GetMapping(value="/normal/test")
    public String test1() {
        return "普通角色访问";
    }

    /**
     * 测试管理员权限
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/admin/test")
    public String test2() {
        return "管理员访问";
    }
}
