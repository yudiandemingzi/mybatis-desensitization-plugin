package desensitization.strategy.impl;

import desensitization.enums.SensitiveTypeEnums;
import desensitization.strategy.SensitiveStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * 默认脱敏方式
 *
 * @author xub
 * @date 2022/6/2 上午9:16
 */
public class DefaultStrategyHandle implements SensitiveStrategy {


    @Override
    public SensitiveTypeEnums getType() {
        return SensitiveTypeEnums.DEFAULT;
    }

    @Override
    public String handle(Object object, String fillValue) {
        if (object == null) {
            return null;
        }
        //字段原始值
        String value = object.toString();
        SensitiveTypeEnums type = getType();
        int end = type.getEnd();
        int length = StringUtils.length(value);
        if (end < length) {
            return this.rightFill(value, fillValue);
        }
        return value;
    }
}
