package com.nowcoder.community;

import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityUtilTests {

    @Test
    public void testGetJsonString(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        //System.out.println(map);
        System.out.println(CommunityUtil.getJsonString(0,"ok",map));
    }
}
