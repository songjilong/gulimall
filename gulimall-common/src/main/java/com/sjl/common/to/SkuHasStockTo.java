package com.sjl.common.to;

import lombok.Data;

/**
 * @author songjilong
 * @date 2020/5/11 14:42
 */
@Data
public class SkuHasStockTo {
    private Long skuId;
    private Boolean hasStock;
}
