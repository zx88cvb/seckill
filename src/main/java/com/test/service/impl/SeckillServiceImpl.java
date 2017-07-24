package com.test.service.impl;

import com.test.dao.SeckillDao;
import com.test.dao.SuccessKilledDao;
import com.test.dao.cache.RedisDao;
import com.test.dto.Exposer;
import com.test.dto.SeckillExecution;
import com.test.entity.Seckill;
import com.test.entity.SuccessKilled;
import com.test.enums.SeckillStatEnum;
import com.test.exception.RepeatKillException;
import com.test.exception.SeckillCloseException;
import com.test.exception.SeckillException;
import com.test.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/6/29.
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private final String salt="sagdfgfdd.[psdag.aer.568165e";
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Resource
    private SeckillDao seckillDao;

    @Resource
    private SuccessKilledDao successKilledDao;

    @Resource
    private RedisDao redisDao;

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(Long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(Long seckillId) {

        //缓存优化 Redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill==null){
            //访问数据库
            seckill = seckillDao.queryById(seckillId);
            if(seckill==null){
                return new Exposer(false,seckillId);
            }else {
                //放入redis
                redisDao.putSeckill(seckill);
            }
        }

        Long startTime=seckill.getStartTime().getTime();
        Long endTime=seckill.getEndTime().getTime();
        Long nowTime=new Date().getTime();
        if(nowTime<startTime || nowTime>endTime){
            return new Exposer(false,seckillId,nowTime,startTime,endTime);
        }
        String md5;
        md5=getMd5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    private String getMd5(Long seckillId){
        String base=seckillId+"/"+salt;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    public SeckillExecution executeSeckill(Long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5==null || !md5.equals(getMd5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        try {
            //购买记录行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if(insertCount<=0){
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            }else{
                //减库存 热点商品竞争
                int count = seckillDao.reduceNumber(seckillId, new Date());
                if(count<=0){
                    throw new SeckillCloseException("seckill is closed");
                }else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }

            }


        }catch (RepeatKillException e1){
            throw e1;
        }catch (SeckillCloseException e2){
            throw e2;
        }catch (Exception s){
            logger.error(s.getMessage(),s);
            throw new SeckillException("seckill inner error"+s.getMessage());
        }
    }

    public SeckillExecution executeSeckillProcedure(Long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5==null || !md5.equals(getMd5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime=new Date();
        Map<String, Object> map=new HashMap<String,Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程 result被赋值
        try {
            seckillDao.killByProcedure(map);
            //获取result
            Integer result = MapUtils.getInteger(map, "result", -2);
            if(result==1){
                SuccessKilled successKilled =
                        successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,successKilled);
            }else {
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);//
        }

    }
}
