package com.tuoming.mes.strategy.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

public class CompressUtil {
	
	public static byte[] decompress(String infile) throws Exception {
        if (infile.endsWith(".gz")) {
        	return ungzip(infile);
        }else if(infile.endsWith(".zip")) {
        	return unzip(infile);
        }
        return null;
    }
	
    private static byte[] ungzip(String infile) throws IOException {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GzipCompressorInputStream gis = null;
        try {
        	gis = new GzipCompressorInputStream(new FileInputStream(infile));
        	int count;
        	byte data[] = new byte[1024];
        	while ((count = gis.read(data, 0, 1024)) != -1) {
        		baos.write(data, 0, count);
        	}
        	return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {
			if(gis!=null) {
				gis.close();
			}
			if(baos!=null) {
				baos.close();
			}
		}
		return null;
    }

    
    private static byte[] unzip(String infile)
            throws IOException {
        InputStream is = null;
        ZipFile zipFile = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            zipFile = new ZipFile(infile);
            Enumeration entryEnum = zipFile.getEntries();
            if (null != entryEnum) {
                ZipArchiveEntry zipEntry = null;
                while (entryEnum.hasMoreElements()) {
                    zipEntry = (ZipArchiveEntry) entryEnum.nextElement();
                    if (zipEntry.isDirectory()) {
                        continue;
                    }
                    if (zipEntry.getSize() > 0) {
                        // 鏂囦欢
                        is = zipFile.getInputStream(zipEntry);
                        byte[] buffer = new byte[4096];
                        int readLen = 0;
                        while ((readLen = is.read(buffer, 0, 4096)) >= 0) {
                        	baos.write(buffer, 0, readLen);
                        }
                    }
                }
            }
        } catch (IOException ex) {
        	ex.printStackTrace();
        } finally {
            if (null != zipFile) {
            	zipFile.close();
                zipFile = null;
            }
            if (null != is) {
                is.close();
            }
            if(baos!=null) {
            	baos.close();
            }
        }
        return baos.toByteArray();
    }

}
