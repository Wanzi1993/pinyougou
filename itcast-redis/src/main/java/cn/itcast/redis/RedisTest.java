package cn.itcast.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

/**
 * Date:2018-09-07
 * Author:Wanzi
 * Desc:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    //测试字符串objing
    @Test
    public void testString() {
        redisTemplate.boundValueOps("string_key").set("传智播客");
        Object obj = redisTemplate.boundValueOps("string_key").get();
        System.out.println(obj);
    }

    //测试散列hash
    @Test
    public void testHash(){
        redisTemplate.boundHashOps("hash_key").put("f1","v1");
        redisTemplate.boundHashOps("hash_key").put("f2","v2");
        List list = redisTemplate.boundHashOps("hash_key").values();
        System.out.println(list);
    }

    //测试列表
    @Test
    public void testList(){
        redisTemplate.boundListOps("list_key").leftPush(1);
        redisTemplate.boundListOps("list_key").leftPush(2);
        redisTemplate.boundListOps("list_key").rightPush(2);

        List list = redisTemplate.boundListOps("list_key").range(0, -1);

        System.out.println(list);
    }

    //测试集合
    @Test
    public void testSet(){
        redisTemplate.boundSetOps("set_key").add(1,3,5,"itcast",7);
        Set set = redisTemplate.boundSetOps("set_key").members();
        System.out.println(set);
    }

    //测试有序集合
    @Test
    public void testSortedSet(){
        redisTemplate.boundZSetOps("zset_key").add("aa",20);
        redisTemplate.boundZSetOps("zset_key").add("bb",10);
        redisTemplate.boundZSetOps("zset_key").add("cc",00);
        Set zset = redisTemplate.boundZSetOps("zset_key").range(0, -1);
        System.out.println(zset);
    }
}
