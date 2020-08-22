package com.nowcoder.community;


import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class UserServiceTests implements CommunityConstant {

    @Autowired
    UserService userService;

    @Test
    public void testRegister(){
        User user = new User();
        user.setUsername("19924682983");
        user.setEmail("yangj557@mail2.sysu.edu.cn");
        user.setPassword("yj19924682983");
        Map<String,Object>map = userService.register(user);
        System.out.println(map);
    }

    @Test
    public void testActivation(){
        int ret = userService.activation(153,"6db0defc3c5d41cf9b10e0e324bdb515");
        System.out.println(ret);
    }

    @Test
    public void testLogin(){
        Map<String,Object> map = userService.login("19924682983","yj19924682983",CommunityConstant.DEFAULT_EXPIRED_SECONDS);
        System.out.println(map);//{ticket=92792e1b5bb84ca1aedb4b9992ccb87b}

        map = userService.login("19924682983","errorpassword",CommunityConstant.DEFAULT_EXPIRED_SECONDS);
        System.out.println(map);//{passwordMsg=密码不正确!}
    }

    @Test
    public void testLogout(){
        userService.logout("8144556ea0b140eea79a1713406a7430");
    }
}
