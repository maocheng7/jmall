package com.jmall.common.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jmall.common.core.exception.SystemException;

import java.util.List;
import java.util.Map;

/**
 * JSON 工具类
 * <p>
 * 基于 Jackson ObjectMapper，提供对象与 JSON 字符串之间的序列化/反序列化。
 * 内置单例 ObjectMapper，线程安全。
 * </p>
 *
 * @author jmall
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    /**
     * 全局 ObjectMapper 实例
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 序列化配置
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 反序列化配置
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 支持 Java 8 时间类型
        MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * 获取 ObjectMapper 实例
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    /**
     * 对象转 JSON 字符串
     *
     * @param obj 对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SystemException("JSON序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 对象转格式化 JSON 字符串（美化输出）
     *
     * @param obj 对象
     * @return 格式化 JSON 字符串
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SystemException("JSON序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new SystemException("JSON反序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转 List
     *
     * @param json       JSON 字符串
     * @param elementType List 元素类型
     * @return List
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> elementType) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (JsonProcessingException e) {
            throw new SystemException("JSON反序列化List失败: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转 Map
     *
     * @param json   JSON 字符串
     * @param keyType   Key 类型
     * @param valueType Value 类型
     * @return Map
     */
    public static <K, V> Map<K, V> fromJsonToMap(String json, Class<K> keyType, Class<V> valueType) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructMapType(Map.class, keyType, valueType));
        } catch (JsonProcessingException e) {
            throw new SystemException("JSON反序列化Map失败: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转复杂泛型对象
     *
     * @param json        JSON 字符串
     * @param typeReference 泛型类型引用
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new SystemException("JSON反序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 对象转 Map
     *
     * @param obj 对象
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return MAPPER.convertValue(obj, Map.class);
    }

    /**
     * Map 转对象
     *
     * @param map   Map
     * @param clazz 目标类型
     * @return 对象
     */
    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        return MAPPER.convertValue(map, clazz);
    }
}
