package com.scaffold.common.json.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Jackson JSON 工具类。
 *
 * <p>封装常用序列化/反序列化操作，默认配置：</p>
 * <ul>
 *   <li>LocalDateTime 序列化为 yyyy-MM-dd HH:mm:ss</li>
 *   <li>忽略 null 值字段和空集合</li>
 *   <li>忽略未知属性不报错</li>
 *   <li>支持 Java 8 日期时间类型</li>
 *   <li>BigDecimal 序列化为字符串（保留精度）</li>
 * </ul>
 *
 * @author scaffold
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JacksonUtils {

    /** 全局共享的 ObjectMapper（线程安全） */
    private static final ObjectMapper MAPPER = createMapper();

    // ==================== ObjectMapper 工厂 ====================

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Java 8 日期时间模块
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        timeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        timeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        timeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // 日期序列化模块
        SimpleModule dateModule = new SimpleModule();
        dateModule.addSerializer(Date.class,
            new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        dateModule.addDeserializer(Date.class,
            new DateDeserializers.DateDeserializer(
                DateDeserializers.DateDeserializer.INSTANCE,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                "yyyy-MM-dd HH:mm:ss"));

        // BigDecimal 序列化（保留精度，避免科学计数法）
        SimpleModule bdModule = new SimpleModule();
        bdModule.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider sp)
                throws IOException {
                gen.writeString(value.toPlainString());
            }
        });
        bdModule.addDeserializer(BigDecimal.class, new JsonDeserializer<BigDecimal>() {
            @Override
            public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
                String text = p.getText();
                return (text != null && !text.isBlank()) ? new BigDecimal(text) : null;
            }
        });

        mapper.registerModules(timeModule, dateModule, bdModule);

        // 序列化包含策略：非空（null + 空集合 + 空字符串）
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // 忽略未知属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 允许单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        // 空对象不报错
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper;
    }

    /**
     * 获取全局共享的 ObjectMapper。
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    // ==================== 序列化 ====================

    /**
     * 对象序列化为 JSON 字符串。
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("序列化 JSON 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 对象序列化为格式化 JSON 字符串（带缩进，用于调试）。
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("序列化格式化 JSON 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 对象序列化为字节数组。
     */
    public static byte[] toBytes(Object obj) {
        if (obj == null) {
            return new byte[0];
        }
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.warn("序列化字节数组失败: {}", e.getMessage());
            return new byte[0];
        }
    }

    // ==================== 反序列化 ====================

    /**
     * JSON 字符串反序列化为对象。
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.warn("JSON 反序列化 [{}] 失败: {}", clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * JSON 字符串反序列化为复杂类型（支持泛型）。
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.warn("JSON 反序列化 TypeReference 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * JSON 字节数组反序列化。
     */
    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            log.warn("字节数组反序列化失败: {}", e.getMessage());
            return null;
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 深拷贝对象（通过 JSON 中转）。
     */
    public static <T> T deepClone(Object obj, Class<T> clazz) {
        String json = toJson(obj);
        return fromJson(json, clazz);
    }

    /**
     * 判断字符串是否为有效 JSON。
     */
    public static boolean isValidJson(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }
        try {
            MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 将 Map 转换为指定类型对象。
     */
    public static <T> T mapToObj(Map map, Class<T> clazz) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.convertValue(map, clazz);
        } catch (IllegalArgumentException e) {
            log.warn("Map 转对象失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 将对象转换为 Map。
     */
    public static Map<String, Object> objToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return MAPPER.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * 将对象转换为 LinkedHashMap（保留字段顺序）。
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> objToLinkedMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return MAPPER.convertValue(obj, LinkedHashMap.class);
    }
}
