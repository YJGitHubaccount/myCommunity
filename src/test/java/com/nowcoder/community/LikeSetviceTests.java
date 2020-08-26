package com.nowcoder.community;

import com.nowcoder.community.service.LikeService;
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
public class LikeSetviceTests implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Test
    public void testLike(){
        likeService.like(21,ENTITY_TYPE_POST,281,153);
        likeService.like(21,ENTITY_TYPE_POST,283,153);
        likeService.like(21,ENTITY_TYPE_POST,284,153);
        likeService.like(22,ENTITY_TYPE_POST,281,153);
        likeService.like(23,ENTITY_TYPE_POST,281,153);
        likeService.like(24,ENTITY_TYPE_POST,281,153);
        likeService.like(25,ENTITY_TYPE_POST,281,153);
    }
}
