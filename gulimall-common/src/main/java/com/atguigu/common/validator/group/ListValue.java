package com.atguigu.common.validator.group;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint( //注解使用的校验器
        validatedBy = {ListValueConstraintValidator.class}
)
//指定注解可以使用在哪些对象上
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME) //注解的时机：运行时
public @interface ListValue {
    String message() default "{com.atguigu.common.validator.group.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] value() default {};//用来设置可传入的值列表
}
