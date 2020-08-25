package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    RedisTemplate redisTemplate;

    //  点赞
    public void like(int userId, int entityType, int entityId){
        String key = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        boolean ismember = redisTemplate.opsForSet().isMember(key,userId);
        if (ismember){
            redisTemplate.opsForSet().remove(key,userId);
        }else {
            redisTemplate.opsForSet().add(key,userId);
        }
    }


    //查询某实体点赞数量
    public long findEntityLikeCount(int entityType, int entityId){
        String key = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(key);
    }

    //查询某人对实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String key = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(key,userId) ? 1 : 0;
    }
}
