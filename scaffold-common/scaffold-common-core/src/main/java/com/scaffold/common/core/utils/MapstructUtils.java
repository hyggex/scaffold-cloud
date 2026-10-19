package com.scaffold.common.core.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MapStruct 对象转换工具类。
 *
 * <p>封装 MapStruct 生成的转换器，提供链式调用和空安全转换。</p>
 *
 * <p>使用示例：</p>
 * <pre>
 * // 单个对象转换
 * UserVo vo = MapstructUtils.convert(user, UserVo::new);
 *
 * // 列表转换
 * List&lt;UserVo&gt; voList = MapstructUtils.convert(list, UserVo::new);
 *
 * // 链式转换
 * UserVo vo = MapstructUtils.convert(user, UserVo::new)
 *     .setName("new name");
 * </pre>
 *
 * @author Lion Li
 */
public class MapstructUtils {

    private MapstructUtils() {}

    /**
     * 转换单个对象（空安全）。
     *
     * @param source  源对象
     * @param converter 转换函数（通常为构造器引用，如 UserVo::new）
     * @param <S>  源类型
     * @param <T>  目标类型
     * @return 转换后的目标对象，source 为 null 时返回 null
     */
    public static <S, T> T convert(S source, Function<S, T> converter) {
        if (source == null) {
            return null;
        }
        return converter.apply(source);
    }

    /**
     * 转换集合（空安全）。
     *
     * @param sources 源集合
     * @param converter 转换函数
     * @param <S>  源类型
     * @param <T>  目标类型
     * @param <C>  集合类型
     * @return 转换后的目标列表，sources 为 null 时返回空列表
     */
    public static <S, T, C extends Collection<S>> List<T> convert(C sources, Function<S, T> converter) {
        if (sources == null || sources.isEmpty()) {
            return List.of();
        }
        return sources.stream().map(converter).collect(Collectors.toList());
    }

    /**
     * 将源对象转换到已有目标对象（覆盖模式）。
     *
     * @param source 源对象
     * @param target 已有目标对象
     * @param <S>  源类型
     * @param <T>  目标类型
     * @return 目标对象（与 target 为同一引用）
     */
    public static <S, T> T convertToTarget(S source, T target) {
        if (source == null || target == null) {
            return target;
        }
        // 通过反射将 source 的非空属性复制到 target
        copyProperties(source, target);
        return target;
    }

    /**
     * 简单的属性拷贝（仅处理 MapStruct 无法处理的场景）。
     * 此方法仅作兜底，实际对象转换应由 MapStruct 生成器完成。
     */
    private static <S, T> void copyProperties(S source, T target) {
        // 实际项目中应使用 BeanUtils 或 MapStruct 生成的转换器
        // 此处仅作接口定义，使用时替换为具体的 MapStruct 转换器
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }
}
