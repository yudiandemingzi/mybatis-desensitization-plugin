package com.jincou.data.desensitization.strategy.impl;

import com.jincou.data.desensitization.enums.SensitiveTypeEnums;
import com.jincou.data.desensitization.strategy.SensitiveStrategy;

import java.util.regex.Pattern;

/**
 * 身份证号脱敏 330127199911114444转为330127199911114444****
 *
 * @author xub
 * @date 2022/6/2 上午9:16
 */
public class IdCardStrategyHandle implements SensitiveStrategy {

    /**
     * 身份证号码位数限制
     */
    public final static String ID_CARD = "^\\d{15}|(\\d{17}[0-9,x,X])$";

    @Override
    public SensitiveTypeEnums getType() {
        return SensitiveTypeEnums.ID_CARD;
    }

    @Override
    public String handle(Object object, String fillValue) {
        if (object == null) {
            return null;
        }
        //字段原始值
        String value = object.toString();
        //如果身份证号不符合格式 直接返回 不进行脱敏
        if (!Pattern.matches(ID_CARD, value)) {
            return value;
        }
        //身份证号脱敏
        return this.rightFill(value, fillValue);
    }
}
