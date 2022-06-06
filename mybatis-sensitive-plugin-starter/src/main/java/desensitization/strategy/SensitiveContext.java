package desensitization.strategy;

import desensitization.enums.SensitiveTypeEnums;
import desensitization.strategy.impl.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  获取所有策略
 *
 * @author xub
 * @date 2022/6/2 下午2:13
 */
@Component
public class SensitiveContext {

    private static final Map<SensitiveTypeEnums,SensitiveStrategy> map = new ConcurrentHashMap<>();

    static {
        map.put(SensitiveTypeEnums.DEFAULT,new DefaultStrategyHandle());
        map.put(SensitiveTypeEnums.CHINESE_NAME,new NameStrategyHandle());
        map.put(SensitiveTypeEnums.MOBILE,new MobileStrategyHandle());
        map.put(SensitiveTypeEnums.FIXED_PHONE,new FixedPhoneStrategyHandle());
        map.put(SensitiveTypeEnums.BANK_CARD,new BankCardStrategyHandle());
        map.put(SensitiveTypeEnums.ID_CARD,new IdCardStrategyHandle());
        map.put(SensitiveTypeEnums.EMAIL,new EmailStrategyHandle());
        map.put(SensitiveTypeEnums.ADDRESS,new AddressStrategyHandle());
    }


    public static SensitiveStrategy get(SensitiveTypeEnums sensitiveType){

        SensitiveStrategy eensitiveStrategy =  map.get(sensitiveType);
        Assert.notNull(eensitiveStrategy,"eensitiveStrategy no found!");
        return eensitiveStrategy;
    }


}
