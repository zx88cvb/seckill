package com.test.service;

import com.test.dto.Exposer;
import com.test.dto.SeckillExecution;
import com.test.entity.Seckill;
import com.test.exception.RepeatKillException;
import com.test.exception.SeckillCloseException;
import com.test.exception.SeckillException;

import java.util.List;

/**
 * Created by Administrator on 2017/6/29.
 */
public interface SeckillService {
    /**
     * 查询秒杀所有记录
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(Long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(Long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    SeckillExecution executeSeckill(Long seckillId,long userPhone,String md5)
            throws SeckillException,RepeatKillException,SeckillCloseException;

    /**
     * 执行秒杀操作by 存储过程
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    SeckillExecution executeSeckillProcedure(Long seckillId,long userPhone,String md5);
}
