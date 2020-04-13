package com.sjl.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author songjilong
 * @date 2020/4/13 12:03
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int countStatus;

    private int fullCount;
    private BigDecimal discount;
    private BigDecimal priceStatus;

    private List<MemberPrice> memberPrice;
}
