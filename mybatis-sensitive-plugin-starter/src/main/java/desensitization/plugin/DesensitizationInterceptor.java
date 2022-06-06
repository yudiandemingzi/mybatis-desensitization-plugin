package desensitization.plugin;

import desensitization.annotation.SensitiveField;
import desensitization.enums.SensitiveTypeEnums;
import desensitization.strategy.SensitiveContext;
import desensitization.strategy.SensitiveStrategy;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 基于拦截器对数据脱敏
 *
 * @author xub
 * @date 2022/6/2 下午2:23
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {java.sql.Statement.class})
})
public class DesensitizationInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(DesensitizationInterceptor.class);

    /**
     * key值为class对象 value可以理解成是该类带有SensitiveField注解的属性，只不过对属性封装了一层。
     * 它是非常能够提高性能的处理器 它的作用就是不用每一次一个对象经来都要看下它的哪些属性带有SensitiveField注解
     * 毕竟类的反射在性能上并不友好。只要key包含该对象那就不需要检查它哪些属性带SensitiveField注解。
     */
    private Map<Class, List<Handler>> handlerMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取结果
        List<Object> results = (List<Object>) invocation.proceed();
        if (CollectionUtils.isEmpty(results)) {
            return results;
        }
        // 批量设置加密
        for (Object object : results) {
            process(object);
        }
        return results;
    }


    private void process(Object object) throws Throwable {
        Class handlerKey = object.getClass();
        List<Handler> handlerList = handlerMap.get(handlerKey);

        //性能优化点，如果有两个都是user对象同时,那么只需有个进行反射处理属性就好了,另一个只需执行下面的for循环
        SYNC:
        if (handlerList == null) {
            synchronized (this) {
                handlerList = handlerMap.get(handlerKey);
                //如果到这里map集合已经存在，则跳出到指定SYNC标签
                if (handlerList != null) {
                    break SYNC;
                }
                handlerMap.put(handlerKey, handlerList = new ArrayList<>());
                // 反射工具类 获取带有SensitiveField注解的所有属性字段
                Set<Field> allFields = ReflectionUtils.getAllFields(
                        object.getClass(),
                        input -> input != null && input.getAnnotation(SensitiveField.class) != null
                );

                for (Field field : allFields) {
                    handlerList.add(new Handler(field));
                }
            }
        }
        for (Handler handler : handlerList) {
            handler.accept(object);
        }
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }


    private static class Handler {
        Field field;

        Handler(Field field) {
            this.field = field;
        }

        private boolean checkField(Object object, Field field) throws IllegalAccessException {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            //如果为空 那么就不用进行脱敏操作了
            return field.get(object) != null;
        }

        public void accept(Object o) throws Throwable {
            if (checkField(o, field)) {
                SensitiveField annotation = field.getAnnotation(SensitiveField.class);
                SensitiveTypeEnums typeEnums = annotation.value();
                String fillValue = annotation.fillValue();
                Object o1 = field.get(o);
                log.info("加密之前数据 = {}",o1);
                SensitiveStrategy sensitiveStrategy = SensitiveContext.get(typeEnums);
                String s = sensitiveStrategy.handle(o1, fillValue);
                log.info("加密之后数据 = {}",s);
                field.set(o, s);
            }
        }
    }
}
