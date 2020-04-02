package com.sjl.gulimall.order.dao;

import com.sjl.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-02 01:02:42
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
