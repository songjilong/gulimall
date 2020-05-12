package com.sjl.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.sjl.common.constant.ProductConstant;
import com.sjl.common.to.SkuHasStockTo;
import com.sjl.common.to.SkuReductionTo;
import com.sjl.common.to.SpuBoundsTo;
import com.sjl.common.to.es.SkuEsModel;
import com.sjl.common.utils.R;
import com.sjl.gulimall.product.dao.AttrDao;
import com.sjl.gulimall.product.entity.*;
import com.sjl.gulimall.product.feign.CouponFeignService;
import com.sjl.gulimall.product.feign.SearchFeignService;
import com.sjl.gulimall.product.feign.WareFeignService;
import com.sjl.gulimall.product.service.*;
import com.sjl.gulimall.product.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;

import com.sjl.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Resource
    private AttrDao attrDao;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        Long spuId = spuInfoEntity.getId();

        //2、保存spu描述信息 pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(String.join(",", vo.getDecript()));
        spuInfoDescService.saveSpuDesc(spuInfoDescEntity);

        //3、保存spu图片信息 pms_spu_images
        List<String> images = vo.getImages();
        List<SpuImagesEntity> spuImagesList = images.stream().map(image -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setSpuId(spuId);
            spuImagesEntity.setImgUrl(image);
            return spuImagesEntity;
        }).filter(image -> StringUtils.isNotBlank(image.getImgUrl()))
                .collect(Collectors.toList());
        spuImagesService.saveSpuImages(spuImagesList);

        //4、保存spu规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueList = baseAttrs.stream().map(baseAttr -> {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setSpuId(spuId);
            entity.setAttrId(baseAttr.getAttrId());
            entity.setQuickShow(baseAttr.getShowDesc());
            entity.setAttrValue(baseAttr.getAttrValues());
            entity.setAttrName(attrService.getById(baseAttr.getAttrId()).getAttrName());
            return entity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBaseAttrs(productAttrValueList);

        //5、保存spu积分信息 gulimall_sms -> sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        spuBoundsTo.setSpuId(spuId);
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        R r1 = couponFeignService.save(spuBoundsTo);
        if (r1.getCode() != 0) {
            log.error("保存spu积分信息出错");
        }

        //6、保存sku信息
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(sku -> {
                SkuInfoEntity skuInfo = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfo);
                skuInfo.setSpuId(spuId);
                skuInfo.setBrandId(spuInfoEntity.getBrandId());
                skuInfo.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfo.setSaleCount(0L);
                skuInfo.setSkuDesc("");
                List<Images> skuImages = sku.getImages();
                String defaultImage = "";
                //遍历得到默认图片
                for (Images skuImage : skuImages) {
                    if (skuImage.getDefaultImg() == 1) {
                        defaultImage = skuImage.getImgUrl();
                    }
                }
                skuInfo.setSkuDefaultImg(defaultImage);
                //6.1、sku基本信息 pms_sku_info
                skuInfoService.saveSkuInfo(skuInfo);

                Long skuId = skuInfo.getSkuId();
                List<SkuImagesEntity> skuImagesList = skuImages.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(image.getImgUrl());
                    skuImagesEntity.setDefaultImg(image.getDefaultImg());
                    return skuImagesEntity;
                }).filter(image -> StringUtils.isNotBlank(image.getImgUrl()))
                        .collect(Collectors.toList());
                //6.2、保存sku图片信息 pms_sku_images
                skuImagesService.saveImages(skuImagesList);

                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrList = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity saleAttr = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, saleAttr);
                    saleAttr.setSkuId(skuId);
                    return saleAttr;
                }).collect(Collectors.toList());
                //6.3、sku销售属性 pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(saleAttrList);

                //6.4、sku优惠、满减等信息 gulimall_sms -> sms_sku_ladder/sms_sku_full_reduction/sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                skuReductionTo.setSkuId(skuId);
                BeanUtils.copyProperties(sku, skuReductionTo);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1) {
                    R r2 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r2.getCode() != 0) {
                        log.error("保存sku优惠、满减等信息出错");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(wrapper -> wrapper.eq("id", key).or().like("spu_name", key));
        }
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String status = (String) params.get("status");
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("publish_status", status);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        //根据spuId查出所有sku
        List<SkuInfoEntity> skuInfoList = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        //查询当前sku的所有可以被检索的规格属性
        //1、查出所有的属性
        List<ProductAttrValueEntity> productAttrValues = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        //2、获得上一步的属性id
        List<Long> attrIds = productAttrValues.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        //3、找到可被检索的属性id
        List<Long> attrs = attrService.querySearchAttrIds(attrIds);
        //4、得到结果
        List<SkuEsModel.Attrs> searchAttrs = productAttrValues.stream()
                .filter(productAttrValue -> attrs.contains(productAttrValue.getAttrId()))
                .map(productAttrValue -> {
                    SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(productAttrValue, attrs1);
                    return attrs1;
                }).collect(Collectors.toList());

        //获取当前sku是否有库存
        //1、得到skuId集
        List<Long> skuIds = skuInfoList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        Map<Long, Boolean> stockMap = null;
        try {
            //2、远程调用ware的方法
            R skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            //3、讲结果封装为map
            stockMap = skuHasStock.getData(new TypeReference<List<SkuHasStockTo>>(){}).stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
        }catch (Exception e){
            log.error("远程调用仓储服务出错：{}", e);
        }
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skuInfoList.stream().map(sku -> {
            SkuEsModel skuEs = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEs);
            skuEs.setSkuPrice(sku.getPrice());
            skuEs.setSkuImg(sku.getSkuDefaultImg());
            skuEs.setAttrs(searchAttrs);
            //远程调用库存系统获取 hasStock
            skuEs.setHasStock(finalStockMap == null ? true : finalStockMap.get(sku.getSkuId()));
            //热度评分hostScore 0
            skuEs.setHotScore(0L);
            //查询品牌信息
            BrandEntity brand = brandService.getById(skuEs.getBrandId());
            skuEs.setBrandName(brand.getName());
            skuEs.setBrandImg(brand.getLogo());
            //查询分类信息
            CategoryEntity category = categoryService.getById(skuEs.getCatalogId());
            skuEs.setCatalogName(category.getName());
            return skuEs;
        }).collect(Collectors.toList());

        //将数据发送给ES进行保存：gulimall-search
        R r = searchFeignService.productStatusUp(collect);
        if(r.getCode() == 0){
            //成功，改变商品状态
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setUpdateTime(new Date());
            spuInfoEntity.setPublishStatus(ProductConstant.StatusEnum.SPU_UP.getCode());
            this.baseMapper.updateById(spuInfoEntity);
        }else{
            //失败
            //TODO 重复调用，接口幂等性，重试机制
        }
    }
}