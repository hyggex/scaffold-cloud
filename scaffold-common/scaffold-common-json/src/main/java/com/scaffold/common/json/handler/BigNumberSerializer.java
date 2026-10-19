package com.scaffold.common.json.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;

import java.io.IOException;

/**
 * 超出 JS 最大最小值处理。
 *
 * <p>JavaScript 的 Number 类型能精确表示的最大整数为 Number.MAX_SAFE_INTEGER (= 2^53-1)。
 * 超出此范围的 Long 值序列化为字符串，避免前端精度丢失。</p>
 */
@JacksonStdImpl
public class BigNumberSerializer extends NumberSerializer {

    /** JS 最大安全整数 */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    /** JS 最小安全整数 */
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    /** 提供实例 */
    public static final BigNumberSerializer INSTANCE = new BigNumberSerializer(Number.class);

    public BigNumberSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER) {
            super.serialize(value, gen, provider);
        } else {
            gen.writeString(value.toString());
        }
    }
}
