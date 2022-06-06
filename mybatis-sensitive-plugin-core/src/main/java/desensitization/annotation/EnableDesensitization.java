package desensitization.annotation;


import desensitization.plugin.DesensitizationInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 *  是否支持脱敏开关
 *
 * @author xub
 * @date 2022/6/1 下午2:13
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({DesensitizationInterceptor.class})
@Documented
public @interface EnableDesensitization {



}
