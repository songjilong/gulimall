package com.sjl.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author songjilong
 * @date 2020/4/14 20:38
 */
@Data
public class PurchaseDoneVo {
    private Long id;
    private List<PurchaseDoneItemVo> items;
}
