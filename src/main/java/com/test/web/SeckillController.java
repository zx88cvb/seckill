package com.test.web;

import com.test.dto.Exposer;
import com.test.dto.SeckillExecution;
import com.test.dto.SeckillResult;
import com.test.entity.Seckill;
import com.test.enums.SeckillStatEnum;
import com.test.exception.RepeatKillException;
import com.test.exception.SeckillCloseException;
import com.test.exception.SeckillException;
import com.test.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/7/9.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Resource
    private SeckillService seckillService;

    /**
     * 列表页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";
    }

    /**
     * 详情页
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail",method=RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
         if(seckillId==null){
             return "redirect:/seckill/list";
         }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill==null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    /**
     * 是否开启秒杀
     * @param seckillId
     * @return
     */
    @RequestMapping(value = "/{seckillId}/exposer",method = RequestMethod.POST,
    produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result=new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result=new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    /**
     * 秒杀商品
     * @param seckillId
     * @param md5
     * @param phone
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
    method = RequestMethod.POST,
    produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable Long seckillId,
                                                   @PathVariable  String md5,
                                                   @CookieValue(value = "killPhone",required = false) Long phone){
        if(phone==null){
            return  new SeckillResult<SeckillExecution>(false,"未注册");
        }
        SeckillResult<SeckillExecution> result;
        try {
            //存储过程调用
            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (RepeatKillException e){
            SeckillExecution execution =new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (SeckillCloseException e){
            SeckillExecution execution =new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (SeckillException e) {
            SeckillExecution execution =new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
    }

    /**
     * 获取系统当前时间
     * @return
     */
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now=new Date();
        return new SeckillResult(true,now.getTime());
    }
}
