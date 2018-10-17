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

package com.pyrlong.net.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.pyrlong.collection.CollectionsBase;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.exception.ExceptionCodes;
import com.pyrlong.exception.PyrlongException;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.strategy.util.DateUtil;

/**
 * Created with IntelliJ IDEA. User: james Date: 6/11/13 Time: 11:48 AM
 */
public class FTPClientExt {
    private static Log logger = LogFactory.getLog(FTPClientExt.class);
    private static Map<String, FTPClientExt> servers = null;
    private FTPClient ftpClient;
    private String url;
    private Integer port = 21;
    private String password;
    private String uid;
    private String name;
    private String controlEncoding;
    private boolean enterLocalPassiveMode;
    private int retryCount = 0;

    public void setControlEncoding(String encoding) {
        controlEncoding = encoding;
    }

    public FTPClientExt() {
        ftpClient = new FTPClient();
    }

    public FTPClientExt(String url, int port, String uid, String password) {
        ftpClient = new FTPClient();
        this.url = url;
        this.port = port;
        this.password = password;
        this.uid = uid;
    }

    public boolean isEnterLocalPassiveMode() {
        return enterLocalPassiveMode;
    }

    public void setEnterLocalPassiveMode(boolean enterLocalPassiveMode) {
        this.enterLocalPassiveMode = enterLocalPassiveMode;
    }

