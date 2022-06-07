
#### 1、项目背景

有时候我们数据库中存储一些敏感的信息比如**手机号**、**银行卡号**，我们希望我们查询出来的的时候对一些敏感信息做一些脱敏处理。

当面项目是基于自定义Mydatis插件方式实现数据脱敏处理，通过插件拦截结果集进行脱敏后再返回，所以对于使用者透明，业务逻辑无感知。

目前支持用户名脱敏、手机号脱敏、座机号码脱敏、银行卡脱敏、身份证号脱敏、邮箱脱敏、地址脱敏。


#### 2、注解说明

```java
/**
 * 对需要脱敏的字段加上该注解
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
```

**目前支持脱敏类型**

```java
/**
 * 脱敏类型
 */
public enum SensitiveTypeEnums {

    /**
     * 默认方式脱敏
     */
    DEFAULT(0,6),

    /**
     * 中文名称
     */
    CHINESE_NAME(1,1),

    /**
     * 手机号
     */
    MOBILE(3,4),

    /**
     * 座机号码
     */
    FIXED_PHONE(0,4),

    /**
     * 银行卡
     */
    BANK_CARD(6,4),

    /**
     * 身份证号
     */
    ID_CARD(0,4),

    /**
     * 邮箱
     */
    EMAIL(2,0),

    /**
     * 地址
     */
    ADDRESS(6,4),

    ;
}
```


`举例`: 如11位的手机号,默认脱敏策略是显示(3,4)前三后四，如 13712345678 脱敏后变成 137****5678。



## 实现原理

该插件项目的原理是

```
通过拦截器拦截Mybatis的select语句,通过自定义注解获取到需要脱敏处理的属性，并为该属性根据不同的脱敏类型进行脱敏,处理后再返回。
```

插件源码


```java
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
```

<br>

## 三、使用方式

#### 1、添加jar包

```xml
 <dependency>
    <groupId>com.jincou</groupId>
    <artifactId>mybatis-sensitive-plugin-starter</artifactId>
    <version>1.0.0</version>
 </dependency>
```



#### 2、User实体添加注解

```java
/**
 * 用户信息表
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 姓名
     */
    @SensitiveField(SensitiveTypeEnums.CHINESE_NAME)
    private String name;

    /**
     * 邮箱
     */
    @SensitiveField(SensitiveTypeEnums.EMAIL)
    private String email;

    /**
     * 手机号
     */
    @SensitiveField(SensitiveTypeEnums.MOBILE)
    private String mobile;

    /**
     * 地址
     */
    @SensitiveField(SensitiveTypeEnums.ADDRESS)
    private String address;
}
```

#### 3、请求接口

```java
@RestController
public class TestController {

    @Autowired
   private UserMapper userMapper;

    @GetMapping("/getById")
    User get(Integer id){
     return  userMapper.selectByPrimaryKey(id);
    }


    @GetMapping("/findAllUser")
    List<User> findAllUser(){
     return  userMapper.findAllUser();
    }
}
```

请求接口url

```
http://localhost:8080/findAllUser
```

#### 4、接口返回数据

```json
[
    {
        "id":1,
        "age":10,
        "name":"z*****u",
        "email":"45*******@qq.com",
        "mobile":"137****2222",
        "address":"宁波市慈溪市***********鸣鹤古镇"
    },
    {
        "id":2,
        "age":20,
        "name":"l**i",
        "email":"xu****@outlook.com",
        "mobile":"139****5678",
        "address":"西安市未央区************100米"
    },
    {
        "id":8,
        "age":30,
        "name":"w****a",
        "email":"wa****@163.com",
        "mobile":"137****5678",
        "address":"西安市未央区************100米"
    }
]
```


#### 5、表中原始数据

```java
1	10	zhaoliu	450760999@qq.com	13722222222	宁波市慈溪市观海卫镇禹皇路999号鸣鹤古镇
2	20	lisi	xu5555@outlook.com	13912345678	西安市未央区凤城二路与连心路交叉口南100米
8	30	wangba	wangba@163.com	13712345678	西安市未央区凤城二路与连心路交叉口南100米
```






