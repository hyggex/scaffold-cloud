package com.scaffold.common.mybatis.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Collection;
import java.util.List;

/**
 * 基础 Service 实现类。
 *
 * <p>继承 MyBatis-Plus 的 ServiceImpl，提供标准 CRUD 和批量操作。
 * 所有业务 ServiceImpl 推荐继承此类。</p>
 *
 * <p>批量插入示例：
 * <pre>
 * &#64;Service
 * public class UserServiceImpl extends BaseServiceImpl&lt;UserMapper, UserEntity&gt; implements UserService {
 *     // 批量插入，返回插入后的对象列表（含自动生成的ID、时间戳等）
 *     public List&lt;UserEntity&gt; batchCreate(List&lt;UserEntity&gt; list) {
 *         return saveBatchAndReturn(list, 5000);
 *     }
 * }
 * </pre>
 *
 * @param <M> Mapper 类型
 * @param <T> 实体类型
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    /**
     * 批量插入并返回插入后的对象列表。
     *
     * <p>插入过程中 MyBatis-Plus 会自动填充 ID（雪花算法）和所有字段，
     * 返回后列表中每个对象均已包含完整的数据库记录数据。</p>
     *
     * @param list      数据列表
     * @param batchSize 每批大小（默认 5000）
     * @param <L>      列表类型（必须是 List 的子类型）
     * @return 插入后的对象列表（含自动生成的ID、时间戳等），与入参为同一引用
     */
    public <L extends List<T>> L saveBatchAndReturn(L list, int batchSize) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        // 调用父类（CrudRepository）已实现的批量插入逻辑
        saveBatch((Collection<T>) list, batchSize);
        return list;
    }
}
