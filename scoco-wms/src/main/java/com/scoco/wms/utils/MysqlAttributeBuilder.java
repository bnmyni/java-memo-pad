package com.scoco.wms.utils;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 通过中文生成mysql表属性
 * @author sunke
 * @date 2018/12/18
 * @version 1.0.0.0
 */
public class MysqlAttributeBuilder {

    /**
     * 将中文转换成拼音，如果为空则返回一个随机的字符串
     * @param chinese 中文单词
     * @return 单词对应的拼音
     */
    public static String builderAttributeByChinese(String chinese) {

        if (StringUtils.isEmpty(chinese)) {
            return UUID.randomUUID().toString();
        }
        return PinyinTool.getPingYin(chinese).trim().replaceAll("\\W", "_");
    }

    /**
     * 将中文单词列表转换成中文拼音
     * @param chineseWords 中文单词列表
     * @return 单词对应的拼音列表
     */
    public static List<String> builderAtrributesByChinese(List<String> chineseWords){
        if (CollectionUtils.isEmpty(chineseWords)) {
            return null;
        }
        List<String> attributes = new ArrayList<>(chineseWords.size());
        chineseWords.forEach(e-> attributes.add(builderAttributeByChinese(e)));
        return attributes;
    }
}
