package desensitization.controller;



import desensitization.entity.User;
import desensitization.mapper.UserMapper;
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
