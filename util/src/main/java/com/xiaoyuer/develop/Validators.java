package com.xiaoyuer.develop;

import com.alibaba.fastjson.JSONObject;
import com.xiaoyuer.error.CommonError;
import com.xiaoyuer.error.common.Errors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by george on 2023/9/12 10:46
 *
 * @author george
 */
@Slf4j
public class Validators {

    public static void validatorParam(Object param, boolean all, List<String> exclude,String... keys) throws NoSuchFieldException, IllegalAccessException {
        if (param == null) {
            throw Errors.wrap(CommonError.PARAM_IS_NULL, "param");
        }
        log.info("validator Object : {}", JSONObject.toJSONString(param));
        Class<?> clazz = param.getClass();
        String[] fieldsToValidate = all ? getAllFieldNames(clazz,exclude) : keys;
        for (String key : fieldsToValidate) {
            Field field = clazz.getDeclaredField(key);
            field.setAccessible(true);
            Object value = field.get(param);
            if (value == null) {
                log.info("validator key : {}", key);
                throw Errors.wrap(CommonError.PARAM_IS_NULL, key);
            }
            if (value instanceof String) {
                String strValue = (String) value;
                if (strValue.isEmpty()) {
                    log.info("validator key : {}", key);
                    throw Errors.wrap(CommonError.PARAM_IS_NULL, key);
                }
            }

            if (value instanceof List) {
                List list = (List) value;
                if (CollectionUtils.isEmpty(list)) {
                    log.info("validator key : {}", key);
                    throw Errors.wrap(CommonError.PARAM_IS_NULL, key);
                }
            }

            if (value instanceof Map) {
                Map map = (Map) value;
                if (MapUtils.isEmpty(map)) {
                    log.info("validator key : {}", key);
                    throw Errors.wrap(CommonError.PARAM_IS_NULL, key);
                }
            }
        }
    }

    private static String[] getAllFieldNames(Class<?> clazz,List<String> exclude) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            list.add(fields[i].getName());
        }

        return CollectionUtils.isNotEmpty(exclude) ? list.stream().filter(e-> !exclude.contains(e)).toArray(String[]::new) : list.stream().toArray(String[]::new);
    }
}
