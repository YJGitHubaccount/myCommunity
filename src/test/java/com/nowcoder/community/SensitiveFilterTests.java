package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.Target;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveFilterTests {
    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void testFilter(){
        String text = "fu^%ck@# asd qwkej asd asd qw fuck 赌^博 嫖@娼";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