    public static FTPClientExt getInstance(String name) {
        if (servers == null) {
            servers = new HashMap<String, FTPClientExt>();
            synchronized (servers) {
                CollectionsBase<Object> servs = ConfigurationManager.getDefaultConfig().getAdvanceObjectCollection().get("FTPServers");
                if (servs != null) {
                    for (Object srv : servs) {
                        FTPClientExt s = (FTPClientExt) srv;
                        if (s != null) {
                            servers.put(s.getName(), s);
                        }
                    }
                }
            }
        }
        return servers.get(name).clone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public FTPClientExt clone() {
        FTPClientExt ftp = new FTPClientExt();
        ftp.setName(name);
        ftp.setUrl(url);
        ftp.setPort(port);
        ftp.setUid(uid);
        ftp.setPassword(password);
        return ftp;
    }

    public void setConnectTimeout(int seconds) {
        ftpClient.setConnectTimeout(seconds);
    }

    public void changeWorkDir(String remoteDir) {
        try {
            ftpClient.changeWorkingDirectory(remoteDir);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public boolean connect() {
        try {
            int reply;
            // 杩炴帴FTP鏈嶅姟鍣�
            ftpClient.connect(url, port);
            // 鐧诲綍FTP
            ftpClient.login(uid, password);
            ftpClient.setParserFactory(new FTPFileEntryParserFactoryExt());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setDefaultTimeout(120000);
            ftpClient.setDataTimeout(120000);
            if (StringUtil.isNotEmpty(controlEncoding))
                ftpClient.setControlEncoding(controlEncoding);
            logger.info("Control encoding " + ftpClient.getControlEncoding());
            if (isEnterLocalPassiveMode())
                ftpClient.enterLocalPassiveMode();
            else
                ftpClient.enterLocalActiveMode();

            ftpClient.addProtocolCommandListener(new PrintCommandListener(System.out));
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.error("disconnected from the server, server return code :" + reply);
                ftpClient.disconnect();
                return false;
            }
            logger.info("Login success:" + url + ":" + port);
            return true;
        } catch (Exception e) {
            logger.fatal(url + " : " + e.getMessage(), e);
            if (retryCount < 5) {
                try {
                    logger.info("Retry to connect to " + url);
                    Thread.currentThread().join(30000);
                    retryCount++;
                    return connect();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        }
    }

    public boolean deleteFile(String remoteFile) {
        try {
            ftpClient.deleteFile(remoteFile);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean deleteDir(String pathname) {
        try {
            if (!pathname.endsWith("/")) pathname += "/";
            ftpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFile = ftpClient.listFiles();
            for (FTPFile ff : ftpFile) {
            	if(ff.getTimestamp().getTimeInMillis()>System.currentTimeMillis()-4*3600*1000){//只删除4小时之前的数据
            		logger.info("file modifyTime is " + ff.getTimestamp().getTimeInMillis()+" and systemTime is "+System.currentTimeMillis());
            		continue;
            	}
                if (ff.isDirectory()) {
                    deleteDir(pathname + ff.getName());
                    ftpClient.changeWorkingDirectory("..");
                } else {
                    logger.info("Try delete file :" + ff.getName());
                    this.deleteFile(ff.getName());
                }
            }
            ftpClient.changeWorkingDirectory("..");
            logger.info("Try delete " + pathname);
            return ftpClient.removeDirectory(pathname);
        } catch (IOException e) {
            logger.fatal(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 鏍规嵁缁欏畾璺緞涓嬬殑FTPClient瀵硅薄浠ュ強缁欏畾鐨勬鍒欒〃杈惧紡锛岃幏鍙栧綋鍓嶈矾寰勪笅鐨勬枃浠跺悕瀛楃殑鍒楄〃
     *
     * @param regex 鍖归厤鏂囦欢鍚嶇殑姝ｅ垯琛ㄨ揪寮�
     * @return 鏂囦欢鍚嶆暟缁�
     */
    public String[] getFileNames(String regex) {
        String[] fileNames = null;

        try {
            fileNames = ftpClient.listNames();
        } catch (Exception e) {
            throw new PyrlongException(ExceptionCodes.ERROR_COMMON_1007);
        }

        if (fileNames == null) {
            logger.debug("There are no files match your regex rule:" + regex);
            return null;
        }

        String[] targetFileNames = new String[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            if (fileNames[i].matches(regex)) {
                targetFileNames[i] = fileNames[i];
            }
        }

        return targetFileNames;
    }

    /**
     * 鏍规嵁缁欏畾璺緞涓嬬殑FTPClient瀵硅薄浠ュ強缁欏畾鐨勬鍒欒〃杈惧紡锛岃幏鍙栧綋鍓嶈矾寰勪笅鐨勬枃浠跺悕瀛楃殑鍒楄〃
     *
     * @param listPath 闇�鏌ョ湅鐨刦tp鐩綍
     * @param regex    鍖归厤鐩綍鍚嶇殑姝ｅ垯琛ㄨ揪寮�
     * @return 鏂囦欢鍚嶆暟缁�
     */
    public String[] getFileDir(String listPath, String regex) throws IOException {
        FTPFile[] ftpFile = null;
        ftpClient.changeWorkingDirectory(listPath);
        logger.info("List files...");
        ftpFile = ftpClient.listFiles(listPath);
        if (ftpFile == null) {
            logger.debug("There are no files match your regex rule:" + regex);
            return null;
        }
        logger.info("found file:" + ftpFile.length);
        List<String> listFile = new ArrayList<String>();
        for (int i = 0; i < ftpFile.length; i++) {
            if (ftpFile[i].isDirectory() && (StringUtil.isEmpty(regex) || ftpFile[i].getName().matches(regex))) {
                listFile.add(ftpFile[i].getName());
            }
        }
        String[] targetFileNames = new String[listFile.size()];
        for (int i = 0; i < listFile.size(); i++) {
            targetFileNames[i] = listFile.get(i);
        }
        return targetFileNames;
    }

    /**
     * 涓嬭級閬犵▼鏂囦欢澶惧収瀹癸紝鍖呮嫭瀛愮洰褰�
     *
     * @param remoteDir
     * @param localDir
     * @param regex
     * @return
     */
    public boolean downloadDir(String remoteDir, String localDir, String regex, boolean includeSubDir, boolean deleteAfterDown) {
        try {
            String[] subDirs = getFileDir(remoteDir, "");
            if (!remoteDir.endsWith("/")) remoteDir = remoteDir + "/";
            logger.info("Downloading dir " + remoteDir);
            downloadFile(remoteDir, localDir, regex, deleteAfterDown);
            if (includeSubDir) {
                for (String sb : subDirs) {
                    downloadDir(remoteDir + sb, localDir + "/" + sb, regex, includeSubDir, deleteAfterDown);
                    if (deleteAfterDown)
                        deleteDir(remoteDir + sb);
                }
            }
            return true;
        } catch (Exception e) {
            logger.fatal(url + " : " + e.getMessage(), e);
            return false;
        }
    }

    public boolean downloadFile(String remoteDir, String localDir, String regex, boolean deleteAfterDown) {
        boolean isSuccess = false;
        if (ftpClient == null) {
            logger.warn("ftpClient not connected");
            return false;
        }
        try {
            // 鎸囧畾涓嬭浇鐩綍
            ftpClient.changeWorkingDirectory(remoteDir);
            String currentDir = ftpClient.printWorkingDirectory();
            if(remoteDir.indexOf(currentDir)==-1) {
            	logger.info("当前目录:"+currentDir+",目标文件目录："+remoteDir);
            	return false;
            }
            List<String> fileNames = new ArrayList<String>();
            FTPFile[] ftpFile =  ftpClient.listFiles();
            logger.info("found files " + ftpFile.length);
            boolean getFileByCreateDate = false;
            int second = 0;
            String dateFormat = null;
            if(StringUtil.isNotEmpty(regex)&&regex.contains("getFileByCreateDate")) {
            	Matcher m = Pattern.compile("(.*?)getFileByCreateDate\\((\\d+),'(.*?)'\\)",Pattern.CASE_INSENSITIVE).matcher(regex);
            	if(m.find()) {
            		regex = m.group(1);
            		second = Integer.parseInt(m.group(2));
            		dateFormat = m.group(3);
            		getFileByCreateDate = true;
            	}
            }
            for (FTPFile ff : ftpFile) {
                if (!ff.isDirectory() && (StringUtil.isMatch(ff.getName(), regex) || ff.getName().endsWith(regex) || StringUtil.isEmpty(regex))) {
                    if(!getFileByCreateDate||(getFileByCreateDate&&this.compare(ff.getTimestamp(), second, dateFormat))) {
                    	fileNames.add(ff.getName());
                    }
                }
            }
            if(fileNames.isEmpty()) {
            	logger.info("Start download files,there are " + fileNames.size() + " files founded");
            	return false;
            }
            localDir = FileOper.formatePath(localDir);
            FileOper.checkAndCreateForder(localDir);
            if(!FileOper.isFileExist(localDir)) {
            	FileOper.checkAndCreateForder(localDir);
            }
            logger.info("Start download files,there are " + fileNames.size() + " files founded");
            int successCount = 0;
            for (String fileName : fileNames) {
                logger.debug("Get file:" + fileName);
                String fileFullPath = localDir + fileName.replaceAll(":", ".");
                if (FileOper.isFileExist(fileFullPath)) {
                    logger.info("There is a file with the same name in the local dir,skiped : " + fileFullPath);
                    continue;
                }
                fileFullPath += ".tmp";
                File localFilePath = new File(fileFullPath);
                try {
                	
                	OutputStream output = new FileOutputStream(localFilePath);
                	ftpClient.isRemoteVerificationEnabled();
                	ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                	ftpClient.retrieveFile(fileName, output);
                	output.close();
                	FileOper.moveFile(fileFullPath, fileFullPath.substring(0, fileFullPath.length() - 4));
				} catch (IOException e) {
					logger.info("下载 ："+fileName+" 失败!正在尝试重新下载："+fileName);
					disconnect();
					if(connect()) {
						File errFile = new File(fileFullPath.substring(0, fileFullPath.length() - 4)); 
						if(errFile.exists()) {
							errFile.delete();
						}
						File tmpFile = new File(fileFullPath); 
						if(tmpFile.exists()) {
							tmpFile.delete();
						}
						ftpClient.changeWorkingDirectory(remoteDir);
						OutputStream output = new FileOutputStream(localFilePath);
						ftpClient.isRemoteVerificationEnabled();
						ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
						ftpClient.retrieveFile(fileName, output);
						output.close();
						FileOper.moveFile(fileFullPath, fileFullPath.substring(0, fileFullPath.length() - 4));
					}
				}
                successCount++;
//                if (deleteAfterDown) {
//                    logger.debug("Success delete remote file:" + fileName);
//                    deleteFile(fileName);
//                }
                logger.debug("Successful get file :" + fileName);
            }
            logger.info("There are " + successCount + " files downloaded," + (fileNames.size() - successCount) + " files failed");
            isSuccess = true;
        } catch (Exception e) {
            logger.fatal(url + " : " + e.getMessage(), e);
            logger.info(retryCount + " Retry download file " + url + remoteDir);
            disconnect();
            if (connect() && retryCount < 5) {
            	try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
                retryCount++;
                return downloadFile(remoteDir, localDir, regex, deleteAfterDown);
            }
        }
        return isSuccess;
    }

    private boolean compare(Calendar timestamp, int second, String dateFormat) {
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.SECOND, -second);
//    	String current = DateUtil.format(cal.getTime(), dateFormat);
//    	String time = DateUtil.format(timestamp.getTime(), dateFormat);
//    	if(current.equals(time)) {
//    		return true;
//    	}
    	if(timestamp.compareTo(cal)>=0) {
    		return true;
    	}
		return false;
	}

	public boolean downloadFile(String remoteDir, String localDir) {
        boolean isSuccess = false;
        try {
            downloadFile(remoteDir, localDir, "", false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return isSuccess;
    }

    public void disconnect() {
        if (ftpClient.isConnected()) {
            try {
                // 閫�嚭FTP
                ftpClient.abort();
                ftpClient.logout();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }finally {
            	if(ftpClient.isConnected()) {
            		try {
            			ftpClient.disconnect();
					} catch (Exception e2) {
					}
            	}
            }
        }
    }

    /**
     * 浠巉tp鏈嶅姟鍣ㄦ寚瀹氱洰褰曚笅锛屾牴鎹粰瀹氱殑鏂囦欢鍚嶅瓧鍒楄〃涓嬭浇鎸囧畾鏂囦欢鍒版寚瀹氱殑鏈湴鐩綍涓�
     *
     * @param remoteDir 瑕佷笅杞界殑鏂囦欢鍦‵tp鏈嶅姟鍣ㄤ笂鐨勮矾寰�
     * @param localDir  鏂囦欢瑕佷笅杞藉埌鏈湴鐨勮矾寰�
     * @param fileNames 瑕佷笅杞界殑鏂囦欢鍚嶅垪琛�
     * @return 涓嬭浇鏂囦欢鐨勭姸鎬侊紝鎴愬姛杩斿洖true锛屽け璐ヨ繑鍥瀎alse
     */
    public boolean downloadFile(String remoteDir, String localDir, List<String> fileNames) {
        if (!localDir.endsWith("/") && !localDir.endsWith("\\"))
            localDir += "/";
        File localDirectory = new File(localDir);
        if (!localDirectory.exists()) {
            localDirectory.mkdirs();
        }
        boolean isSuccess = false;
        if (ftpClient == null) {
            return false;
        }
        try {
            // 鎸囧畾涓嬭浇鐩綍
            ftpClient.changeWorkingDirectory(remoteDir);
            int successCount = 0;
            int failedCount = 0;
            logger.info("Start downloading ,there are " + fileNames.size() + " files in process");
            for (String file : fileNames) {
                boolean success = true;
                String targetFileName = file;
                String fileFullPath = null;
                try {
                    logger.info("Start get " + targetFileName);
                    fileFullPath = localDir + targetFileName;
                    File localFilePath = new File(fileFullPath);
                    OutputStream output = new FileOutputStream(localFilePath);
                    ftpClient.retrieveFile(targetFileName, output);
                    output.close();
                    if (FileOper.getLength(fileFullPath) > 0) {
                        successCount++;
                        logger.info("Success get " + targetFileName);
                    } else {
                        FileOper.delFile(fileFullPath);
                    }
                    success = true;
                }catch (IOException ex) {
                	logger.info(targetFileName+" 下载失败! start reget "+targetFileName);
                	if (FileOper.getLength(fileFullPath) >0) {
                		FileOper.delFile(fileFullPath);
                    }
                	if(connect()) {
                		File localFilePath = new File(fileFullPath);
                		OutputStream output = new FileOutputStream(localFilePath);
                		ftpClient.retrieveFile(targetFileName, output);
                		output.close();
                		logger.warn(ex.getMessage());
                	}
                }
                if (!success) {
                    failedCount++;
                    logger.info("There are no file named " + targetFileName);
                }
            }
            logger.info("Successful get " + successCount + " files falis"+ failedCount + " failed");
            isSuccess = true;
        } catch (Exception e) {
            logger.fatal(url + " : " + e.getMessage(), e);
            //閲嶈瘯
            logger.info(retryCount + " Retry download file " + url + remoteDir);
            disconnect();
            if (connect() && retryCount < 5) {
                retryCount++;
                return downloadFile(remoteDir, localDir, fileNames);
            }
        }
        return isSuccess;
    }
}
