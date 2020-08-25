package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey,100));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey,10));
    }

    @Test
    public void testHashs(){
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","zhangshan");

        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }

    @Test
    public void testList(){
        String redisKey = "test:ids";

        redisTemplate.opsForList().rightPush(redisKey,101);
        redisTemplate.opsForList().rightPush(redisKey,102);
        redisTemplate.opsForList().rightPush(redisKey,103);
        redisTemplate.opsForList().rightPush(redisKey,104);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
    }

    @Test
    public void testSet(){
        String redisKey = "test:teachers";

        redisTemplate.opsForSet().add(redisKey,"aaa","bbb","ccc","ddd","eee","eee");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));

    }

    @Test
    public void testSortedSets(){
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey,"aaa",80);
        redisTemplate.opsForZSet().add(redisKey,"bbb",90);
        redisTemplate.opsForZSet().add(redisKey,"ccc",50);
        redisTemplate.opsForZSet().add(redisKey,"ddd",70);
        redisTemplate.opsForZSet().add(redisKey,"eee",10);
        redisTemplate.opsForZSet().add(redisKey,"fff",80);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"aaa"));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"aaaaa"));
        System.out.println(redisTemplate.opsForZSet().remove(redisKey,"aaa"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"ccc"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"ccc"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,2));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,2));
    }

    @Test
    public void testKeys(){
        redisTemplate.delete("test:user");

        System.out.println(redisTemplate.hasKey("test:user"));

        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);

    }

    //多次访问一个key
    @Test
    public void testBoundOperations(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        System.out.println(operations.get());
        System.out.println(operations.increment());
        System.out.println(operations.decrement());
        System.out.println(operations.increment(1000));
        System.out.println(operations.decrement(900));
    }


    //事务
    @Test
    public void testTransactional(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                redisOperations.multi();

                redisOperations.opsForSet().add(redisKey,"aaa");
                redisOperations.opsForSet().add(redisKey,"bbb");
                redisOperations.opsForSet().add(redisKey,"ccc");
                redisOperations.opsForSet().add(redisKey,"aaa");

                System.out.println(redisOperations.opsForSet().members(redisKey));

                return redisOperations.exec();
            }
        });

        System.out.println(obj);
    }
}
