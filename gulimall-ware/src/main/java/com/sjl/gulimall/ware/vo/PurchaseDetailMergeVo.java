package com.sjl.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author songjilong
 * @date 2020/4/14 16:48
 */
@Data
public class PurchaseDetailMergeVo {
    private Long purchaseId;
    private List<Long> items;
}
