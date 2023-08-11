package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IShopTypeService typeService;

    @Override
    public Result queryTypeList() {

        String shopList = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_TYPE);

        if(StrUtil.isNotBlank(shopList)){
            List<ShopType> shopTypes = JSONUtil.toList(shopList, ShopType.class);
            return Result.ok(shopTypes);
        }

        List<ShopType> typeList = typeService.query().orderByAsc("sort").list();

        if (typeList == null){
            return Result.fail("无商店类型");
        }

        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_TYPE, JSONUtil.toJsonStr(typeList));
        return Result.ok(typeList);
    }
}
