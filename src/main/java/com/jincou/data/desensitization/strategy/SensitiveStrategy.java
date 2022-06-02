package com.jincou.data.desensitization.strategy;


import com.jincou.data.desensitization.enums.SensitiveTypeEnums;
import org.apache.commons.lang3.StringUtils;

/**
 * 脱敏策略
 *
 * @author xub
 * @date 2021/12/2 上午10:22
 */
public interface SensitiveStrategy {


    /**
     * 具体脱敏类型
     */
    SensitiveTypeEnums getType();


    /**
     * 默认处理具体脱敏方法，如果特色子类 由子类实现
     *
     * @param object    具体需要脱敏字段
     * @param fillValue 填充值 默认*
     * @return 已经脱敏后的数据
     */
    String handle(Object object, String fillValue);





//===========================接口默认方法，把实现类公共部分抽离出来==========================================================

    /**
     * 中间填充的脱敏数据 比如手机号、银行卡、座机等等
     *
     * @param value     具体需要脱敏字段
     * @param fillValue 填充值 默认*
     * @return 已经脱敏后的数据
     */
    default String centerFill(String value, String fillValue) {

        SensitiveTypeEnums typeEnums = this.getType();
        int begin = typeEnums.getBegin();
        int end = typeEnums.getEnd();
        int length = StringUtils.length(value);

        //这里以手机为列子 说明下是如何做到中间填充的。其它的比如银行卡，身份证号等等都是一个道理
        //这里一共做了4步:
        //1、获取左边值:StringUtils.left(mobile, begin) 13312345678获取133
        //2、获取右边值:StringUtils.right(mobile, end)  13312345678获取5678
        //3、填充将5678左填充变为 ***5678
        //4、在合并1，3就变成 133133***5678
        return StringUtils.left(value, begin)
                .concat(StringUtils.leftPad(StringUtils.right(value, end), length - begin, fillValue));

    }

    /**
     * 右边填充的脱敏数据 比如身份证号、座机号、地址等等
     *
     * @param value     具体需要脱敏字段
     * @param fillValue 填充值 默认*
     * @return 已经脱敏后的数据
     */
    default String rightFill(String value, String fillValue) {

        SensitiveTypeEnums typeEnums = this.getType();
        int end = typeEnums.getEnd();
        int length = StringUtils.length(value);

        //这里以身份证为示例 说明下是如何做到中间填充的
        //这里一共做了2步:
        //1、获取左边值:StringUtils.left(value, end) 330127199911114444获取33012719991111
        //2、填充将33012719991111左填充变为 33012719991111****
        return StringUtils.rightPad(StringUtils.left(value, length - end), length, fillValue);
    }

}
