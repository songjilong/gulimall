package com.sjl.gulimall.ware.service.impl;

import com.alibaba.fastjson.JSON;
import com.sjl.common.to.SkuHasStockTo;
import com.sjl.common.to.SkuReductionTo;
import com.sjl.common.utils.R;
import com.sjl.gulimall.ware.feign.ProductFeignService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;

import com.sjl.gulimall.ware.dao.WareSkuDao;
import com.sjl.gulimall.ware.entity.WareSkuEntity;
import com.sjl.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        String wareId = (String) params.get("wareId");
        if(StringUtils.isNotBlank(wareId)){
            queryWrapper.eq("ware_id", wareId);
        }
        String skuId = (String) params.get("skuId");
        if(StringUtils.isNotBlank(wareId)){
            queryWrapper.eq("sku_id", skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 商品入库
     * @param skuId 商品id
     * @param wareId 仓库id
     * @param skuNum 商品数量
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuList = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId));
        if(wareSkuList == null || wareSkuList.size() == 0){
            WareSkuEntity entity = new WareSkuEntity();
            entity.setSkuId(skuId);
            entity.setWareId(wareId);
            entity.setStock(skuNum);
            //远程调用，获取商品名称，出现异常无需处理
            try {
                R r = productFeignService.info(skuId);
                if(r.getCode() == 0){
                    Map<String, Object> skuInfo = (Map<String, Object>) r.get("skuInfo");
                    entity.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception ignored) { }
            entity.setStockLocked(0);
            this.baseMapper.insert(entity);
        }else{
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockTo to = new SkuHasStockTo();
            Long stock = this.baseMapper.countStock(skuId);
            to.setSkuId(skuId);
            to.setHasStock(stock != null && stock > 0);
            return to;
        }).collect(Collectors.toList());
    }
}