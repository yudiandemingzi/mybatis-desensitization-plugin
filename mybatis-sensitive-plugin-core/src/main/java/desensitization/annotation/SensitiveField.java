package desensitization.annotation;

import desensitization.enums.SensitiveTypeEnums;

import java.lang.annotation.*;


/**
 * 对需要脱敏的字段加上该注解
 *
 * @author xub
 * @date 2022/6/1 下午2:08
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface SensitiveField {

    /**
     * 脱敏类型
     */
    SensitiveTypeEnums value();

    /**
     * 填充值
     */
    String fillValue() default "*";

}
