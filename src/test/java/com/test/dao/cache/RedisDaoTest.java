package com.test.dao.cache;

import com.test.dao.SeckillDao;
import com.test.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/7/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
    private long id =1001;
    @Resource
    private RedisDao redisDao;

    @Resource
    private SeckillDao seckillDao;
    @Test
    public void testSeckill() throws Exception {
        Seckill seckill = redisDao.getSeckill(id);
        if(seckill==null){
            seckill = seckillDao.queryById(id);
            if(seckill!=null){
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill=redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }

}