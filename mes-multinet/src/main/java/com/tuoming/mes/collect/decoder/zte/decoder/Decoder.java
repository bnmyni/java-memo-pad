/*******************************************************************************
 * Copyright (c) 2014.  Pyrlong All rights reserved.
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

package com.tuoming.mes.collect.decoder.zte.decoder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by shenhaitao on 2014/7/22 0022.
 */
public abstract class Decoder {

    private BufferedInputStream in;

    private String toatlBitStr = "";

    private String curbitStr = "";

    private boolean isFinish = false;

    public Decoder(File file) throws FileNotFoundException {
        in = new BufferedInputStream(new FileInputStream(file));
    }

    private String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }


    private byte bitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {
            if (byteStr.charAt(0) == '0') {
                re = Integer.parseInt(byteStr, 2);
            } else {
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }


    private void readBitStr() throws IOException {
        byte[] b = new byte[128];
        int temp = in.read(b, 0, 128);
        if (temp == -1)
        {
            isFinish=true;
            return ;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(byteToBit(b[i]));
        }
        toatlBitStr = toatlBitStr + sb.toString();
    }

    private void ReadBit(int bitSize) throws IOException {

        if (toatlBitStr.equals("") || toatlBitStr.length() < bitSize) {
            readBitStr();
        }
        if (!isFinish)
        {
            curbitStr = toatlBitStr.substring(0, bitSize);
            toatlBitStr = toatlBitStr.substring(bitSize);
        }
    }

    private String PadLeft(String input, int size, char symbol) {
        while (input.length() < size) {
            input = symbol + input;
        }
        return input;
    }


    protected String readBitAsBitStr(int bitSize) throws IOException {
        ReadBit(bitSize);
        return curbitStr;
    }

    protected int cell(int bitSize) throws IOException {
        ReadBit(bitSize);
        int ss1 = Integer.parseInt(curbitStr, 2);
        return ss1;
    }

    protected String readBitAsHexStr(int bitSize) throws IOException {
        ReadBit(bitSize);
        String er = "";
        for (int i = 0; i < curbitStr.length() / 8; i++) {
            int ss1 = Integer.parseInt(curbitStr.substring(i * 8, 8 * (i + 1)),
                    2);

            er += " 0x" + PadLeft(String.format("%x", ss1), 2, '0');
        }
        return er;
    }
    protected int readBitAsNum(int bitSize) throws IOException {
        ReadBit(bitSize);
        int ss1 = Integer.parseInt(curbitStr, 2);
        return ss1;
    }


    protected String readBitAsStr(int bitSize) throws IOException {
        ReadBit(bitSize);
        String isoString = "";
        if (curbitStr.length() % 8 == 0) {
            byte[] data = new byte[curbitStr.length() / 8];

            for (int i = 0; i < curbitStr.length() / 8; i++) {

                data[i] = bitToByte(curbitStr.substring(i * 8, 8 * (i + 1)));
            }
            isoString = new String(data,"UTF-8");
        }
        return isoString;
    }

    protected void close() throws IOException {
        in.close();
    }
}
