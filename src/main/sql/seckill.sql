-- 秒杀执行存储过程
DELIMITER $$ -- ;转换为 $$
-- 定义存储过程
-- in:输入参数 out:输出参数
-- row_count() 返回上一条修改类型sql影响行数 (除了select)
-- row_count() 0:未修改数据  >0修改行数  <0 sql错误/未执行
CREATE PROCEDURE `seckill`.`execute_seckill`
  (in v_seckill_id BIGINT,in v_phone BIGINT,
  in v_kill_time TIMESTAMP,OUT r_result INT)
  BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION ;
    INSERT IGNORE INTO success_killed
    (seckill_id,user_phone,create_time)
      VALUE (v_seckill_id,v_phone,v_kill_time);
    SELECT row_count() INTO insert_count;
    IF(insert_count=0) THEN
      ROLLBACK ;
      SET r_result=-1;
    ELSEIF (insert_count<0) THEN
      ROLLBACK ;
      SET r_result=-2;
    ELSE
      UPDATE seckill
        set number=number-1
      WHERE seckill_id=v_seckill_id
      AND end_time>v_kill_time
      and start_time<v_kill_time
      AND number>0;
      SELECT row_count() INTO insert_count;
      IF (insert_count=0) THEN
        ROLLBACK ;
        SET r_result=0;
      ELSEIF (insert_count<0) THEN
        ROLLBACK ;
        set r_result=-2;
      ELSE
        COMMIT ;
        SET r_result=1;
      END IF ;
    END IF ;
  END;
$$
-- 存储过程定义结束
DELIMITER ;

set @r_result=-3;
-- 执行存储过程
CALL execute_seckill(1003,12345678908,now(),@r_result);
-- 获取结果
SELECT @r_result;
