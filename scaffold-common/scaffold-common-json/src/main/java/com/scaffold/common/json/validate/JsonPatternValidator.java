package com.scaffold.common.json.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.scaffold.common.core.utils.StringUtils;
import com.scaffold.common.json.utils.JsonUtils;

/**
 * JSON 格式校验器
 *
 * @author 8800
 */
public class JsonPatternValidator implements ConstraintValidator<JsonPattern, String> {

    private JsonType jsonType;

    @Override
    public void initialize(JsonPattern annotation) {
        this.jsonType = annotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            // 交给 @NotBlank 或 @NotNull 控制是否允许为空
            return true;
        }
        return switch (jsonType) {
            case ANY -> JsonUtils.isJson(value);
            case OBJECT -> JsonUtils.isJsonObject(value);
            case ARRAY -> JsonUtils.isJsonArray(value);
        };
    }
}
