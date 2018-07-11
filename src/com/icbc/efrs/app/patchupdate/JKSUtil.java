package com.icbc.efrs.app.patchupdate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// 用于客户端：
// RSAPrivateKey privateKey = (RSAPrivateKey) JKSUtil.getPrivateKey(filePath, jksPass, keyAlias, aliasPass);
// String privateExponent = privateKey.getPrivateExponent().toString(16);
// String privateModulus = privateKey.getModulus().toString(16);

// RSAPublicKey publicKey = (RSAPublicKey) JKSUtil.getPublicKey(filePath, jksPass, keyAlias);
// String publicExponent = publicKey.getPublicExponent().toString(16);

// 用于服务器端：
// RSAPrivateKey privateKey = (RSAPrivateKey) JKSUtil.getPrivateKey(filePath, jksPass, keyAlias, aliasPass);
// String privateExponent = privateKey.getPrivateExponent().toString();
// String privateModulus = privateKey.getModulus().toString();

// RSAPublicKey publicKey = (RSAPublicKey) JKSUtil.getPublicKey(filePath, jksPass, keyAlias);
// String publicExponent = publicKey.getPublicExponent().toString();
// String publicModulus = "65537";


public class JKSUtil {
	
	static String keyStoreFile = "C:\\Users\\kfzx-zhaohq01\\Desktop\\test.jks";
	static String storeFilePass = "123456";
	static String keyAlias = "test1";
	static String keyAliasPass = "123456";
	
//	static String keyStoreFile = "C:\\Users\\kfzx-zhaohq01\\Desktop\\keystore2";
//	static String storeFilePass = "111111";
//	static String keyAlias = "zhaohaiqiang";
//	static String keyAliasPass = "111111";
	
	public static void main(String[] args){
		// 客户端
//		RSAPublicKey publicKey = (RSAPublicKey) getPublicKey(keyStoreFile,
//				storeFilePass, keyAlias);
//		String publicExponent = publicKey.getPublicExponent().toString(16);
//		String publicModules=publicKey.getModulus().toString(16);
//		
//		
//		
//		System.out.println("publicExponent:" + publicExponent);
//		System.out.println("publicModules:" + publicModules);
//		
//
//		RSAPrivateKey privateKey = (RSAPrivateKey)getPrivateKey(keyStoreFile, storeFilePass, keyAlias, keyAliasPass);
//		String privateExponent = privateKey.getPrivateExponent().toString();
//		String privateModulus = privateKey.getModulus().toString();
//		System.out.println("privateExponent:" + privateExponent);
//		System.out.println("privateModulus:" + privateModulus);
		
		
		 RSAPrivateKey privateKey = (RSAPrivateKey) JKSUtil.getPrivateKey(keyStoreFile, storeFilePass, keyAlias, keyAliasPass);
		 String privateExponent = privateKey.getPrivateExponent().toString();
		 String privateModulus = privateKey.getModulus().toString();
		 System.out.println("服务器端privateExponent:" + privateExponent);
		 System.out.println("服务器端privateModulus:" + privateModulus);
		 

		 RSAPublicKey publicKey = (RSAPublicKey) JKSUtil.getPublicKey(keyStoreFile, storeFilePass, keyAlias);
		 String publicExponent = publicKey.getPublicExponent().toString(16);
		 String publicModulus = publicKey.getModulus().toString(16);
		 System.out.println("给客户端publicModulus:" + publicModulus);

		
		
	}
	
	/**
	 * 得到公钥
	 * 
	 * @param keyStoreFile
	 *            私钥文件
	 * @param storeFilePass
	 *            私钥文件的密码
	 * @param keyAlias
	 *            别名
	 * @return
	 */
	public static PublicKey getPublicKey(String keyStoreFile,
			String storeFilePass, String keyAlias) {

		KeyStore ks;
		// 公钥类所对应的类
		PublicKey pubkey = null;
		try {

			// 得到实例对象
			ks = KeyStore.getInstance("JKS");
			FileInputStream fin;
			try {

				// 读取JKS文件
				fin = new FileInputStream(keyStoreFile);
				try {
					// 读取公钥
					ks.load(fin, storeFilePass.toCharArray());
					java.security.cert.Certificate cert = ks
							.getCertificate(keyAlias);
					pubkey = cert.getPublicKey();
					fin.close();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return pubkey;
	}

	/**
	 * 得到私钥
	 * 
	 * @param keyStoreFile
	 *            私钥文件
	 * @param storeFilePass
	 *            私钥文件的密码
	 * @param keyAlias
	 *            别名
	 * @param keyAliasPass
	 *            密码
	 * @return
	 */
	public static PrivateKey getPrivateKey(String keyStoreFile,
			String storeFilePass, String keyAlias, String keyAliasPass) {
		KeyStore ks;
		PrivateKey prikey = null;
		try {
			ks = KeyStore.getInstance("JKS");
			FileInputStream fin;
			try {
				fin = new FileInputStream(keyStoreFile);
				try {
					try {
						// 先打开文件
						ks.load(fin, storeFilePass.toCharArray());
						// 通过别名和密码得到私钥
						prikey = (PrivateKey) ks.getKey(keyAlias,
								keyAliasPass.toCharArray());
						fin.close();
					} catch (UnrecoverableKeyException e) {
						e.printStackTrace();
					} catch (CertificateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return prikey;
	}
}
