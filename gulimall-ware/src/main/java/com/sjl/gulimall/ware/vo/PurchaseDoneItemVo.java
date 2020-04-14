package com.sjl.gulimall.ware.vo;

import lombok.Data;

/**
 * @author songjilong
 * @date 2020/4/14 20:38
 */
@Data
public class PurchaseDoneItemVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
