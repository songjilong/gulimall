package com.sjl.gulimall.product.web;

import com.sjl.gulimall.product.entity.CategoryEntity;
import com.sjl.gulimall.product.service.CategoryService;
import com.sjl.gulimall.product.vo.Catalog2Vo;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author songjilong
 * @date 2020/5/12 11:59
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redisson;

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        //查询一级分类
        List<CategoryEntity> level1Categories = categoryService.getLevel1Categories();
        model.addAttribute("categories1", level1Categories);
        return "index";
    }

    @GetMapping("/index/json/catalog.json")
    @ResponseBody
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        //获取Lock锁，设置锁的名称
        RLock lock = redisson.getLock("my-lock");
        //开启
        lock.lock(25, TimeUnit.SECONDS);
        try {
            System.out.println("上锁：" + Thread.currentThread().getId());
            //模拟业务处理20秒的时间
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            System.out.println("解锁：" + Thread.currentThread().getId());
            //关闭
            lock.unlock();
        }
        return "hello";
    }
}
