package com.icbc.efrs.app.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileUtil {

    /**
     * 删除文件夹
     *
     * @param path 文件夹路径
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        deleteFile(file);
    }

    /**
     * 删除文件夹
     *
     * @param file 文件夹对象
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            String[] fileList = file.list();
            for (String subPath : fileList) {
                deleteFile(file.getAbsolutePath() + File.separator + subPath);
            }
            file.delete();
        } else if (file.isFile()) {
            file.delete();
        }
    }

    /**
     * 获取文件后缀名
     *
     * @param file 文件名
     */
    public static String getFileSufix(String file) {
        int splitIndex = file.lastIndexOf(".");
        return file.substring(splitIndex + 1);
    }

    /**
     * 查找文件（遍历所有子目录）
     *
     * @param dir  目录
     * @param file 文件名
     * @return String    匹配到的文件全路径
     */
    public static String searchFile(String dir, String file) {
        String result = null;
        File folder = new File(dir);
        File[] subFiles = folder.listFiles();
        assert subFiles != null;
        for (File listfile : subFiles) {
            if (listfile.getName().equals(file)) {
                result = dir + File.separator + file;
                break;
            } else if (listfile.isDirectory()) {
                result = searchFile(listfile.getAbsolutePath(), file);
                if (CoreUtils.nullSafeSize(result) != 0) {
                    result = listfile.getAbsolutePath() + File.separator + file;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 取文件byte[]
     *
     * @param file 文件对象
     * @return byte[]        字节数组
     */
    public static byte[] getFileByteArr(File file) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream bis = null;
        byte[] content = null;
        try {
            bos = new ByteArrayOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int size = 0;
            while ((size = bis.read()) != -1) {
                bos.write(size);
            }
            content = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CoreUtils.closeStreams(bis, bos);
        }
        return content;
    }

    /**
     * 取文件byte[]
     *
     * @param is
     * @return 文件byte[]
     */
    public static byte[] getFileByteArr(InputStream is) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream bis = null;
        byte[] content = null;
        try {
            bos = new ByteArrayOutputStream();
            bis = new BufferedInputStream(is);
            int size = 0;
            while ((size = bis.read()) != -1) {
                bos.write(size);
            }
            content = bos.toByteArray();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            CoreUtils.closeStreams(bis, bos);
        }
        return content;
    }

    /**
     * 复制文件
     *
     * @param sFile 源文件
     * @param dFile 目标文件
     * @return
     */
    public static void copyFile(File sFile, File dFile) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(sFile);
            fo = new FileOutputStream(dFile);
            in = fi.getChannel();
            out = fo.getChannel();

            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CoreUtils.closeInputStream(fi);
            CoreUtils.closeChannel(in);
            CoreUtils.closeOutputStream(fo);
            CoreUtils.closeChannel(out);
        }
    }

    /**
     * @param filepath
     * @param str
     * @return
     */
    public static boolean writeFile(String filepath, String str) {
        boolean ret = false;
        FileWriter writer = null;
        try {
            writer = new FileWriter(filepath);
            writer.write(str);
            writer.flush();
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    public static void writeFileNIO(String filePath, boolean appendable, byte[] dataArr) {
        RandomAccessFile aFile = null;
        FileChannel inChannel = null;
        final ByteBuffer buf = ByteBuffer.allocate(dataArr.length);
        try {
            aFile = new RandomAccessFile(filePath, "rw");
            inChannel = aFile.getChannel();
            buf.clear();
            buf.put(dataArr);
            buf.flip();
            while (buf.hasRemaining()) {
                inChannel.write(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CoreUtils.close(inChannel);
            CoreUtils.close(aFile);
        }
    }
    
    /**
     * 读文件内容
     *
     * @param filepath filepath
     * @return 读文件内容
     */
    public static String getContent(String filepath) {
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filepath), CoreUtils.ENCODING_DEF));
            while ((line = bfr.readLine()) != null) {
                if (!line.startsWith("#")) {
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
     *
     * @param filepath filepath
     * @return 读文件内容
     */
    public static ArrayList<String> getLineArr(String filepath) {
        ArrayList<String> retList = new ArrayList<String>();
        String line;
        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filepath), CoreUtils.ENCODING_DEF));
            while ((line = bfr.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("#")) {
                    retList.add(line);
                }
            }
            bfr.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retList;
    }


}
