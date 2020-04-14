package com.sjl.gulimall.ware.service.impl;

import com.sjl.common.constant.WareConstant;
import com.sjl.gulimall.ware.entity.PurchaseDetailEntity;
import com.sjl.gulimall.ware.service.PurchaseDetailService;
import com.sjl.gulimall.ware.service.WareSkuService;
import com.sjl.gulimall.ware.vo.PurchaseDetailMergeVo;
import com.sjl.gulimall.ware.vo.PurchaseDoneItemVo;
import com.sjl.gulimall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;

import com.sjl.gulimall.ware.dao.PurchaseDao;
import com.sjl.gulimall.ware.entity.PurchaseEntity;
import com.sjl.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils unreceiveList(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", WareConstant.PurchaseStatusEnum.CREATED.getCode()).or()
                        .eq("status", WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void merge(PurchaseDetailMergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();

        //采购单状态是0、1才能合并
        int status = this.getById(purchaseId).getStatus();
        if(status != WareConstant.PurchaseStatusEnum.CREATED.getCode() && status != WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()){
            return;
        }

        if (purchaseId == null) {
            //新建采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //合并
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;



        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(finalPurchaseId);
        entity.setUpdateTime(new Date());
        this.updateById(entity);
    }

    @Transactional
    @Override
    public void receivePurchase(List<Long> ids) {
        //确认采购单状态是新建或已分配，过滤掉其他的
        List<PurchaseEntity> purchaseList = ids.stream()
                .map(this::getById)
                .filter(purchase -> purchase.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || purchase.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .peek(purchaseEntity -> {
                    purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    purchaseEntity.setUpdateTime(new Date());
                }).collect(Collectors.toList());

        //改变采购单的状态
        this.updateBatchById(purchaseList);

        //改变采购单下的采购需求的状态
        purchaseList.forEach(purchaseEntity -> {
            List<PurchaseDetailEntity> purchaseDetailList = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseEntity.getId()));
            List<PurchaseDetailEntity> collect = purchaseDetailList.stream().map(purchaseDetail -> {
                PurchaseDetailEntity entity = new PurchaseDetailEntity();
                entity.setId(purchaseDetail.getId());
                entity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
        });

    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo doneVo) {
        //如果状态是已完成直接结束
        PurchaseEntity purchaseEntity = this.getById(doneVo.getId());
        if(purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.FINISH.getCode()){
            return;
        }
        //1、改变采购项状态
        List<PurchaseDoneItemVo> items = doneVo.getItems();
        //记录采购中是否出错
        boolean hasError = false;
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseDoneItemVo item : items) {
            Long itemId = item.getItemId();
            Integer status = item.getStatus();
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            entity.setId(itemId);
            entity.setStatus(status);
            if(status == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                hasError = true;
            }else{
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                //3、将成功采购的入库
                wareSkuService.addStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum());
            }
            updates.add(entity);
        }
        purchaseDetailService.updateBatchById(updates);

        //3、改变采购单状态
        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(doneVo.getId());
        entity.setStatus(hasError ? WareConstant.PurchaseStatusEnum.HASERROR.getCode() : WareConstant.PurchaseStatusEnum.FINISH.getCode());
        this.updateById(entity);
    }
}