package com.sjl.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.sjl.common.to.SkuHasStockTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sjl.gulimall.ware.entity.WareSkuEntity;
import com.sjl.gulimall.ware.service.WareSkuService;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.R;



/**
 * 商品库存
 *
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-02 01:11:03
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 查询Sku是否有库存
     */
    @PostMapping("/hasstock")
    public R<List<SkuHasStockTo>> getSkuHasStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockTo> data = wareSkuService.getSkuHasStock(skuIds);
        R<List<SkuHasStockTo>> r = new R<>();
        r.setData(data);
        return r;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
