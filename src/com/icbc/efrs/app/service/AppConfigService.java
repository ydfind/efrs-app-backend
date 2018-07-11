package com.icbc.efrs.app.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

import javax.servlet.ServletContext;

import com.icbc.efrs.app.constant.Constants;
import com.icbc.efrs.app.utils.CoreUtils;

public class AppConfigService{
	private static long lastModified = -1;
	private static LinkedHashMap<String,String> propMap = null;
	public final static String filePath = "/WEB-INF/appconfig/efrsapp.properties";
	/**
	 * 
	 * @param context
	 */
	private static void genPropList(ServletContext context){
		String domainFilePath = context.getRealPath(filePath);
		File propFile = new File(domainFilePath);
		long timestamp = propFile.lastModified();
		if(timestamp > lastModified){
			propMap = new LinkedHashMap<String,String>();
			String line="";
			try {
				BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(domainFilePath), Constants.ENCODING_DEF));
				while ((line = bfr.readLine()) != null){
					line = new String(line.getBytes(Constants.ENCODING_DEF),Constants.ENCODING_DEF);
					if(line.contains("=")){
						String key = line.substring(0,line.indexOf("=")).trim();
						String val = line.substring(line.indexOf("=")+1).trim();
						propMap.put(key,val);
					}
				}
				bfr.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			lastModified = timestamp;
		}
	}
	/**
	 * 
	 * @param domainFilePath
	 */
	private static void genPropList(String domainFilePath){
		File propFile = new File(domainFilePath);
		long timestamp = propFile.lastModified();
		if(timestamp > lastModified){
			propMap = new LinkedHashMap<String,String>();
			String line="";
			try {
				BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(domainFilePath), Constants.ENCODING_DEF));
				while ((line = bfr.readLine()) != null){
					line = new String(line.getBytes(Constants.ENCODING_DEF),Constants.ENCODING_DEF);
					if(line.contains("=")){
						String key = line.substring(0,line.indexOf("=")).trim();
						String val = line.substring(line.indexOf("=")+1).trim();
						propMap.put(key,val);
					}
				}
				bfr.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			lastModified = timestamp;
		}
	}
	
	/**
	 * 读文件内容
	 * @param context
	 * @return 读文件内容
	 */
	public static String getContent(ServletContext context){
		String domainFilePath = context.getRealPath(filePath);
		return getContent(domainFilePath);
	}
	
	/**
	 * 读文件内容
	 * @param context
	 * @return 读文件内容
	 */
	public static String getContent(ServletContext context,String del){
		String domainFilePath = context.getRealPath(filePath);
		return getContent(domainFilePath,del);
	}
	
	/**
	 * 读文件内容
	 * @param domainFilePath
	 * @return 读文件内容
	 */
	public static String getContent(String domainFilePath){
		StringBuffer sb = new StringBuffer();
		String line="";
		try {
			BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(domainFilePath), Constants.ENCODING_DEF));
			while ((line = bfr.readLine()) != null){
				if(!line.startsWith("#")){
					sb.append(line).append("\n");
				}
			}
			bfr.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 读文件内容
	 * @param domainFilePath
	 * @param del
	 * @return 读文件内容
	 */
	public static String getContent(String domainFilePath,String del){
		StringBuffer sb = new StringBuffer();
		String line="";
		try {
			BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(domainFilePath), Constants.ENCODING_DEF));
			while ((line = bfr.readLine()) != null){
				line = new String(line.getBytes(Constants.ENCODING_DEF),Constants.ENCODING_DEF);
				if(!line.startsWith("#")){
					sb.append(line).append(del);
				}
			}
	        if (sb.length() > 0)
	            sb.delete(sb.length() - del.length(), sb.length());
			bfr.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 上传
	 * @param domainFilePath
	 * @param contentByteArr
	 */
	public static void uploadConfigPropFile(String domainFilePath,byte[] contentByteArr){
		File configFile = new File(domainFilePath);
		if(!configFile.canWrite()){
			configFile.setWritable(true);
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(configFile);
			os.write(contentByteArr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 无对应值，返回空字符串
	 * @param domainFilePath
	 * @param key
	 * @return 无对应值，返回空字符串
	 */
	public static String getString(String domainFilePath,String key){
		genPropList(domainFilePath);
		String ret = "";
		if(CoreUtils.nullSafeSize(propMap)>0 && propMap.containsKey(key)){
			ret = propMap.get(key);
		}
		return ret;
	}
	
	/**
	 * 无对应值，返回空字符串
	 * @param context
	 * @param key
	 * @return 无对应值，返回空字符串
	 */
	public static String getString(ServletContext context,String key){
		genPropList(context);
		String ret = "";
		if(CoreUtils.nullSafeSize(propMap)>0 && propMap.containsKey(key)){
			ret = propMap.get(key);
		}
		return ret;
	}
	
	/**
	 * 
	 * @param key
	 * @param val
	 */
	public static void setString(String key,String val){
		if(CoreUtils.nullSafeSize(propMap)>0 && propMap.containsKey(key)){
			propMap.put(key,val);
		}
	}
}
