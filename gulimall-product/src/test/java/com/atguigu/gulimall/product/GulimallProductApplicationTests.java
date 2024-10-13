package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Test
    void testRedis(){
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        //设置redis数据
        opsForValue.set("hello","world_"+ UUID.randomUUID());
        //读取redis数据
        String hello = opsForValue.get("hello");
        System.out.println("hello = " + hello);
    }

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }
    @Test
    void contextLoads() {
//        BrandEntity brandEntity=new BrandEntity();
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("遥遥领先");
//        brandService.updateById(brandEntity);

//        brandEntity.setName("华为");
//        brandService.save(brandEntity);

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach(System.out::println);
    }

}
