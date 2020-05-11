package com.sjl.gulimall.ware.dao;

import com.sjl.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-02 01:11:03
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    /**
     * 商品入库
     * @param skuId 商品id
     * @param wareId 仓库id
     * @param skuNum 商品数量
     */
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long countStock(Long skuId);
}
