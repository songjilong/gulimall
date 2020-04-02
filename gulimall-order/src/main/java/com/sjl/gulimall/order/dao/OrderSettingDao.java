package com.sjl.gulimall.order.dao;

import com.sjl.gulimall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-02 01:02:42
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
