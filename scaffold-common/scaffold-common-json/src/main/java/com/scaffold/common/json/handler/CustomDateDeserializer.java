package com.scaffold.common.json.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date 反序列化器，支持多种日期格式。
 *
 * <p>支持的格式：yyyy-MM-dd HH:mm:ss、yyyy-MM-dd、yyyy/MM/dd HH:mm:ss、yyyy/MM/dd</p>
 *
 * @author 8800
 */
public class CustomDateDeserializer extends JsonDeserializer<Date> {

    private static final String[] PATTERNS = {
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy/MM/dd",
        "yyyyMMddHHmmss",
        "yyyyMMdd"
    };

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return null;
        }
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return new Date(p.getLongValue());
        }
        String text = p.getValueAsString();
        if (text == null || text.isEmpty()) {
            return null;
        }
        for (String pattern : PATTERNS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                return sdf.parse(text);
            } catch (ParseException ignored) {
                // 尝试下一个格式
            }
        }
        return null;
    }
}
