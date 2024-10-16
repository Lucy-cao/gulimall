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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
	@Autowired
	RedissonClient redisson;

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
	public Map<Long, List<CategoryLevel2Vo>> getCatByLevel() {
		//加入缓存机制
		// 如果redis里面存在分类数据，则直接获取；如果没有，从数据库获取，并保存入redis
		String catalogJson = redisTemplate.opsForValue().get("catalogJson");
		if (StringUtils.isEmpty(catalogJson)) {
			//redis不存在，从数据库获取
			System.out.println("缓存不命中。。。查询数据库");
			Map<Long, List<CategoryLevel2Vo>> catByLevelFromDb = getCatByLevelFromDbWithRedissonLock();
			return catByLevelFromDb;
		}
		//redis已存在，将字符串转化为相应对象返回
		System.out.println("缓存命中。。。直接返回。。。");
		Map<Long, List<CategoryLevel2Vo>> result = JSON.parseObject(catalogJson,
				new TypeReference<Map<Long, List<CategoryLevel2Vo>>>() {
				});
		return result;
	}

	public Map<Long, List<CategoryLevel2Vo>> getCatByLevelFromDbWithRedissonLock() {
		//使用redisson添加分布式锁
		//锁的颗粒度越细，程序执行越快。可以根据具体存放的数据去控制锁的粒度
		RLock lock = redisson.getLock("catalog-lock");
		Map<Long, List<CategoryLevel2Vo>> catByLevelFromDb;
		try {
			//加锁成功，查询数据
			catByLevelFromDb = getCatByLevelFromDb();
		} finally {
			//解锁
			lock.unlock();
		}
		return catByLevelFromDb;
	}

	public Map<Long, List<CategoryLevel2Vo>> getCatByLevelFromDbWithRedisLock() {
		//分布式锁：基于各个服务到redis占坑的过程，假设有一个lock的key在redis已存在，后面的服务就不能再占到这个锁。
		//redis的命令行：set key value nx  （nx是key不存在才能设置成功），对应代码是setIfAbsent
		//添加过期时间和占锁必须是同步的，原子的
		String uuid = UUID.randomUUID().toString();
		Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
		Map<Long, List<CategoryLevel2Vo>> catByLevelFromDb;
		if (lock) {
			try {
				//加锁成功，查询数据
				catByLevelFromDb = getCatByLevelFromDb();
			} finally {
				//查数据+对比删锁必须是原子操作，使用Lua脚本
				String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return 0 end";
				redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), uuid);
			}

			//查询到数据之后，释放锁
//			String lockValue = redisTemplate.opsForValue().get("lock");
//			if(uuid.equals(lockValue)){
//				//当前的锁的值是设置的自己的uuid才可以执行删除
//				redisTemplate.delete("lock");
//			}

			return catByLevelFromDb;
		} else {
			//加锁不成功，等待100ms后重试
			return getCatByLevelFromDbWithRedisLock();//使用自旋的方式
		}
	}

	public Map<Long, List<CategoryLevel2Vo>> getCatByLevelFromDb() {
		//再次校验redis中是否有数据，高并发情况下，可能前面线程已经获取过数据放到redis中
		String catalogJson = redisTemplate.opsForValue().get("catalogJson");
		if (!StringUtils.isEmpty(catalogJson)) {
			Map<Long, List<CategoryLevel2Vo>> result = JSON.parseObject(catalogJson,
					new TypeReference<Map<Long, List<CategoryLevel2Vo>>>() {
					});
			return result;
		}
		System.out.println("查询了数据库。。。");
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

		//将数据库获取到的数据保存到redis。需要放在加锁的方法内执行。
		//如果放在加锁的方法外执行，可能出现前一个请求还未将查询到的数据放入redis，后一个请求已经占到锁，但是发现redis里面还是没数据，会再次查询数据库
		redisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(catLevels), 1, TimeUnit.DAYS);
		return catLevels;
	}

	public Map<Long, List<CategoryLevel2Vo>> getCatByLevelFromDbWithLocalLock() {
		//在此方法内通过同步块加锁
		synchronized (this) {
			Map<Long, List<CategoryLevel2Vo>> catByLevelFromDb = getCatByLevelFromDb();
			return catByLevelFromDb;
		}
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
