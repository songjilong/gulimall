package com.sjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sjl.gulimall.product.entity.CategoryBrandRelationEntity;
import com.sjl.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;

import com.sjl.gulimall.product.dao.CategoryDao;
import com.sjl.gulimall.product.entity.CategoryEntity;
import com.sjl.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

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
     * 批量删除
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 删除前先判断是否有被关联的数据
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 找到指定分类id的完整三级分类路径 [父、子、孙]
     * e.g.[2, 25, 225]
     * @param catelogId 当前id
     * @return 完整路径
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> list = getCatelogPath(catelogId, new ArrayList<>());
        Collections.reverse(list);
        return list.toArray(new Long[0]);
    }

    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        //TODO 更新关联表的信息
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 递归查找当前分类id的父id
     * @param catelogId 当前id
     * @param list 存储结果的集合
     * @return 完整路径的倒序
     */
    private List<Long> getCatelogPath(Long catelogId, List<Long> list){
        list.add(catelogId);
        CategoryEntity ce = this.getById(catelogId);
        if(ce.getParentCid() != 0){
            getCatelogPath(ce.getParentCid(), list);
        }
        return list;
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