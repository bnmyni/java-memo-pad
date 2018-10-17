/*******************************************************************************
 * Copyright (c) 2013.  Pyrlong All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyrlong.util.io;

import com.pyrlong.Envirment;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.CharacterSetToolkit;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 文件相关操作
 */
public class FileOper {
    private static Logger logger = LogFacade.getLog4j(FileOper.class);

    /**
     * 格式化路径，统一格式化为结尾带分隔符的路径
     *
     * @param path 要格式化的路径
     * @return
     */
    public static String formatePath(String path) {
        path = path.replace("\\", "/");
        if (!path.endsWith("/"))
            path += "/";
        return path;
    }

    /**
     * 删除修改时间在给定时间之前的文件
     *
     * @param dir      要搜索的文件夹
     * @param regex    用于匹配文件名的正则表达式
     * @param dateTime 时间点，格式为yyyy-MM-dd HH:mm:ss
     */
    public static void delFileModifyBefore(String dir, String regex, String dateTime) {
        List<String> subfiles = getFileModifyBefore(dir, regex, dateTime);
        deleteFiles(subfiles);
        List<String> dirs = getSubDir(dir, "[\\w\\d]+");
        for (String d : dirs) {
            File f = new File(d);
            File[] files = f.listFiles();
            //如果发现空目录则删除
            if (files != null && files.length == 0) {
                logger.info("Delete dir  " + d);
                f.delete();
            }
        }
    }


    /**
     * 批量重命名操作,
     *
     * @param forder  要重命名文件所处文件夹
     * @param newName 文件匹配
     * @param regex   匹配表达式
     */
    public static void renameAll(String forder, String regex, String newName) {
        List<String> files = getSubFiles(forder, regex, true);
        for (String file : files) {
            File f = new File(file);
            rename(f.getAbsolutePath(), f.getParentFile().getAbsolutePath() + Envirment.PATH_SEPARATOR + StringUtil.replaceAll(f.getName(), regex, newName));
        }
    }

    public static void copyTo(String srcName, String targetName) throws IOException {
        File file = new File(srcName);
        if (file.exists()) {
            BufferedInputStream inBuff = null;
            BufferedOutputStream outBuff = null;
            try {
                // 新建文件输入流并对它进行缓冲
                inBuff = new BufferedInputStream(new FileInputStream(srcName));

                // 新建文件输出流并对它进行缓冲
                outBuff = new BufferedOutputStream(new FileOutputStream(targetName));

                // 缓冲数组
                byte[] b = new byte[1024 * 5];
                int len;
                while ((len = inBuff.read(b)) != -1) {
                    outBuff.write(b, 0, len);
                }
                // 刷新此缓冲的输出流
                outBuff.flush();
            } finally {
                // 关闭流
                if (inBuff != null)
                    inBuff.close();
                if (outBuff != null)
                    outBuff.close();
            }
        }
    }

    public static void rename(String srcName, String targetName) {
        File file = new File(srcName);
        if (file.exists()) {
            file.renameTo(new File(targetName));
        }
    }

