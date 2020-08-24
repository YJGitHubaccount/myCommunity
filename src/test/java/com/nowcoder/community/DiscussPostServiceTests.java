package com.nowcoder.community;

import com.nowcoder.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class DiscussPostServiceTests {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void testFindDiscussPostById(){
        System.out.println(discussPostService.findDiscussPostById(153).toString());
    }
}
