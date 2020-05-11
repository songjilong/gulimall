package com.sjl.gulimall.product.feign;

import com.sjl.common.to.es.SkuEsModel;
import com.sjl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author songjilong
 * @date 2020/5/11 18:22
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {
    @GetMapping("/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
