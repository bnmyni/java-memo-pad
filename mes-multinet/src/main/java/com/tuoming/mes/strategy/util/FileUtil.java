package com.tuoming.mes.strategy.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.pyrlong.util.StringUtil;

public class FileUtil {

    /**
     * 按照正则表达式将指定文件夹下的文件归类
     *
     * @param file
     * @param fileRegex
     * @return
     */
    public static Map<String, List<String>> getChildFile(String file,
                                                         String fileRegex) {
        Map<String, List<String>> resMap = new HashMap<String, List<String>>();
        File f = new File(file);
        if (!f.exists()) {
            return resMap;
        }
        if (f.isDirectory()) {
            for (File childFile : f.listFiles()) {
                Map<String, List<String>> resList = getChildFile(childFile.getAbsolutePath(), fileRegex);
                for (Entry<String, List<String>> entry : resList.entrySet()) {
                    if (resMap.get(entry.getKey()) == null) {
                        resMap.put(entry.getKey(), new ArrayList<String>());
                    }
                    resMap.get(entry.getKey()).addAll(entry.getValue());
                }
            }
        } else {
            if (StringUtil.isEmpty(fileRegex)) {
                if (resMap.get("ALL") == null) {
                    resMap.put("ALL", new ArrayList<String>());
                }
                resMap.get("ALL").add(file);
            } else {
                Matcher m = Pattern.compile(fileRegex).matcher(f.getName());
                if (m.find()) {
                    String key = "";
                    for (int i = 1; i <= m.groupCount(); i++) {
                        key = key + m.group(i);
                    }
                    if (resMap.get(key) == null) {
                        resMap.put(key, new ArrayList<String>());
                    }
                    resMap.get(key).add(file);
                }
            }

        }
        return resMap;
    }

}
