package com.scaffold.api.system.service;

import com.scaffold.api.system.domain.vo.DubboDictDataVo;
import com.scaffold.api.system.domain.vo.DubboDictTypeVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 字典服务接口（Dubbo RPC）。
 *
 * <p>提供给其他微服务调用的字典查询接口。</p>
 *
 *  @author 8800
 */
public interface DubboDictService {

    // ==================== 基础查询 ====================

    /**
     * 根据字典类型查询字典数据列表。
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<DubboDictDataVo> getDictDataByType(String dictType);

    /**
     * 根据字典类型和字典键值查询字典标签。
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    String getDictLabel(String dictType, String dictValue);

    /**
     * 根据字典类型和字典标签查询字典数据。
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @return 字典数据
     */
    DubboDictDataVo getDictDataByLabel(String dictType, String dictLabel);

    /**
     * 根据字典类型和字典键值查询字典数据。
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典数据
     */
    DubboDictDataVo getDictDataByValue(String dictType, String dictValue);

    /**
     * 根据字典类型查询字典类型信息。
     *
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    DubboDictTypeVo getDictTypeByType(String dictType);

    // ==================== 批量查询 ====================

    /**
     * 获取所有字典类型。
     *
     * @return 字典类型列表
     */
    List<DubboDictTypeVo> getAllDictTypes();

    /**
     * 获取所有字典数据。
     *
     * @return 字典数据列表
     */
    List<DubboDictDataVo> getAllDictData();

    /**
     * 根据字典类型集合批量查询字典数据。
     *
     * @param dictTypes 字典类型集合
     * @return 字典数据列表
     */
    List<DubboDictDataVo> getDictDataByTypes(Set<String> dictTypes);

    /**
     * 根据字典键值集合批量查询字典数据。
     *
     * @param dictValues 字典键值集合
     * @return 字典数据列表
     */
    List<DubboDictDataVo> getDictDataByValues(Set<String> dictValues);

    // ==================== 高级查询 ====================

    /**
     * 根据字典类型查询字典数据，封装成 Map 返回。
     * 用于前端一次性加载多个字典类型数据。
     *
     * @param dictTypes 字典类型列表
     * @return 字典数据 Map，外层 Key=dictType，内层 Key=dictValue，Value=dictLabel
     */
    Map<String, Map<String, String>> getDictTypeData(List<String> dictTypes);

    /**
     * 获取所有字典数据（按类型分组）。
     *
     * @return 字典数据 Map，key 为字典类型
     */
    Map<String, List<DubboDictDataVo>> getAllDictDataGroupedByType();
}
