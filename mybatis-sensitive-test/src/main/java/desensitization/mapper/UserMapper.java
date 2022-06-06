package desensitization.mapper;

import desensitization.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author <a>xub</a>
 * @since 2022-05-10
 */
@Mapper
public interface UserMapper{


    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 实体对象
     */
    User selectByPrimaryKey(Integer id);


}
