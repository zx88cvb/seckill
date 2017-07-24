package com.test.service;

import com.test.dto.Exposer;
import com.test.dto.SeckillExecution;
import com.test.entity.Seckill;
import com.test.exception.RepeatKillException;
import com.test.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/7/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Resource
    private SeckillService seckillService;
    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("list={}",seckillList);
    }

    @Test
    public void getById() throws Exception {
        Seckill seckillServiceById = seckillService.getById(1000L);
        logger.info("seckill={}",seckillServiceById);
    }

    @Test
    public void testSeckillLogic() throws Exception {
        Long id=1001L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            try {
                logger.warn("exposer={}",exposer);
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, 12345678904L, exposer.getMd5());
                logger.info("seckillExecution={}",seckillExecution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else{
            //秒杀未开启
            logger.warn("exposer={}",exposer);
        }

        //20:52:49.169 [main] INFO  com.test.service.SeckillServiceTest - exposer=Exposer{exposed=true, md5='04558d397f13ec4b5d42aaa839956ab2', seckillId=1000, now=null, start=null, end=null}
    }

    @Test
    public void executeSeckillProcedure() throws Exception {
        long seckillId=1001L;
        long phone=12345678909L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5=exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info(execution.getStateInfo());
        }
    }

}