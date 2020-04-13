package com.sjl.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author songjilong
 * @date 2020/4/13 11:55
 */
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
