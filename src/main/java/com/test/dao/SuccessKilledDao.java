package com.test.dao;

import com.test.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017/6/25.
 */
public interface SuccessKilledDao {
    /**
     * 插入购买明细,可过滤重复
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") Long seckillId,@Param("userPhone") long userPhone);

    /**
     * 根据id查询SuccessKilled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") Long seckillId,@Param("userPhone") long userPhone);
}
