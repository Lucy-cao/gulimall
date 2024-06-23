package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 属性分组
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-18 17:02:39
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private AttrService attrService;
    @Autowired
    AttrAttrgroupRelationService relationService;

    /**
     * 根据分类id获取属性分组的列表信息
     */
    @RequestMapping("/list/{catelogId}")
    public R listByCatId(@RequestParam Map<String, Object> params,
                         @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrGroupService.queryByCatId(params, catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getCascaderById(attrGroupId);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 获取属性分组关联的属性信息
     */
    @GetMapping("/{attrGroupId}/attr/relation")
    public R getRelatedAttr(@PathVariable Long attrGroupId) {
        List<AttrEntity> attrEntityList = attrService.getRelatedAttr(attrGroupId);

        return R.ok().put("data", attrEntityList);
    }

    /**
     * 获取当前分类中，未与当前属性分组关联的属性
     */
    @GetMapping("/{attrGroupId}/noattr/relation")
    public R getNoRelatedAttr(@RequestParam Map<String, Object> params, @PathVariable Long attrGroupId) {
        PageUtils page = attrService.getNoRelatedAttr(params, attrGroupId);
        return R.ok().put("page", page);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 添加属性与分组的关联关系
     */
    @PostMapping("/attr/relation")
    public R saveRelations(@RequestBody List<AttrAttrgroupRelationEntity> relations) {
        relationService.saveBatch(relations);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * 删除属性分组和属性的关联关系
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody List<AttrGroupRelationVo> relationVos) {
        attrService.deleteRelation(relationVos);

        return R.ok();
    }

}
