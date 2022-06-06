package desensitization.controller;



import desensitization.entity.User;
import desensitization.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
   private UserMapper userMapper;

    @GetMapping("/getById")
    User get(Integer id){
     return  userMapper.selectByPrimaryKey(id);
    }


    @GetMapping("/findAllUser")
    List<User> findAllUser(){
     return  userMapper.findAllUser();
    }



}
