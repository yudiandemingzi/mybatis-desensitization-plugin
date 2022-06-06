package com.jincou.data.desensitization.controller;


import com.jincou.data.desensitization.entity.User;
import com.jincou.data.desensitization.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
   private UserMapper userMapper;

    @GetMapping("/test")
    User get(Integer id){
     return  userMapper.selectByPrimaryKey(id);
    }
}
