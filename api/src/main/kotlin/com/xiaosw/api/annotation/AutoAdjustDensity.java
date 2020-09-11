package com.xiaosw.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: {@link AutoAdjustDensity}
 * @Description:
 *
 * Created by admin at 2020-08-28
 * @Email xiaosw0802@163.com
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoAdjustDensity {

    float baseDp() default 320f;

    boolean baseDpByWidth() default true;

}
