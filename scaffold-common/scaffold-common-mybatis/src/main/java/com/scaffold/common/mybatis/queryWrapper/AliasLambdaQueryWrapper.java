package com.scaffold.common.mybatis.queryWrapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * 支持表别名的 LambdaQueryWrapper。
 *
 * <p>在使用多表联查时，通过设置表别名，使所有 Lambda 字段引用自动加上"别名."前缀，
 * 避免手动拼写 SQL 别名导致的错误。</p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 单表查询（无需别名，与普通 Wrapper 完全一致）
 * AliasLambdaQueryWrapper<User> qw = AliasLambdaQueryWrapper.of();
 * qw.eq(User::getStatus, "0").like(User::getName, "张");
 *
 * // 多表联查（设置别名，所有 Lambda 条件自动拼接别名）
 * AliasLambdaQueryWrapper<User> qw = AliasLambdaQueryWrapper.of("u");
 * qw.eq(User::getStatus, "0")              // -> u.status = '0'
 *   .like(User::getName, "张")              // -> u.name LIKE '%张%'
 *   .exists("SELECT 1 FROM sys_role r WHERE r.role_id = u.role_id");
 *
 * // 获取带别名的列名（用于原生 SQL）
 * String nameCol = qw.getAliasedColumn(User::getName); // "u.name"
 * qw.apply("{} IS NOT NULL", nameCol);
 *
 * // 动态切换别名
 * qw.alias("t1").eq(User::getStatus, "0");
 * }</pre>
 *
 * <p>核心原理：重写 {@link #columnToString(SFunction)} 方法，
 * MyBatis-Plus 在处理所有 Lambda 列引用时都会调用此方法，
 * 因此只需设置一次 alias，所有条件自动生效。</p>
 *
 * @author 8800
 * @param <T> 实体类型
 */
public class AliasLambdaQueryWrapper<T> extends LambdaQueryWrapper<T> {

    /** 表别名，为 null 或空串时不添加别名前缀 */
    private String alias;

    // ==================== 工厂方法 ====================

    /** 创建无别名的查询包装器 */
    public static <T> AliasLambdaQueryWrapper<T> of() {
        return new AliasLambdaQueryWrapper<>();
    }

    /** 创建带别名的查询包装器（推荐） */
    public static <T> AliasLambdaQueryWrapper<T> of(String alias) {
        return new AliasLambdaQueryWrapper<>(alias);
    }

    // ==================== 构造器 ====================

    public AliasLambdaQueryWrapper() {
        super();
    }

    public AliasLambdaQueryWrapper(String alias) {
        super();
        this.alias = alias;
    }

    // ==================== 别名设置 ====================

    /**
     * 设置表别名（链式调用）。
     *
     * @param alias 表别名（如 "u"、"t1"）
     * @return 当前实例，支持链式调用
     */
    public AliasLambdaQueryWrapper<T> alias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    // ==================== 核心方法 ====================

    /**
     * Lambda 字段转 SQL 列名时自动增加表别名。
     *
     * <p>MyBatis-Plus 在处理所有 Lambda 条件（eq、like、in 等）时，
     * 都会调用此方法将 Lambda 引用转换为实际列名字符串。
     * 因此只需调用一次 {@link #alias(String)}，所有后续条件自动生效。</p>
     *
     * @param column Lambda 字段引用
     * @return 带别名的列名（如 "u.name"），未设置别名时返回原始列名
     */
    @Override
    protected String columnToString(SFunction<T, ?> column) {
        String columnName = super.columnToString(column);
        if (alias == null || alias.isBlank()) {
            return columnName;
        }
        return alias + "." + columnName;
    }

    /**
     * 获取带别名的列名字符串（用于原生 SQL 条件）。
     *
     * <p>当需要在 apply()、last() 等原生 SQL 方法中使用带别名的列名时调用。</p>
     *
     * @param column Lambda 字段引用
     * @return 带别名的列名（如 "u.name"），未设置别名时返回原始列名
     */
    public String getAliasedColumn(SFunction<T, ?> column) {
        return columnToString(column);
    }

    /**
     * 获取带别名的列名（直接传入列名字符串）。
     *
     * @param columnName 列名（如 "name"）
     * @return 带别名的列名（如 "u.name"），未设置别名时返回原始列名
     */
    public String getAliasedColumn(String columnName) {
        if (alias == null || alias.isBlank()) {
            return columnName;
        }
        return alias + "." + columnName;
    }
}
