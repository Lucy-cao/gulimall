package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.vo.CategoryCascaderVo;
import com.atguigu.gulimall.product.vo.CategoryLevel2Vo;
import com.atguigu.gulimall.product.vo.CategoryLevel3Vo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
	@Autowired
	CategoryBrandRelationDao categoryBrandRelationDao;
	@Autowired
	StringRedisTemplate redisTemplate;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryEntity> page = this.page(
				new Query<CategoryEntity>().getPage(params),
				new QueryWrapper<CategoryEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<CategoryEntity> listWithTree() {
		//1、获取所有的商品类型
		List<CategoryEntity> entities = this.list();
		//2、将商品种类按照树形结构组装
		//1)、获取根节点
		List<CategoryEntity> rootMenu = entities.stream().filter(item -> {
					return item.getParentCid() == 0;
				}).map(root -> {
					//2)、使用递归获取子节点
					root.setChildren(getChildren(root, entities));
					return root;
				})
				//进行排序
				.sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort()))
				.collect(Collectors.toList());
		return rootMenu;
	}

	@Override
	public void removeMenusById(List<Long> asList) {
		//TODO: 删除前需要校验菜单是否被引用
		baseMapper.deleteBatchIds(asList);
	}

	@Override
	public CategoryCascaderVo getCascaderById(Long catId) {
		//获取级联分类
		List<Long> catelogIds = new ArrayList<>();
		List<String> catelogNames = new ArrayList<>();
		Long currentCatlogId = catId;
		while (!currentCatlogId.equals(0L)) {
			//将当前id放进去
			catelogIds.add(0, currentCatlogId);
			CategoryEntity category = this.getById(currentCatlogId);
			catelogNames.add(0, category.getName());
			//将父级id赋值给当前id
			currentCatlogId = category.getParentCid();
		}
		//返回数据
		CategoryCascaderVo cascaderVo = new CategoryCascaderVo();
		cascaderVo.setCascaderId(catelogIds);
		cascaderVo.setCascaderNames(String.join("/", catelogNames));
		return cascaderVo;
	}

	@Override
	@Transactional
	public void updateDetail(CategoryEntity category) {
		//更新详情，同时更新关联的冗余表
		this.updateById(category);
		categoryBrandRelationDao.updateCategory(category.getCatId(), category.getName());
	}

	@Override
	public List<CategoryEntity> getAllFirstLevelCat() {
		List<CategoryEntity> list = this.list(Wrappers.lambdaQuery(CategoryEntity.class)
				.eq(CategoryEntity::getParentCid, 0)
				.eq(CategoryEntity::getShowStatus, 1));
		return list;
	}

	@Override
	public Map<Long, List<CategoryLevel2Vo>> getCatByLevel(){
		//加入缓存机制
		// 如果redis里面存在分类数据，则直接获取；如果没有，从数据库获取，并保存入redis
		String catalogJson = redisTemplate.opsForValue().get("catalogJson");
		if(StringUtils.isEmpty(catalogJson)){
			//redis不存在，从数据库获取
			Map<Long, List<CategoryLevel2Vo>> catByLevelFromDb = getCatByLevelFromDb();
			//将数据库获取到的数据保存到redis
			redisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(catByLevelFromDb));
			return catByLevelFromDb;
		}
		//redis已存在，将字符串转化为相应对象返回
		Map<Long, List<CategoryLevel2Vo>> result = JSON.parseObject(catalogJson,
				new TypeReference<Map<Long, List<CategoryLevel2Vo>>>() {});
		return result;
	}

	public Map<Long, List<CategoryLevel2Vo>> getCatByLevelFromDb() {
		Map<Long, List<CategoryLevel2Vo>> catLevels = new HashMap<>();
		//获取所有的分类
		List<CategoryEntity> all = this.list(Wrappers.lambdaQuery(CategoryEntity.class)
				.eq(CategoryEntity::getShowStatus, 1));

		//获取所有一级分类
		List<CategoryEntity> level1List = all.stream().filter(cat -> cat.getParentCid() == 0).collect(Collectors.toList());
		level1List.forEach(level1 -> {
			Long level1CatId = level1.getCatId();
			//获取二级分类
			List<CategoryEntity> level2List = all.stream().filter(cat -> Objects.equals(cat.getParentCid(), level1CatId))
					.collect(Collectors.toList());

			List<CategoryLevel2Vo> level2Vos = level2List.stream().map(level2 -> {
				CategoryLevel2Vo categoryLevel2Vo = new CategoryLevel2Vo();
				categoryLevel2Vo.setId(level2.getCatId());
				categoryLevel2Vo.setName(level2.getName());
				categoryLevel2Vo.setCatalog1Id(level1CatId);
				//获取三级分类
				List<CategoryEntity> level3List = all.stream().filter(cat -> Objects.equals(cat.getParentCid(), level2.getCatId()))
						.collect(Collectors.toList());
				List<CategoryLevel3Vo> level3Vos = level3List.stream().map(level3 -> {
					CategoryLevel3Vo level3Vo = new CategoryLevel3Vo();
					level3Vo.setId(level3.getCatId());
					level3Vo.setName(level3.getName());
					level3Vo.setCatalog2Id(level2.getCatId());
					return level3Vo;
				}).collect(Collectors.toList());
				categoryLevel2Vo.setCatalog3List(level3Vos);

				return categoryLevel2Vo;
			}).collect(Collectors.toList());
			catLevels.put(level1CatId, level2Vos);
		});
		return catLevels;
	}

	private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
		//获取当前菜单的子菜单
		List<CategoryEntity> childrenMenus = all.stream().filter(item -> Objects.equals(item.getParentCid(), root.getCatId()))
				.map(menu -> {
					//子菜单递归获取各自的子菜单
					menu.setChildren(getChildren(menu, all));
					return menu;
				}).sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort()))
				.collect(Collectors.toList());

		return childrenMenus;
	}

}
