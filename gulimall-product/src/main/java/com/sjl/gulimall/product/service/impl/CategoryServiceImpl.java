package com.sjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;
import com.sjl.gulimall.product.dao.CategoryDao;
import com.sjl.gulimall.product.entity.CategoryEntity;
import com.sjl.gulimall.product.service.CategoryBrandRelationService;
import com.sjl.gulimall.product.service.CategoryService;
import com.sjl.gulimall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


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
    @Transactional(rollbackFor = Exception.class)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        //TODO 更新关联表的信息
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categories() {
        return this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
    }

    /**
     * 查出所有一级分类下的子分类数据
     * key：一级分类id   value：子分类数据
     */
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        //查询出所有分类信息，只查询一次数据库，就可以获取需要的数据
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);
        //获取所有的一级分类
        List<CategoryEntity> level1Categories = getLevel1Categories();
        //封装一级分类信息
        return level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查询一级分类下的所有二级分类信息
            List<CategoryEntity> level2Categories = getListByParentCid(selectList, v.getCatId());
            return level2Categories.stream().map(l2 -> {
                //查询二级分类下的所有三级分类信息
                List<CategoryEntity> level3Categories = getListByParentCid(selectList, l2.getCatId());
                //封装三级分类信息
                List<Catalog2Vo.Catalog3Vo> collect = level3Categories.stream()
                        .map(l3 -> new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName()))
                        .collect(Collectors.toList());
                //封装二级分类信息
                return new Catalog2Vo(v.getCatId().toString(), l2.getCatId().toString(), l2.getName(), collect);
            }).collect(Collectors.toList());
        }));
    }

    /**
     * 根据父id查询每个分类的数据
     */
    private List<CategoryEntity> getListByParentCid(List<CategoryEntity> selectList, Long parentCid) {
        return selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
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