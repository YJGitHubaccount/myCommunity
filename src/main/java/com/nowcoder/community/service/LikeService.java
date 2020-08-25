package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param userId 点赞的用户
     * @param entityType
     * @param entityId
     * @param entityUserId 作者
     */
    public void like(int userId, int entityType, int entityId, int entityUserId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikekey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean ismember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);

                operations.multi();

                if (ismember){
                    redisTemplate.opsForSet().remove(entityLikeKey,userId);
                    redisTemplate.opsForValue().decrement(userLikekey);
                }else {
                    redisTemplate.opsForSet().add(entityLikeKey,userId);
                    redisTemplate.opsForValue().increment(userLikekey);
                }
                return operations.exec();
            }
        });
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

    //查询用户的赞
    public int findUserLikeCount(int userId){
        String userLikekey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer)redisTemplate.opsForValue().get(userLikekey);
        return count == null ? 0 : count;
    }
}
