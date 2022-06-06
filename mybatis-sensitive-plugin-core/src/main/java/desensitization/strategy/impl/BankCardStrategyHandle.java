package desensitization.strategy.impl;


import desensitization.enums.SensitiveTypeEnums;
import desensitization.strategy.SensitiveStrategy;

import java.util.regex.Pattern;

/**
 * 银行卡脱敏: 6228477477865321转为622847******5321
 *
 * @author xub
 * @date 2022/6/2 上午9:16
 */
public class BankCardStrategyHandle implements SensitiveStrategy {

    /**
     * 银行卡卡号位数匹配
     */
    public final static String BANK_CARD_NUMBER = "^\\d{16}|\\d{19}$";

    @Override
    public SensitiveTypeEnums getType() {
        return SensitiveTypeEnums.BANK_CARD;
    }

    @Override
    public String handle(Object object, String fillValue) {
        if (object == null) {
            return null;
        }
        //字段原始值
        String value = object.toString();
        //如果银行卡不符合格式 直接返回 不进行脱敏
        if (!Pattern.matches(BANK_CARD_NUMBER, value)) {
            return value;
        }
        //填充银行卡卡号
        return this.centerFill(value, fillValue);
    }
}
