package com.sjl.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sjl.gulimall.product.entity.BrandEntity;
import com.sjl.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;


    @Test
    void save() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setLogo("iPhone");
        brandService.save(brandEntity);
    }

    @Test
    void update() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setLogo("HUAWEI");
        brandService.updateById(brandEntity);
    }

    @Test
    void query() {
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1));
        list.forEach(System.out::println);
    }
}
