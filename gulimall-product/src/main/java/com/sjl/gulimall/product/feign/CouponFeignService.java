package com.sjl.gulimall.product.feign;

import com.sjl.common.to.SkuReductionTo;
import com.sjl.common.to.SpuBoundsTo;
import com.sjl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author songjilong
 * @date 2020/4/13 11:50
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * 保存spu积分信息
     */
    @PostMapping("/coupon/spubounds/save")
    public R save(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    public R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
