package com.owater.security_jwt.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @Author Owater
 * @Date 2019/7/14
 **/
@Data
@Entity
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

}
