package com.sjl.gulimall.product.dao;

import com.sjl.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-01 23:18:20
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
