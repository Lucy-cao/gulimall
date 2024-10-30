package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.constant.AttrTypeEnum;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 商品属性
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-18 17:02:40
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;

    /**
     * 根据分类id查询规格参数
     */
    @GetMapping("/base/list/{catId}")
    //@RequiresPermissions("product:attr:list")
    public R baseList(@RequestParam Map<String, Object> params,
                      @PathVariable Long catId) {
        PageUtils page = attrService.queryPageByCatId(params, catId, AttrTypeEnum.BASE_ATTR.getCode());

        return R.ok().put("page", page);
    }

    /**
     * 根据分类id查询销售属性
     */
    @GetMapping("/sale/list/{catId}")
    //@RequiresPermissions("product:attr:list")
    public R saleList(@RequestParam Map<String, Object> params,
                      @PathVariable Long catId) {
        PageUtils page = attrService.queryPageByCatId(params, catId, AttrTypeEnum.SALE_ATTR.getCode());

        return R.ok().put("page", page);
    }

    @GetMapping("/base/listforspu/{spuId}")
    public R getAttrListForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> data = attrService.getAttrListForSpu(spuId);
        return R.ok().put("data", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrRespVo respVo = attrService.getDetailById(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo) {
        attrService.saveDetail(attrVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateDetailById(attr);

        return R.ok();
    }

    /**
     * 修改spu的规格参数
     */
    @RequestMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,
                           @RequestBody List<ProductAttrValueEntity> entities) {
        productAttrValueService.updateSpuAttr(spuId, entities);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

    /**
     * 根据属性id获取属性信息
     */
    @GetMapping("/getAttrById/{attrId}")
    public R getAttrById(@PathVariable("attrId") Long attrId) {
        AttrEntity respVo = attrService.getById(attrId);
        return R.ok().put("data", respVo);
    }

}
