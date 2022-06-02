package com.jincou.data.desensitization.annotation;


import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.SchedulingConfiguration;

import java.lang.annotation.*;

/**
 *  是否支持脱敏开关
 *
 * @author xub
 * @date 2022/6/1 下午2:13
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({SchedulingConfiguration.class})
@Documented
public @interface EnableDesensitization {



}
