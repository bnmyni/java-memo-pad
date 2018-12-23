package com.scoco.wms.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类，将中文转换成拼音
 *
 * @author sunke
 * @version 1.0.0.0
 * @date 2018/12/18
 */
public class PinyinTool {

    private static HanyuPinyinOutputFormat getPinYinFormat() {
        final HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        return format;
    }

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     */
    public static String getPingYin(String chinese) {
        HanyuPinyinOutputFormat format = getPinYinFormat();
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        StringBuilder output = new StringBuilder();

        try {
            for (char c : chinese.trim().toCharArray()) {
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    output.append(temp[0]);
                } else {
                    output.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese) {
        StringBuilder output = new StringBuilder();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat format = getPinYinFormat();
        for (char c : arr) {
            if (c > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (temp != null) {
                        output.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                output.append(c);
            }
        }
//        return output.toString().replaceAll("\\W", "_").trim();
        return output.toString();
    }

}
