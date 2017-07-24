package com.test.dao;

import com.test.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/25.
 */
public interface SeckillDao {
    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber( @Param("seckillId") Long seckillId,@Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀对象
     * @param seckillId
     * @return
     */
    Seckill queryById(Long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offet
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offet,@Param("limit") int limit);

    /**
     * 执行存储过程秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String,Object> paramMap);
}
