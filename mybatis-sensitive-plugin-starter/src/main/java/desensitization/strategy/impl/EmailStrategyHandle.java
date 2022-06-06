package desensitization.strategy.impl;


import desensitization.enums.SensitiveTypeEnums;
import desensitization.strategy.SensitiveStrategy;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;


/**
 * 邮箱脱敏 邮箱脱敏比较特殊。一般我们说455555@qq.com 那我们会在@之前几位进行脱敏 45****@qq.com
 *
 * @author xub
 * @date 2022/6/2 上午10:31
 */
public class EmailStrategyHandle implements SensitiveStrategy {


    /**
     * 邮箱email
     */
    public static final String EMAIL_REG = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    @Override
    public SensitiveTypeEnums getType() {
        return SensitiveTypeEnums.EMAIL;
    }

    @Override
    public String handle(Object object, String fillValue) {
        if (object == null) {
            return null;
        }
        //字段原始值
        String value = object.toString();
        //如果不符合格式 直接返回 不进行脱敏
        if (!Pattern.matches(EMAIL_REG, value)) {
            return value;
        }
        //以 455555@qq.com 示例
        int length = StringUtils.length(value);
        //获取@位置
        int indexOf = StringUtils.indexOf(value, "@");
        //获取455555 部分
        String left = StringUtils.left(value, indexOf);
        //获取@qq.com部分 这部分数据是不用处理的 后面在拼接回来就好了
        String right = StringUtils.right(value, length - indexOf);

        int leftLength = StringUtils.length(left);
        //45@qq.com 直接返回 **@qq.com
        if (leftLength <= 2) {
            return StringUtils.leftPad(right, length, fillValue);
        }
        //如果leftLength大于2
        String leftFill = this.centerFill(left, fillValue);
        //做好拼接
        return leftFill.concat(right);
    }

}
