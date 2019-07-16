package com.owater.security_jwt.repository;

import com.owater.security_jwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Owater
 * @Date 2019/7/14
 **/
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername( String username );
}
