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

package com.tuoming.mes.collect.decoder.ericsson.asn1;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pyrlong.util.ConvertBinaryUtil;
import com.tuoming.mes.collect.decoder.ericsson.Decoder;


public abstract class AbstractBerDecoder implements Decoder {
    protected BufferedInputStream inputStream;

    public AbstractBerDecoder(File file) {
        try {
            InputStream steam = new FileInputStream(file);
            // the buffer size = 4MB
            inputStream = new BufferedInputStream(steam, 1048576 * 4);
        } catch (FileNotFoundException e) {
        	  e.printStackTrace();
        }
    }

    /**
     * 跳过和丢弃此输入流中数据的 n个字节
     */
    protected void skipBytes(int n) throws IOException {
        // 因为BufferedInputStream(skip只跳过缓存区中的字节)，因此不要用inputStream.skip(n)
        getBytes(n);
    }

    /**
     * 读取n个字节到byte[]
     */
    private byte[] getBytes(int n) throws IOException {
        byte[] b = new byte[n];
        inputStream.read(b);
        return b;
    }

    /**
     * 跳过下一组固定TLV值
     * @throws Exception 
     */
    protected void skipNextFixedTLV() throws Exception {
        int length = getNextFixedLength();
        if (length > 0) {
            // 跳过执行的Length长度(Value对应的长度)
            getBytes(length);
        } else if (length < 0) {
            throw new Exception( "TLV Length < 0!");
        }
    }

    /**
     * 取得下一组固定TLV中的Value
     */
    protected byte[] getNextFixedValue() throws IOException {

        int length = getNextFixedLength();

        // 取得Value的字节
        return getBytes(length);
    }

    /**
     * 取得下一组固定TLV中的Length
     */
    protected int getNextFixedLength() throws IOException {

        // 取得Tag和Length字节
        byte[] readTagAndLength = getBytes(2);
        return readTagAndLength[1];
    }

    /**
     * 取得下一组固定TLV中的Value转换为String
     */
    protected String getNextFixedValueToString() throws IOException {

        int length = getNextFixedLength();

        // 取得Value的字节
        return ConvertBinaryUtil.bytesToString(getBytes(length));
    }

    /**
     * 取得下一组固定TLV中的Value转换为int
     * @throws Exception 
     */
    protected int getNextFixedValueToInt() throws Exception {

        int length = getNextFixedLength();

        // long length的情况暂不处理
        if (length > 127) {
            throw new Exception( "TLV的Length大于127!");
        }

        // 取得Value的字节
        byte[] byteValues = getBytes(length);

        int value = ConvertBinaryUtil.bytesToInt(byteValues);

        return value;
    }

    /**
     * 取得下一组固定TLV中的Value转换为Long
     * @throws Exception 
     */
    protected long getNextFixedValueToLong() throws Exception {

        int length = getNextFixedLength();

        // long length的情况暂不处理
        if (length > 127) {
            throw new  Exception( "TLV的Length大于127!");
        }

        // 取得Value的字节
        byte[] byteValues = getBytes(length);
        long value = ConvertBinaryUtil.bytesToLong(byteValues);
        return value;
    }

    /**
     * 取得下一组固定TLV中的Value转换为Timestamp
     * @throws IOException 
     */
    protected Timestamp getNextFixedValueToTimeStamp() throws IOException   {

        int length = getNextFixedLength();
        // 取得Value的字节
        String strTime = ConvertBinaryUtil.bytesToString(getBytes(length));

        try {
            // 转换为Timestamp类型
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyyMMddHHmm");
            Date d = format.parse(strTime);
            return new Timestamp(d.getTime());
        } catch (ParseException e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
		return null;
    }

    /**
     * 查看是否为结束标记位,如果是则返回true并跳过结束位 n为结束标记的长度
     */
    protected boolean isEndOfContent(int n) throws IOException {

        boolean endFlag = true;
        // 标记开始位置
        inputStream.mark(n);

        byte[] endFlags = getBytes(n);
        for (byte b : endFlags) {
            if (b != 0x0) {
                endFlag = false;
                break;
            }
        }

        // 如果不是结束标志位，返回标记开始的位置
        if (!endFlag) {
            inputStream.reset();
        }

        return endFlag;
    }

    /**
     * 关闭输入流对象
     */
    protected void close() throws IOException {
        inputStream.close();
    }

}
