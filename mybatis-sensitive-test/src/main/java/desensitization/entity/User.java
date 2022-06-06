package desensitization.entity;


import desensitization.annotation.SensitiveField;
import desensitization.enums.SensitiveTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author <a>xub</a>
 * @since 2022-05-10
 */
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 姓名
     */
    @SensitiveField(SensitiveTypeEnums.CHINESE_NAME)
    private String name;


}
