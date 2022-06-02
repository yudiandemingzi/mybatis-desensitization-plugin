package com.jincou.data.desensitization.strategy.impl;

import com.jincou.data.desensitization.strategy.SensitiveStrategy;
import com.jincou.data.desensitization.enums.SensitiveTypeEnums;

import java.util.regex.Pattern;

/**
 * 手机号码脱敏 13312345678转为133****5678
 *
 * @author xub
 * @date 2022/6/2 上午9:16
 */
public class MobileStrategyHandle implements SensitiveStrategy {


    /**
     * 手机号码匹配
     */
    public static final String PHONE_REG = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";

    @Override
    public SensitiveTypeEnums getType() {
        return SensitiveTypeEnums.MOBILE;
    }

    @Override
    public String handle(Object object, String fillValue) {
        if (object == null) {
            return null;
        }
        //字段原始值
        String mobile = object.toString();
        //如果手机号不符合手机格式 直接返回 不进行脱敏
        if (!Pattern.matches(PHONE_REG, mobile)) {
            return mobile;
        }

        //填充手机号
        return this.centerFill(mobile, fillValue);
    }

}
