package com.sjl.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;

import com.sjl.gulimall.product.dao.CategoryDao;
import com.sjl.gulimall.product.entity.CategoryEntity;
import com.sjl.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查出所有分类及其子分类，以树形结构组装起来
     * @return 查询结果
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查询出所有的分类
        List<CategoryEntity> all = baseMapper.selectList(null);
        //2、查询每个分类的子分类，并排序
        return all.stream()
                .filter(c -> c.getParentCid() == 0)
                .peek(c -> c.setChildren(getChildren(c, all)))
                .sorted(Comparator.comparingInt(c -> (c.getSort() == null ? 0 : c.getSort())))
                .collect(Collectors.toList());
    }

    /**
     * 递归查询出所有菜单的子分类
     * @param root 父分类
     * @param all 所有的数据
     * @return 查询结果
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all){
        return all.stream()
                .filter(c -> c.getParentCid().equals(root.getCatId()))
                .peek(c -> c.setChildren(getChildren(c, all)))
                .sorted(Comparator.comparingInt(c -> (c.getSort() == null ? 0 : c.getSort())))
                .collect(Collectors.toList());
    }

}