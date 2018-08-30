package com.tuoming.mes.strategy.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONSerializer;

import com.tuoming.mes.strategy.consts.Constant;

public class HttpUtil {

	public static void post(String url, String param) {
		PrintStream out = null;
		InputStream in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
	        conn.connect();
			// 获取URLConnection对象对应的输出流
			out = new PrintStream(conn.getOutputStream(), false, "utf-8");
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			in = conn.getInputStream();
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if(in !=null) {
					in.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		List<Map<String, String>> paramList = new ArrayList<Map<String,String>>();
        Map<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("commandname", "commandname");
        paramMap.put("servername", "servername");
        paramMap.put("username", "username");
        paramMap.put("ip", "ip");
        paramMap.put("port", "port");
        paramMap.put("datatype", Constant.PM);
        paramMap.put("grading", Constant.MINUTEUNIT);
        paramMap.put("status", "下载完成");
        paramList.add(paramMap);
        HttpUtil.post("http://172.16.101.19:8080/testweb/test", "collectstate="+JSONSerializer.toJSON(paramList));
	}

}
