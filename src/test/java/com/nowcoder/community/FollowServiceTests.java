package com.nowcoder.community;

import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.util.CommunityConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class FollowServiceTests {

    @Autowired
    private FollowService followService;

    @Test
    public void testFollow(){
        for (int i=21;i<=25;i++){
            followService.follow(i, CommunityConstant.ENTITY_TYPE_USER,153);
        }
        for (int i=21;i<=25;i++){
            followService.follow(153, CommunityConstant.ENTITY_TYPE_USER,i);
        }
    }

    @Test
    public void testUnfollow(){
        for (int i=21;i<=25;i++){
            followService.unfollow(i, CommunityConstant.ENTITY_TYPE_USER,153);
        }
        for (int i=21;i<=25;i++){
            followService.unfollow(153, CommunityConstant.ENTITY_TYPE_USER,i);
        }
    }

}