    /**
     * 对比两个文本文件是否相同
     *
     * @param source 原始文件
     * @param target 目标文件
     * @return 如果相同 返回 True，否则返回False
     */
    public static boolean isSame(String source, String target) {
        if (!isFileExist(source) || !isFileExist(target))
            return false;
        boolean result = true;
        try {
            BufferedReader streamSource = new BufferedReader(new InputStreamReader(new FileInputStream(source), FileOper.getFileEncoding(source)));
            BufferedReader targetSource = new BufferedReader(new InputStreamReader(new FileInputStream(target), FileOper.getFileEncoding(target)));
            String sourceLne = "";
            String targetLne = "";
            while ((sourceLne = streamSource.readLine()) != null) {
                targetLne = targetSource.readLine();
                if (targetLne == null) {
                    result = false;
                } else if (!targetLne.equals(sourceLne)) {
                    result = false;
                }
                if (!result)
                    break;
            }
            streamSource.close();
            targetSource.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    public static List<String> getFileModifyBefore(String dir, String regex, String dateTime) {
        List<String> files = getSubFiles(dir, regex, true);
        List<String> results = new ArrayList<String>();
        Long time = DateUtil.getTimeinteger(dateTime);
        for (String f : files) {
            File file = new File(f);
            if (file.lastModified() <= time) {
                results.add(f);
            }
        }
        return results;
    }

    public static void checkAndCreateForder(String fileOrForderName) {
        File file = new File(fileOrForderName);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        } else if (file.getName().indexOf(".") <= 0) {
            file.mkdirs();
        }
    }

    /**
     * 按行分割文件，主要用于对大数据的文件的入库等处理，注意本方法一般应用于文件大小不超过500M的情况为宜
     *
     * @param fileName 要分割的文件名
     * @param rowCount 文件包括对行数
     * @return
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @author James Cheung Date:Dec 27, 2012
     */
    public static List<String> splitFile(String fileName, int rowCount) throws FileNotFoundException, IOException {
        List<String> result = new ArrayList<String>();
        String line;
        InputStream is = new FileInputStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String outFilename = fileName + "_0";
        result.add(outFilename);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFilename, true), "utf-8"));
        line = reader.readLine();
        int count = 0;
        int fileCount = 1;
        while (line != null) {
            out.write(line);
            out.write(Envirment.LINE_SEPARATOR);
            count++;
            if (count == rowCount) {
                out.flush();
                out.close();
                count = 0;
                outFilename = fileName + "_" + fileCount;
                fileCount++;
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFilename, true), "utf-8"));
                result.add(outFilename);
            }
            line = reader.readLine();
        }
        out.close();
        reader.close();
        is.close();
        return result;
    }


    /**
     * 将文件按照给定的分割符分割为多个文件，目标文件名根据分割符自动生成
     *
     * @param sourceFile   原始文件，用于分割的
     * @param splitRegex   分割字符串
     * @param targetForder 分割后文件保存路径
     */
    public static List<String> splitFile(String sourceFile, String splitRegex, String targetForder) throws IOException {
        List<String> result = new ArrayList<String>();
        String line;
        InputStream is = new FileInputStream(sourceFile);
        targetForder = formatePath(targetForder);
        checkAndCreateForder(targetForder);
        String codeName = getFileEncoding(sourceFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, codeName));
        String outFilename;
        BufferedWriter out = null;// new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFilename, true), "utf-8"));
        line = reader.readLine();
        int fileCount = 0;
        while (line != null) {
            if (StringUtil.isMatch(line, splitRegex)) {
                if (out != null) {
                    out.close();
                }
                outFilename = StringUtil.getMatchString(line, splitRegex);
                outFilename = targetForder + formateFileName(outFilename);
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFilename, true), codeName));
                result.add(outFilename);
                fileCount++;
            }
            if (out != null) {
                out.write(line);
                out.write(Envirment.LINE_SEPARATOR);
            }
            line = reader.readLine();
        }
        out.close();
        reader.close();
        is.close();
        return result;
    }

    public static String formateFileName(String name) {
        return name.replaceAll("[<|>|:|=|;|\\s|,|-|\"]+", "_");
    }

    /**
     * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
     * @return boolean
     */
    public static void delFile(String filePathAndName) {
        String filePath = filePathAndName;
        filePath = filePath.toString();
        File myDelFile = new File(filePath);
        myDelFile.delete();
    }

    /**
     * 这个方法有问题，请谨慎使用，目前只支持带BOM的文件
     *
     * @param fileName
     * @return
     */
    public static String getFileEncoding(String fileName) {
        try {
        	String encode = CharacterSetToolkit.getFileEncoding(fileName);
        	if(encode.toLowerCase().equals("gb2312")) {
        		return "gbk";
        	}
            return CharacterSetToolkit.getFileEncoding(fileName);
        } catch (Exception ex) {
            System.out.println("Get file encoding error " + ex.getMessage() + ", file is " + fileName);
            return CharacterSetToolkit.UTF_8;
        }
    }

    /**
     * 获取给定目录下符合匹配结果的文件列表
     *
     * @param path
     * @param extend
     * @param includeSubDir
     * @return
     */
    public static List<String> getSubFiles(String path, String extend, boolean includeSubDir) {
        List<String> files = new ArrayList<String>();
        File root = new File(path);
        if (root.exists()) {
            File[] filelist = root.listFiles();
            if (filelist != null) {
                for (File f : filelist) {
                    if (f.isDirectory() && includeSubDir) {
                        files.addAll(getSubFiles(f.getAbsolutePath(), extend, includeSubDir));
                    } else if (f.getName().endsWith(extend) || StringUtil.isMatch(f.getName(), extend) || StringUtil.isEmpty(extend))
                        files.add(f.getAbsolutePath().replace("\\", "/"));
                }
            }
        } else {
            LogFacade.info("path not exists :" + path);
        }
        LogFacade.debug("found " + files.size() + " file in " + path);
        return files;
    }

    public static Stack<String> getSubDir(String path, String extend) {
        Stack<String> files = new Stack<String>();
        File root = new File(path);
        if (root.exists()) {
            File[] filelist = root.listFiles();
            if (filelist != null) {
                for (File f : filelist) {
                    if (f.isDirectory()) {
                        Stack<String> nextDirs = getSubDir(f.getAbsolutePath(), extend);
                        while (!nextDirs.empty()) {
                            files.push(nextDirs.pop());
                        }
                        if (f.getName().endsWith(extend) || StringUtil.isMatch(f.getName(), extend) || StringUtil.isEmpty(extend))
                            files.push(f.getAbsolutePath());
                    }
                }
            }
        } else {
            LogFacade.info("path not exists :" + path);
        }
        return files;
    }

    public static List<String> getSubFiles(String path, String extend) {
        return getSubFiles(path, extend, false);
    }

    public static String getFilePath(File file) {
        if (file == null) return "";
        return file.getAbsolutePath().replace("\\", "/");
    }

    /**
     * @param inFilename  输入文件
     * @param outFilename 目标文件
     * @param append      是否以追加方式写入
     * @return 写入到新文件的字节数
     * @throws java.io.IOException
     */
    public static long copyFile(String inFilename, String outFilename, boolean append) throws IOException {
        try {
            InputStream is = new FileInputStream(inFilename);
            int fileSize = 0;
            File file = new File(outFilename);
            if (!file.exists()) file.createNewFile();
            FileOutputStream out = new FileOutputStream(outFilename, append);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                fileSize += len;
            }
            out.flush();
            out.close();
            is.close();
            return fileSize;
        } catch (Exception ex) {
            return 0;
        }
    }

    public static void deleteFiles(List<String> files) {
        for (String s : files) {
            try {
                logger.info("Delete file " + s);
                delFile(s);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    public static void delFiles(String path, String filter) {
        delFiles(path, filter, true);
    }

    public static void delFiles(String path, String filter, boolean subForder) {
        List<String> files = getSubFiles(path, filter, subForder);
        for (String file : files) {
            delFile(file);
        }
    }

    /**
     * @param path String 文件夹路径 如 c:/fqf
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        List<String> tempList = getSubFiles(path, "");
        File temp = null;
        for (int i = 0; i < tempList.size(); i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(tempList.get(i));
            } else {
                temp = new File(tempList.get(i));
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(tempList.get(i));// 先删除文件夹里面的文件
                delFolder(tempList.get(i));// 再删除空文件夹
            }
        }
    }

    /**
     * @param folderPath String 文件夹路径及名称 如c:/fqf
     * @return boolean
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static long copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            int bytesum = 0;

            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                File newFile = new File(newPath);
                if (newFile.isDirectory()) {
                    if (!newPath.endsWith("\\") && !newPath.endsWith("/")) {
                        newPath += "/";
                    }
                    newPath += oldfile.getName();
                }
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
            return bytesum;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    public static void moveFile(String oldPath, String newPath) {
        logger.info("move " + oldPath + " to " + newPath);
        copyFile(oldPath, newPath);
        delFile(oldPath);
    }


    /**
     * 移动文件到指定目录
     *
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    /**
     * @return String
     */
    public static String getPath() {
        String sysPath = FileOper.class.getResource("/").getPath();
        // 对路径进行修改
        sysPath = sysPath.substring(1, sysPath.length() - 16);
        return sysPath;
    }

    public static void createForder(String path) {
        File forder = new File(path);
        if (!forder.exists()) {
            forder.mkdirs();
        }
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static long getLength(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return 0;
        return file.length();
    }

}
