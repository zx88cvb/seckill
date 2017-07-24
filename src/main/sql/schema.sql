CREATE DATABASE seckill;

use seckill;

CREATE TABLE seckill(
  `seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` VARCHAR(120) NOT NULL COMMENT '商品名称',
  `number` INT NOT NULL COMMENT '库存数量',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `start_time` TIMESTAMP NOT NULL COMMENT '秒杀开启时间',
  `end_time` TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (seckill_id),
  key idx_start_time(start_time),
  KEY idx_end_time(end_time),
  key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=UTF8 COMMENT='秒杀库存表';

-- 初始化数据
INSERT into
  seckill(name,number,start_time,end_time)
values
('1000元秒杀iphone8',100,'2017-6-25 17:26:49','2017-6-27 17:27:07'),
('500元秒杀魅族7PRO',500,'2017-6-25 17:26:49','2017-6-27 17:27:07'),
('2000元秒杀小米6',20,'2017-6-25 17:26:49','2017-6-27 17:27:07'),
('200元秒杀诺基亚',10,'2017-6-25 17:26:49','2017-6-27 17:27:07');

-- 秒杀成功明细表
CREATE TABLE success_killed(
  `seckill_id` BIGINT not NULL  COMMENT '秒杀商品id',
  `user_phone` BIGINT NOT NULL  COMMENT '用户手机号',
  `state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识 -1:无效 0:成功 1:已付款',
  `create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
  PRIMARY KEY (seckill_id,user_phone),/*联合主键*/
  KEY idx_create_time(create_time)
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8 COMMENT='秒杀成功明细表';