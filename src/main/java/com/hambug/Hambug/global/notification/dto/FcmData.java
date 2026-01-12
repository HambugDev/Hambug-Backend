package com.hambug.Hambug.global.notification.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record FcmData(
        FcmDataType type,
        Map<String, String> attributes
) {
    public FcmData {
        Objects.requireNonNull(type, "type must not be null");
        attributes = attributes == null ? Collections.emptyMap() : Map.copyOf(attributes);
    }

    public static FcmData of(FcmDataType type) {
        return new FcmData(type, Collections.emptyMap());
    }

    public static FcmData of(FcmDataType type, Map<String, String> attributes) {
        return new FcmData(type, attributes);
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("type", this.type.name());
        if (attributes != null && !attributes.isEmpty()) {
            map.putAll(attributes);
        }
        return map;
    }
}
