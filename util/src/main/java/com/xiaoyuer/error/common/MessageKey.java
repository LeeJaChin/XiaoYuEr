package com.xiaoyuer.error.common;


import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import java.util.ResourceBundle;

/**
 * 异常资源值读取
 * @author CAIFUCHENG3
 */
public interface MessageKey {
    /**
     * 把异常枚举类名按驼峰式拆分为对应的前缀，格式：CommonError --->  COMMON_ERROR
     * @return
     */
    default String prefix() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, getClass().getSimpleName());
    }

    /**
     * name
     * @return
     */
    String name();

    /**
     * key值拼接：CommonError.UNEXPECTED --->  COMMON_ERROR.UNEXPECTED
     * @return
     */
    default String key() {
        return prefix() +"."+ name();
    }

    /**
     * 根据key值获取对应的国际化值，后续国际化读取不同的资源文件，当前默认i18n
     * @return
     */
    default String message() {
        ResourceBundle rb = ResourceBundle.getBundle("META-INF/i18n");
        String k = key();
        return rb.containsKey(k) ? rb.getString(k) : k;
    }


    default String message(String lan) {
        return message(key(),lan);
    }

    default String message(String key,String lan){
        String defaultlan = "i18n";
        if (!StringUtils.isEmpty(lan)) {
            if (!"zh-CN".equalsIgnoreCase(lan)) {
                defaultlan = lan;
            }
        }
        ResourceBundle rb = ResourceBundle.getBundle("META-INF/" + defaultlan);
        return rb.containsKey(key) ? rb.getString(key) : key;
    }

    default String sysmessage(String lan) {
        String defaultlan = "zh-CN_sys";
        if (!StringUtils.isEmpty(lan)) {
            defaultlan = lan + "_sys";
        }
        ResourceBundle rb = ResourceBundle.getBundle("META-INF/" + defaultlan);
        String k = key();
        return rb.containsKey(k) ? rb.getString(k) : k;
    }
}
