package com.atguigu.common.validator.group;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        //初始化数据。获取用户指定的范围
        for (int i : constraintAnnotation.value()) {
            set.add(i);
        }

    }

    /**
     * @param integer                    需要校验的值
     * @param constraintValidatorContext 上下文环境
     * @return
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);//返回需要校验的值是否在指定的列表中
    }
}
