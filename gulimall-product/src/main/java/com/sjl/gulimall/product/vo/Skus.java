/**
  * Copyright 2020 bejson.com 
  */
package com.sjl.gulimall.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2020-04-12 21:18:6
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Skus {
    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;
    private List<String> descar;

    private List<Attr> attr;

    private List<Images> images;

    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int countStatus;
    private int fullCount;
    private BigDecimal discount;
    private BigDecimal priceStatus;

    private List<MemberPrice> memberPrice;
}