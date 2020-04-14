package com.sjl.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjl.common.utils.PageUtils;
import com.sjl.gulimall.ware.entity.PurchaseEntity;
import com.sjl.gulimall.ware.vo.PurchaseDetailMergeVo;
import com.sjl.gulimall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-02 01:11:03
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils unreceiveList(Map<String, Object> params);

    void merge(PurchaseDetailMergeVo mergeVo);

    void receivePurchase(List<Long> ids);

    void done(PurchaseDoneVo doneVo);
}

