package com.icbc.efrs.app.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.Channel;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.icbc.efrs.app.constant.Constants;


public class CoreUtils {

    /**
     * 连续字符生成，例如in语句的连续问号生成
     *
     * @param src   连续生成的字符
     * @param count 分隔符生成次数
     * @param del   分隔符
     */
    public static String dupeString(String src, int count, String del) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count - 1; i++)
            sb.append(src).append(del);
        if (count > 0)
            sb.append(src);
        return sb.toString();
    }

    /**
     * 将List转化为字符串数组
     *
     * @param list list
     * @return 字符串数组
     */
    public static String[] list2StringArr(List<String> list) {
        if (list == null)
            return null;
        String[] ret = new String[list.size()];
        String obj;
        for (int i = 0; i < ret.length; i++) {
            obj = list.get(i);
            ret[i] = obj;
        }
        return ret;
    }

    /**
     * 将一维List构造成二维数组，List中非String元素将以空字符串填充二维数组
     *
     * @param srcList 一维List
     */
    public static String[][] conListToStrArr(List<String> srcList) {
        String[][] retArr = null;
        if (CoreUtils.nullSafeSize(srcList) > 0) {
            retArr = new String[srcList.size()][1];
            for (int i = 0; i < retArr.length; i++) {
                if (srcList.get(i) != null) {
                    retArr[i][0] = srcList.get(i);
                } else {
                    retArr[i][0] = "";
                }
            }
        }
        return retArr;
    }

    /**
     * 将二维List构造成二维数组，List中非String元素将以空字符串填充二维数组，请确保该二维List为工整List(各行宽度size一致)
     *
     * @param srcList 二维List
     */
    public static String[][] con2DListToStrArr(ArrayList<ArrayList<String>> srcList) {
        String[][] retArr = null;
        if (CoreUtils.nullSafeSize(srcList) > 0
                && CoreUtils.nullSafeSize(srcList.get(0)) > 0) {
            retArr = new String[srcList.size()][(srcList.get(0)).size()];
            for (int j = 0; j < srcList.size(); j++) {
                ArrayList<String> srcListChild = srcList.get(j);
                for (int i = 0; i < srcListChild.size(); i++) {
                    if (srcListChild.get(i) != null) {
                        retArr[j][i] = srcListChild.get(i);
                    } else {
                        retArr[j][i] = "";
                    }
                }
            }
        }
        return retArr;
    }

    /**
     * 返回集合大小
     *
     * @param arr 要计算大小的数组
     * @return 集合大小。
     */
    public static int nullSafeSize(Object[] arr) {
        if (arr == null)
            return 0;
        return arr.length;
    }

    /**
     * 将由Map组成的一维List重新组装，返回二维List
     *
     * @param mapList 由Map组成的一维List
     * @return 二维List。可用于TurnPageDao.getAllData()后的结果
     */
    public static ArrayList<ArrayList<String>> convertMapList(List<Map<String, String>> mapList) {
        ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
        if (CoreUtils.nullSafeSize(mapList) > 0) {
            for (Map<String, String> aMapList : mapList) {
                ArrayList<String> tempList = new ArrayList<String>();
                Map<String, String> tempMap = aMapList;
                if (CoreUtils.nullSafeSize(tempMap) > 0) {
                    for (Entry<String, String> entry : tempMap.entrySet()) {
                        String val = String.valueOf(entry.getValue());
                        tempList.add(val);
                    }
                }
                retList.add(tempList);
            }
        }
        return retList;
    }

    /**
     * 返回一个新的List，它是原List的一个子List(通过列号指定)。
     *
     * @param list      原二维List
     * @param col_index 列索引号的数组
     */
    public static ArrayList<ArrayList<String>> subColList(ArrayList<ArrayList<String>> list, int[] col_index) {
        ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
        for (ArrayList<String> aList : list) {
            ArrayList<String> tempList = new ArrayList<String>();
            if (CoreUtils.nullSafeSize(aList) > 0) {
                for (int aCol_index : col_index) {
                    tempList.add(aList.get(aCol_index));
                }
            }
            retList.add(tempList);
        }
        return retList;
    }

    /**
     * 将一个二维List中的某一列转换为一维List。
     *
     * @param list      原二维List
     * @param col_index 列索引号
     */
    public static ArrayList<String> subSingleColList(ArrayList<ArrayList<String>> list, int col_index) {
        ArrayList<String> retList = new ArrayList<String>();
        for (ArrayList<String> tmp : list) {
            if (CoreUtils.nullSafeSize(tmp) > 0) {
                retList.add(tmp.get(col_index));
            }
        }
        return retList;
    }

    /**
     * 返回一个新的array，它是原array的一个子array(通过列号指定)；索引号从0开始计数； 序号越界(超出序号范围)，则跳过。
     *
     * @param arr       原array
     * @param col_index 列索引号的数组
     */
    public static String[] subColArr(String[] arr, int[] col_index) {
        if (nullSafeSize(arr) == 0 || col_index == null
                || col_index.length == 0) {
            return arr;
        }
        List<String> retList = new ArrayList<String>();
        for (int index : col_index) {
            if (index > arr.length - 1 || index < 0) {// 越界(超出序号范围)，则跳过
                continue;
            }
            String tempObj = arr[index];
            retList.add(tempObj);
        }
        return retList.toArray(new String[retList.size()]);
    }

    /**
     * 返回一个新的array，它是原array的一个子array(通过列号指定)；索引号从0开始计数；
     * 若s_col_index越界(超出序号范围)或length小等于0，则返回原数组； 若length超过能返回的最大长度，则返回到array的末尾。
     *
     * @param arr         原array
     * @param s_col_index 开始的列索引号
     * @param length      返回的结果集长度
     */
    public static String[] subColArr(String[] arr, int s_col_index, int length) {
        int size = nullSafeSize(arr);
        if (length == 0) {
            return new String[]{};
        }
        if (size == 0 || length <= 0 || s_col_index > size - 1
                || s_col_index < 0) {
            return arr;
        }
        List<String> retList = new ArrayList<String>();
        int len = (s_col_index + length) > size ? size : length;
        retList.addAll(Arrays.asList(arr).subList(s_col_index, len));
        return retList.toArray(new String[retList.size()]);
    }

    /**
     * 返回字符串(字节)长度
     *
     * @param str 要计算大小的字符串
     * @return 字节大小。
     */
    public static int nullSafeSize(String str) {
        if (str == null)
            return 0;
        Pattern pattern = Pattern.compile("[^\\x00-\\xff]");
        Matcher matcher = pattern.matcher(str);
        String s = matcher.replaceAll("xx");
        return s.length();
    }

    /**
     * 计算日期间隔天数
     *
     * @param fromDate 开始日期
     * @param toDate   结束日期
     * @return 间隔天数。
     */
    public static int compareToDay(String fromDate, String toDate) {
        long days = 24L * 60L * 60L * 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        int result = 0;
        try {
            Date fromD = sdf.parse(fromDate);
            Date toD = sdf.parse(toDate);
            result = (int) ((fromD.getTime() - toD.getTime()) / days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将列表转换为使用指定分隔符分隔的字符串
     *
     * @param list 要转换的列表
     * @param del  分隔符
     * @return 将列表转换为使用指定分隔符分隔的字符串
     */
    public static String listToDelString(List<Object> list, String del) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : list) {
            sb.append(String.valueOf(obj)).append(del);
        }
        if (sb.length() > 0)
            sb.delete(sb.length() - del.length(), sb.length());
        return sb.toString();
    }

    /**
     * 将二维列表转换为使用指定分隔符分隔的字符串
     *
     * @param doubleList 要转换的列表
     * @param subDel     子节点分隔符
     * @param del        节点分隔符
     * @return 字符串
     */
    public static String doubleListToDelString(
            ArrayList<ArrayList<String>> doubleList, String subDel, String del) {
        StringBuilder sb = new StringBuilder();
        for (List<String> list : doubleList) {
            for (String string : list) {
                sb.append(string).append(subDel);
            }
            if (sb.length() > 0)
                sb.delete(sb.length() - subDel.length(), sb.length());
            sb.append(del);
        }
        if (sb.length() > 0)
            sb.delete(sb.length() - del.length(), sb.length());
        return sb.toString();
    }

    /**
     * 将使用指定分隔符分隔的字符串转换为二维列表
     *
     * @param str    要转换的string
     * @param subDel 二级分隔符
     * @param del    一级分隔符
     * @return 二维列表
     */
    public static ArrayList<ArrayList<String>> delStringToDoubleList(String str, String subDel, String del) {
        ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
        String[] arr = StrUtils.split(str, del);
        for (String string : arr) {
            String[] subArr = StrUtils.split(string, subDel);
            ArrayList<String> tempAL = new ArrayList<String>();
            Collections.addAll(tempAL, subArr);
            retList.add(tempAL);
        }
        return retList;
    }

    /**
     * 将二维列表转换为LinkedHashMap
     *
     * @param doubleList 要转换的列表
     * @return 将二维列表转换为LinkedHashMap
     */
    public static LinkedHashMap<String, String> doubleListToLHM(
            ArrayList<ArrayList<String>> doubleList) {
        LinkedHashMap<String, String> retLHM = new LinkedHashMap<String, String>();
        for (List<String> list : doubleList) {
            int size = list.size();
            if (size >= 2) {
                String key = list.get(0);
                String val = list.get(1);
                retLHM.put(key, val);
            }
        }
        return retLHM;
    }

    /**
     * 将二维String数组转换为使用指定分隔符分隔的字符串
     *
     * @param doubleArr 要转换的二维String数组
     * @param subDel    子节点分隔符
     * @param del       节点分隔符
     * @return 将二维String数组转换为使用指定分隔符分隔的字符串
     */
    public static String doubleStrArrToDelString(String[][] doubleArr,
                                                 String subDel, String del) {
        StringBuilder sb = new StringBuilder();
        for (String[] arr : doubleArr) {
            for (String string : arr) {
                sb.append(string).append(subDel);
            }
            if (sb.length() > 0)
                sb.delete(sb.length() - subDel.length(), sb.length());
            sb.append(del);
        }
        if (sb.length() > 0)
            sb.delete(sb.length() - del.length(), sb.length());
        return sb.toString();
    }

    /**
     * 将Set转换为使用指定分隔符分隔的字符串
     *
     * @param set 要转换的set
     * @param del 分隔符
     * @return 将Set转换为使用指定分隔符分隔的字符串
     */
    public static String setToDelString(Set<Object> set, String del) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Object> iterator = set.iterator(); iterator.hasNext(); ) {
            String zbKey = String.valueOf(iterator.next());
            sb.append(zbKey).append(del);
        }
        if (sb.length() > 0)
            sb.delete(sb.length() - del.length(), sb.length());
        return sb.toString();
    }

    /**
     * 以指定分隔符将一维字符串数组构造成字符串
     *
     * @param srcStrArr 要转换的字符串数组
     * @param del       指定分隔符
     */
    public static String conStrArrToStr(String[] srcStrArr, String del) {
        StringBuilder sb = new StringBuilder();
        for (String str : srcStrArr) {
            sb.append(str).append(del);
        }
        if (sb.length() > 0)
            sb.delete(sb.length() - del.length(), sb.length());
        return sb.toString();
    }

    /**
     * 以指定分隔符将map构造成字符串(key1=val1[del]key2=val2[del]...keyN=valN)
     *
     * @param srcStrMap 要转换的map
     * @param del       指定分隔符
     */
    public static String conStrMapToStr(Map<String, String> srcStrMap,
                                        String del) {
        StringBuffer sb = new StringBuffer();
        for (Entry<String, String> entry : srcStrMap.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            sb.append(key).append("=").append(val).append(del);
        }
        if (sb.length() > 0)
            sb.delete(sb.length() - del.length(), sb.length());
        return sb.toString();
    }

    /**
     * 将使用指定分隔符分隔的字符串转换为列表
     *
     * @param str 要转换的字符串
     * @param del 分隔符
     * @return ArrayList
     */
    public static ArrayList<String> delStringToList(String str, String del) {
        return StrUtils.disSymbolString(del, str);
    }

    /**
     * 将使用指定分隔符分隔的字符串转换为LinkedHashMap(key为子项序号,value为原字符串的子项) 要转换的字符串
     *
     * @param del 分隔符
     * @return LinkedHashMap
     */
    public static LinkedHashMap<String, String> delStringToLHM(String str, String del) {
        LinkedHashMap<String, String> retMap = new LinkedHashMap<String, String>();
        String[] arr = StrUtils.split(str, del);
        for (int i = 0; i < arr.length; i++) {
            String string = arr[i];
            retMap.put(i + "", string);
        }
        return retMap;
    }

    /**
     * 将列表中重复记录去除
     *
     * @param list 源列表
     * @return 结果列表
     */
    public static List<Object> genListNoRepeat(List<Object> list) {
        Set<Object> set = new LinkedHashSet<Object>(list);
        return new ArrayList<Object>(set);
    }

    /**
     * 将字符串数组中重复记录去除
     *
     * @param arr 源字符串数组
     * @return 结果列表
     */
    public static String[] genStrArrayNoRepeat(String[] arr) {
        Set<String> set = new LinkedHashSet<String>();
        for (String anArr : arr) {
            String string = String.valueOf(anArr);
            set.add(string);
        }
        return set.toArray(new String[set.size()]);
    }

    /**
     * 将以指定分隔符分隔的指定String中的各项匹配指定Map，返回匹配的结果，以原分隔符分隔
     *
     * @param toMap 要匹配的Map
     * @param str   指定的String，以del分隔
     * @param del   分隔符
     * @return 以原分隔符分隔的匹配结果
     */
    public static String containsKeyStr(Map<String, ?> toMap, String str, String del) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> list = delStringToList(str, del);// 将使用指定分隔符分隔的字符串转换为列表
        for (String element : list) {
            if (!toMap.containsKey(element)) {
                sb.append(element).append(del);
            }
        }
        if (sb.length() > 0)
            sb.delete(sb.length() - del.length(), sb.length());
        return sb.toString();
    }

    /**
     * 指定分隔符分隔的字符串中各项匹配Map中的值(value)，将匹配的Map项组合成新Map返回
     *
     * @param toMap  要匹配的Map
     * @param valStr 指定的String，以del分隔
     * @param del    分隔符
     * @return 以原分隔符分隔的匹配结果
     */
    public static LinkedHashMap<String, String> mapMatchByValue(Map<String, String> toMap, String valStr,
                                                                String del) {
        LinkedHashMap<String, String> retMap = new LinkedHashMap<String, String>();
        if (nullSafeSize(toMap) > 0) {
            String[] strArr = StrUtils.split(valStr, del);
            for (Entry<String, String> entry : toMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                for (String string : strArr) {
                    if (value.equals(string)) {// 与值(value)匹配
                        retMap.put(key, value);
                        break;
                    }
                }
            }
        }
        return retMap;
    }

    /**
     * 比较二个Map，将匹配项去除后，组合成新Map返回
     *
     * @param srcMap 匹配源Map
     * @param desMap 匹配目标Map
     * @return 匹配去除后的新Map
     */
    public static Map<Object, Object> removeAllMap(Map<Object, Object> srcMap, Map<Object, Object> desMap) {
        Map<Object, Object> retMap = new HashMap<Object, Object>(srcMap);
        retMap.keySet().removeAll(desMap.keySet());
        return retMap;
    }

    /**
     * 比较二个Map，将匹配项去除后，组合成新Map返回
     *
     * @param srcMap 匹配源Map(LinkedHashMap)
     * @param desMap 匹配目标Map
     * @return 匹配去除后的新Map(LinkedHashMap)
     */
    public static LinkedHashMap<Object, Object> removeAllMapForLHM(LinkedHashMap<Object, Object> srcMap,
                                                                   Map<Object, Object> desMap) {
        LinkedHashMap<Object, Object> retMap = new LinkedHashMap<Object, Object>(srcMap);
        retMap.keySet().removeAll(desMap.keySet());
        return retMap;
    }

    /**
     * 根据指定索引号，返回String子串（中文计算为2个字符长度）。
     * <p>
     * 前闭后开，例：
     * <p>
     * substring("01上4研7开0123",2,8)&nbsp;&nbsp;&nbsp;&nbsp;"上4研7"
     * <p>
     * substring("01上4研7开0123",1,8)&nbsp;&nbsp;&nbsp;&nbsp;"上4研7"
     * <p>
     * substring("01上4研7开0123",2,9)&nbsp;&nbsp;&nbsp;&nbsp;"上4研7开"
     * <p>
     * substring("01上4研7开0123",2,3)&nbsp;&nbsp;&nbsp;&nbsp;"上"
     * <p>
     *
     * @param src        源字符串
     * @param beginIndex 开始索引
     * @param endIndex   结束索引
     * @return String子串（前闭后开）
     */
    public static String substring(String src, int beginIndex, int endIndex) {
        int count = nullSafeSize(src);
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex >= count) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        if (beginIndex >= endIndex) {
            throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
        }
        StringBuilder sb = new StringBuilder();
        char[] charArr = src.toCharArray();
        Pattern pattern = Pattern.compile("[^\\x00-\\xff]");
        int j = 0;
        for (int i = 0; i < charArr.length; i++, j++) {
            char c = charArr[i];
            Matcher matcher = pattern.matcher(c + "");
            if (matcher.matches()) {
                j++;
            }
            if (j >= beginIndex && j <= endIndex) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 将一维List中String转换(以del分隔)为List,取出其中的第index列
     *
     * @param list  源List(子元素为以del分隔的String)
     * @param del   分隔符
     * @param index 索引号
     * @param flag  非String元素是否保留原样;true保留,false则以空字符串代替
     * @return 转换后的新List(子元素由源List的第index列组成)
     */
    public static List<Object> convertSimpleList(List<Object> list, String del, int index,
                                                 boolean flag) {
        List<Object> retList = new ArrayList<Object>();
        for (Object aList : list) {
            if (aList instanceof String) {
                String element = String.valueOf(aList);
                String[] params = StrUtils.split(element, del);
                retList.add(params[index]);
            } else {
                if (flag) {
                    retList.add(aList);
                } else {
                    retList.add("");
                }
            }
        }
        return retList;
    }

    /**
     * 将一维List中String转换(以del分隔)为List,取出其中的第index列
     *
     * @param list  源List(子元素为以del分隔的String)
     * @param del   分隔符
     * @param index 索引号数组
     * @param flag  非String元素是否保留原样;true保留(第一个为该值,其余为空字符串),false则以空字符串代替
     * @return 转换后的新List(子元素由源List的第index[]列组成)
     */
    public static ArrayList<ArrayList<String>> convertSub2DList(List<String> list, String del, int[] index,
                                                                boolean flag) {
        ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
        for (String aList : list) {
            ArrayList<String> tempList = new ArrayList<String>();
            if (aList != null) {
                String element = aList;
                String[] params = StrUtils.split(element, del);
                for (int anIndex : index) {
                    if (params.length >= anIndex + 1 && anIndex >= 0) {// 存在性判断
                        tempList.add(params[anIndex]);
                    }
                }
            } else {
                if (flag) {
                    tempList.add(aList);
                    for (int j = 1; j < index.length; j++) {
                        tempList.add("");
                    }
                } else {
                    for (int anIndex : index) {
                        tempList.add("");
                    }
                }
            }
            retList.add(tempList);
        }
        return retList;
    }

    /**
     * 将二维List中指定编号元素位置互换
     *
     * @param list   源List
     * @param srcPos 源列索引号
     * @param toPos  目标列索引号
     * @return 转换后的新List
     */
    public static ArrayList<ArrayList<String>> swapList(ArrayList<ArrayList<String>> list, int srcPos, int toPos) {
        ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
        for (ArrayList<String> element : list) {// 复制(二维)
            ArrayList<String> temp = new ArrayList<String>();
            temp.addAll(element);
            retList.add(temp);
        }
        for (ArrayList<String> element : retList) {
            Collections.swap(element, srcPos, toPos);
        }
        return retList;
    }

    /**
     * 关闭输入及输出流
     *
     * @param is 输入流
     * @param os 输出流
     */
    public static void closeStreams(InputStream is, OutputStream os) {
        closeInputStream(is);
        closeOutputStream(os);
    }

    /**
     * 关闭输入流
     *
     * @param s 输入流
     */
    public static void closeInputStream(InputStream s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param s 输出流
     */
    public static void closeOutputStream(OutputStream s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeReader(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeWriter(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 转换99991231日期格式(日期值与日期类型不做匹配校验，务必请自行保证；可用于由日期控件生成的日期值与日期类型)
     *
     * @param desDateType 日期类型 "0","日报"；"1","旬报"；"2","月报"；"3","季报"；"4","半年报"；"5","年报"
     */
    public static List<String> parseLastDate(String desDateType) {
        String desDate = "99991231";
        String desDateLabel = "9999年12月31日";
        // "0","日报"；"1","旬报"；"2","月报"；"3","季报"；"4","半年报"；"5","年报"
        String year = desDate.substring(0, 4);
        String month = desDate.substring(4, 6);
        String date = desDate.substring(6, 8);
        String temp;
        String dayStr;
        switch (new Integer(desDateType)) {
            case 1:
                if (new Integer(date) <= 10) {
                    temp = "1";
                    dayStr = "上";
                } else if (new Integer(date) <= 20) {
                    temp = "2";
                    dayStr = "中";
                } else {
                    temp = "3";
                    dayStr = "下";
                }
                desDate = year + month + temp;
                desDateLabel = year + "年" + month + "月" + dayStr + "旬";
                break;
            case 2:
                desDate = year + month;
                desDateLabel = year + "年" + month + "月";
                break;
            case 3:
                if (new Integer(month) <= 3) {
                    temp = "1";
                    dayStr = "第一";
                } else if (new Integer(month) <= 6) {
                    temp = "2";
                    dayStr = "第二";
                } else if (new Integer(month).intValue() <= 9) {
                    temp = "3";
                    dayStr = "第三";
                } else {
                    temp = "4";
                    dayStr = "第四";
                }
                desDate = year + temp;
                desDateLabel = year + "年" + dayStr + "季度";
                break;
            case 4:
                if (new Integer(month) <= 6) {
                    temp = "5";
                    dayStr = "上";
                } else {
                    temp = "6";
                    dayStr = "下";
                }
                desDate = year + temp;
                desDateLabel = year + "年" + dayStr + "半年";
                break;
            case 5:
                desDate = year;
                desDateLabel = year + "年";
                break;
        }
        List<String> retList = new ArrayList<String>();
        retList.add(desDate);
        retList.add(desDateLabel);
        return retList;
    }

    /**
     * 转换日期值到日期标签值(日期值与日期类型不做匹配校验，务必请自行保证；可用于由日期控件生成的日期值与日期类型)
     *
     * @param dateVal  日期值
     * @param dateType 日期类型 "0","日报"；"1","旬报"；"2","月报"；"3","季报"；"4","半年报"；"5","年报"
     */
    public static String parseDateValueToLabel(String dateVal, String dateType) {
        String desDateLabel = "";
        // "0","日报"；"1","旬报"；"2","月报"；"3","季报"；"4","半年报"；"5","年报"
        StringBuffer sb = new StringBuffer();
        String year = dateVal.substring(0, 4);
        String month = "";
        String dayStr = "";
        switch (new Integer(dateType)) {
            case 0:
                desDateLabel = sb.append(dateVal.substring(0, 4)).append("年")
                        .append(dateVal.substring(4, 6)).append("月").append(
                                dateVal.substring(6, 8)).append("日").toString();
                break;
            case 1:
                String tendays = dateVal.substring(6, 7);// 旬
                month = dateVal.substring(4, 6);
                if ("1".equals(tendays)) {
                    dayStr = "上";
                } else if ("2".equals(tendays)) {
                    dayStr = "中";
                } else {
                    dayStr = "下";
                }
                desDateLabel = year + "年" + month + "月" + dayStr + "旬";
                break;
            case 2:
                month = dateVal.substring(4, 6);
                desDateLabel = year + "年" + month + "月";
                break;
            case 3:
                String season = dateVal.substring(4, 5);// 季
                if ("1".equals(season)) {
                    dayStr = "第一";
                } else if ("2".equals(season)) {
                    dayStr = "第二";
                } else if ("3".equals(season)) {
                    dayStr = "第三";
                } else {
                    dayStr = "第四";
                }
                desDateLabel = year + "年" + dayStr + "季度";
                break;
            case 4:
                String halfyear = dateVal.substring(4, 5);// 半年
                if ("5".equals(halfyear)) {
                    dayStr = "上";
                } else {
                    dayStr = "下";
                }
                desDateLabel = year + "年" + dayStr + "半年";
                break;
            case 5:
                desDateLabel = year + "年";
                break;
        }
        return desDateLabel;
    }

    /**
     * 以paramX_xxx格式对重复字段起别名。如列表为[aaa,bbb,aaa]，则返回[aaa param1_aaa,bbb,aaa
     * param2_aaa]
     *
     * @param fieldlist 包含重复字段的列表
     * @return 起完别名的字段列表。
     */
    public static List<String> getAlias(List<String> fieldlist) {
        if (fieldlist == null || fieldlist.size() == 0)
            return fieldlist;
        Set<String> tempSet = new HashSet<String>();
        tempSet.addAll(fieldlist);
        List<String> tempList = new ArrayList<String>(fieldlist);
        for (String tempStr : tempSet) {
            int i = 0;
            int j = 1;
            if (tempStr != null
                    && tempStr.trim().length() != 0
                    && tempList.lastIndexOf(tempStr) != tempList
                    .indexOf(tempStr)) {// 字段出现多次
                for (String tempField : tempList) {
                    if (tempStr.equals(tempField)) {
                        tempList.set(i, tempList.get(i) + " param" + j + "_"
                                + tempList.get(i));
                        j++;
                    }
                    i++;
                }
            }
        }
        return tempList;
    }

    /**
     * 检查2个数组间是否存在 同序号 元素同时为空/空串或同时非空的情况
     *
     * @param arr0 String数组0
     * @param arr1 String数组1
     * @return 仅当2个数组间同序号元素不同时为空/空串或者不同时非空返回true，否则返回false。
     */
    public static boolean checkDiff(String[] arr0, String[] arr1) {
        if (arr0 == null ^ arr1 == null || arr0 == null || arr0.length != arr1.length)
            return true;
        for (int i = 0; i < arr0.length; i++) {
            if ((nullSafeSize(arr0[i]) > 0) ^ (nullSafeSize(arr1[i]) > 0)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算字符串出现次数
     *
     * @param str str
     * @param sub sub
     * @return 次数
     */
    public static int countOccurrencesOf(String str, String sub) {
        int idx;
        if ((str == null) || (sub == null) || (str.length() == 0) || (sub.length() == 0)) {
            return 0;
        }
        int count = 0;
        int pos = 0;

        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    /**
     * 计算问号在字符串中出现的次数
     *
     * @param toString 被比较的字符串
     * @return 问号出现的次数[int]。
     */
    public static int countAsk(String toString) {
        return countOccurrencesOf(toString, "?");
    }

    /**
     * 匹配4种非等值操作符,LowScopeField/HighScopeField/LowScopeFieldOpen/
     * HighScopeFieldOpen分别匹配为"<=" / ">=" / "<" / ">"
     *
     * @param str 被匹配的字符串
     * @return 替换后的字符串。
     */
    public static String getOperation(String str) {
        if (("LowScopeField").equalsIgnoreCase(str)) {
            return "<=";
        } else if (("HighScopeField").equalsIgnoreCase(str)) {
            return ">=";
        } else if (("LowScopeFieldOpen").equalsIgnoreCase(str)) {
            return "<";
        } else if (("HighScopeFieldOpen").equalsIgnoreCase(str)) {
            return ">";
        }
        return null;
    }

    /**
     * 去除字符串中对应位置参数为空的子串,返回重新拼接的sql串。如"xxx=? and yyy=?"中yyy对应绑定变量的值为空则返回"xxx=?"，
     * 不返回" and yyy=?"
     *
     * @param str       要比较的字符串
     * @param paramsArr 被比较的参数数组
     * @return 去除字符串中对应位置参数为空的子串, 返回重新拼接的sql串。
     */
    public static String removeNullParams(String str, String[] paramsArr) {
        if (nullSafeSize(str) == 0 || paramsArr == null
                || !(str.contains("?")))
            return str;
        // 判断位置
        // 检查无param的段内是否包含超过1个'and',有说明该段内包含多个段，去除该段内最后一段（最后出现的'and'）；无则直接去除整个段
        String tempArr[] = str.split("\\?");
        int p = 0;
        int temp = 0;
        StringBuilder toStr = new StringBuilder();
        for (int i = 0; i < paramsArr.length; i++) {// 遍历问号结尾的段
            p += tempArr[i].length() + 1;
            String tempStr = str.substring(temp, p);
            if (nullSafeSize(paramsArr[i]) == 0) {// 参数为空值 //若为"   "也不拼进SQL
                if (i == 0 && tempStr.matches(".*and.*")) {// 首段判断包含1个及以上and情况
                    toStr.append(tempStr.substring(0, tempStr.lastIndexOf("and")));
                }
                if (tempStr.matches(".*and.*and.*")) {// 非首段判断包含2个及以上and情况
                    toStr.append(tempStr.substring(0, tempStr.lastIndexOf("and")));
                }
            } else {
                toStr.append(tempStr);
            }
            temp = p;
        }
        if (paramsArr.length < tempArr.length) {// 末段不以问号结尾
            toStr.append(str.substring(p, str.length()));
        }
        if (toStr.toString().matches("\\s*and.*")) {// 去处toStr开始部分的and
            toStr = new StringBuilder(toStr.substring(toStr.indexOf("and") + 3, toStr.length()));
        }
        return toStr.toString();
    }

    /**
     * 返回非空元素索引号，以逗号分隔；若无非空元素，返回""
     *
     * @param paramsArr 被比较的数组
     * @return 非空元素索引号。
     */
    public static String getNonIndex(String[] paramsArr) {
        StringBuilder strIndex = new StringBuilder();
        if (paramsArr == null || paramsArr.length == 0)
            return "";
        for (int i = 0; i < paramsArr.length; i++) {
            if (nullSafeSize(paramsArr[i]) > 0)
                strIndex.append(i).append(",");
        }
        if (strIndex.length() > 0) {
            strIndex = new StringBuilder(strIndex.substring(0, strIndex.length() - 1));
        }
        return strIndex.toString();
    }

    /**
     * 返回一个新的字符串数组，它是原字符串数组的一个子字符串数组。
     *
     * @param arr        原字符串
     * @param beginIndex 开始处的索引（包括）
     * @param endIndex   结束处的索引（包括）
     * @return 一个新的字符串数组，它是原字符串数组的一个子字符串数组。
     */
    public static String[] subArr(String[] arr, int beginIndex, int endIndex) {
        int count = arr.length - 1;
        if (beginIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > count) {
            throw new ArrayIndexOutOfBoundsException(endIndex);
        }
        if (beginIndex > endIndex) {
            throw new ArrayIndexOutOfBoundsException(endIndex - beginIndex);
        }
        if ((beginIndex == 0) && (endIndex == count))
            return arr;
        String[] toArr = new String[endIndex - beginIndex + 1];
        for (int i = 0, j = 0; i < arr.length; i++) {
            if (i >= beginIndex && i <= endIndex) {
                toArr[j] = arr[i];
                j++;
            }
        }
        return toArr;
    }

    /**
     * 返回集合的总大小
     *
     * @param arr 要计算大小的字符串数组
     * @return 集合大小。
     */
    public static int nullSafeSizeCount(String[] arr) {
        if (arr == null)
            return 0;
        int count = 0;
        for (String str : arr) {
            count += nullSafeSize(str);
        }
        return count;
    }

    /**
     * 返回集合的最小元素的大小
     *
     * @param arr 要计算大小的字符串数组
     * @return 集合大小。
     */
    public static int nullSafeSizeMin(String[] arr) {
        if (arr == null)
            return 0;
        if (arr.length > 0) {
            int ret = nullSafeSize(arr[arr.length - 1]);
            for (String str : arr) {
                int temp = nullSafeSize(str);
                if (temp < ret) {
                    ret = temp;
                }
            }
            return ret;
        }
        return -1;
    }

    /**
     * 返回集合的最小元素的大小
     *
     * @param arr 要计算大小的字符串数组
     * @return 集合大小。
     */
    public static int nullSafeSizeMax(String[] arr) {
        if (arr == null)
            return 0;
        if (arr.length > 0) {
            int ret = nullSafeSize(arr[arr.length - 1]);
            for (String str : arr) {
                int temp = nullSafeSize(str);
                if (temp > ret) {
                    ret = temp;
                }
            }
            return ret;
        }
        return -1;
    }

    /**
     * 返回集合大小
     *
     * @param collection 要计算大小的集合
     * @return 集合大小。
     */
    public static int nullSafeSize(Collection<?> collection) {
        if (collection == null)
            return 0;
        return collection.size();
    }

    /**
     * 返回Map大小
     *
     * @param map 要计算大小的Map
     * @return Map大小。
     */
    public static int nullSafeSize(Map<?, ?> map) {
        if (map == null)
            return 0;
        return map.size();
    }

    /**
     * 移除此字符串末尾的指定字符串，若此序列的字符串不以指定字符串结尾，返回该字符串
     *
     * @param str       原字符串
     * @param strToTrim 要移除的字符串
     * @return 移除此字符串末尾的指定字符串，若此序列的字符串不以指定字符串结尾，返回该字符串。
     */
    public static String trimTrailing(String str, String strToTrim) {
        if (nullSafeSize(str) == 0 || nullSafeSize(strToTrim) == 0
                || !str.endsWith(strToTrim))
            return str;
        StringBuilder buf = new StringBuilder(str);
        return buf.delete(buf.length() - strToTrim.length(), buf.length())
                .toString();
    }

    /**
     * 返回指定List(嵌套map)的伪hashcode。
     *
     * @param list 原list
     * @return 返回指定List的伪hashcode。
     */
    public static int genHash(List<Object> list) {
        final int prime = 31;
        int result = 1;
        if (nullSafeSize(list) != 0) {
            for (Object obj : list) {
                if (obj instanceof Map) {
                    for (Object o : ((Map) obj).entrySet()) {
                        Entry entry = (Entry) o;
                        String key = (String) (entry.getKey() == null ? ""
                                : entry.getKey());
                        String value = (String) (entry.getValue() == null ? ""
                                : entry.getValue());
                        result = prime * result + key.hashCode();
                        result = prime * result + value.hashCode();
                    }
                } else {
                    result = prime * result
                            + ((obj == null) ? 0 : obj.hashCode());
                }
            }
        }
        return result;
    }

    /**
     * 返回指定Map的伪hashcode。
     *
     * @param map 原map
     * @return 返回指定Map的伪hashcode。
     */
    public static int genHash(Map<String, Object> map) {// java内部：一旦Map中元素的key的hashcode与value的hashcode相等，则该元素的hashcode返回0
        final int prime = 31;
        int result = 1;
        if (nullSafeSize(map) != 0) {
            for (Entry<String, Object> entry : map.entrySet()) {
                String key = (String) (entry.getKey() == null ? "" : entry
                        .getKey());
                String value = (String) (entry.getValue() == null ? "" : entry
                        .getValue());
                result = prime * result + key.hashCode();
                result = prime * result + value.hashCode();
            }
        } else {
            result = prime * result + ((map == null) ? 0 : map.hashCode());
        }
        return result;
    }

    /**
     * 返回参照数组与目标数组相同元素的（参照数组的）index。
     * <p>
     * 目标数组中出现参照数组中未出现的值 或者 目标数组元素个数大于参照数组元素个数 抛异常
     * </p>
     *
     * @param arr0 参照数组
     * @param arr1 目标数组
     */
    public static String[] getSameElementIndex(String[] arr0, String[] arr1) {
        List<String> indexList = new ArrayList<String>();
        if (arr0 == null || arr0.length == 0)
            return null;
        if (arr1 == null || arr1.length == 0) {
            for (int i = 0; i < arr0.length; i++) {
                indexList.add(String.valueOf(i));
            }
        } else if (arr0.length < arr1.length) {
            throw new RuntimeException("参照数组与目标数组元素个数异常");
        } else {
            for (int i = 0; i < arr0.length; i++) {
                for (String anArr1 : arr1) {
                    if (arr0[i].equalsIgnoreCase(anArr1)) {
                        indexList.add(String.valueOf(i));
                        break;
                    }
                }
            }
        }
        return (String[]) indexList.toArray(new String[indexList.size()]);
    }

    /**
     * 返回一个新的List，它是原List的一个子List。
     *
     * @param list  原List
     * @param index 索引号的数组
     * @return List
     */
    public static List<Object> subList(List<Object> list, String[] index) {
        if (nullSafeSize(list) == 0 || index == null || index.length == 0) {
            return list;
        }
        List<Object> tempList = new ArrayList<Object>();
        for (String anIndex : index) {
            int i = 0;
            for (Object obj : list) {
                if (anIndex.equals(String.valueOf(i)))
                    tempList.add(obj);
                i++;
            }
        }
        return tempList;
    }

    /**
     * 获取子List的所在索引号(数组)
     *
     * @param wholeList 目标List/父List
     * @param subList   源List/子List
     * @return int[] 索引号(数组)
     */
    public static int[] getFieldIndex(List<Object> wholeList, List<Object> subList) {
        int[] index = new int[subList.size()];
        for (int i = 0; i < subList.size(); i++) {
            String field = (String) subList.get(i);
            index[i] = wholeList.indexOf(field);
        }
        return index;
    }

    /**
     * 获取索引号(数组)所指定的子List
     *
     * @param list  父List
     * @param index 索引号(数组)
     * @return List
     */
    public static List<?> subList(List<?> list, int[] index) {
        if (nullSafeSize(list) == 0 || index == null || index.length == 0) {
            return list;
        }
        List<Object> tempList = new ArrayList<Object>();
        for (int anIndex : index) {
            int i = 0;
            for (Object obj : list) {
                if (anIndex == i)
                    tempList.add(obj);
                i++;
            }
        }
        return tempList;
    }

    /**
     * 将Map的各个value组合成String数组
     *
     * @param map map
     * @return 合成后的String数组
     */
    public static String[] convertMapVal2StrArr(
            LinkedHashMap<String, String> map) {
        if (map == null)
            return null;
        String[] strArr = new String[map.keySet().size()];
        int i = 0;
        for (String s : map.keySet()) {
            String key = (String) (s);
            strArr[i] = map.get(key);
            i++;
        }
        return strArr;
    }

    /**
     * @param map 源LinkedHashMap
     * @return 返回LinkedHashMap的首个元素的值
     */
    public static Object getLHMFirstVal(LinkedHashMap<Object, Object> map) {
        if (map == null)
            return null;
        Iterator<Object> itor = map.keySet().iterator();
        if (itor.hasNext()) {
            Object key = itor.next();
            return map.get(key);
        } else {
            return null;
        }
    }

    /**
     * @param map 源LinkedHashMap
     * @return 返回LinkedHashMap的首个元素的key
     */
    public static String getLHMFirstKey(LinkedHashMap<String, ?> map) {
        if (map == null)
            return null;
        Iterator<String> itor = map.keySet().iterator();
        if (itor.hasNext()) {
            return itor.next();
        } else {
            return null;
        }
    }

    /**
     * 根据键数组，抽取源MAP中对应相同键的序列，组成子MAP并返回
     *
     * @param map    源MAP
     * @param keyArr key数组
     * @return 根据键数组，抽取源MAP中对应相同键的序列，组成子MAP并返回
     */
    public static LinkedHashMap<?, ?> getSubLHMByKeys(LinkedHashMap<?, ?> map,
                                                      Object[] keyArr) {
        LinkedHashMap<Object, Object> retMap = new LinkedHashMap<Object, Object>();
        for (Object key : keyArr) {
            retMap.put(key, map.get(key));
        }
        return retMap;
    }

    /**
     * 根据值，抽取源MAP中对应相同键的值数组 本方法在大数据量时低效，请通过业务逻辑过滤数据，来实现本功能，尽量不要使用本方法
     *
     * @param map 源MAP
     * @param val 值
     * @return map空/val不存在/无对应key 都返回null
     */
    public static String[] getMapKeysByVal(Map<String, Object> map, Object val) {
        if (CoreUtils.nullSafeSize(map) == 0) {
            return null;
        }
        if (!map.containsValue(val)) {
            return null;
        }
        List<Object> retList = new ArrayList<Object>();
        for (Entry<String, Object> entry : map.entrySet()) {
            String tempKey = entry.getKey();
            Object tempVal = entry.getValue();
            if (tempVal.equals(val)) {
                retList.add(tempKey);
            }
        }
        if (retList.size() == 0) {
            return null;
        }
        return (String[]) retList.toArray(new String[retList.size()]);
    }


    /**
     * 将srcDel分隔的字符串转换为单项后，按Map进行单独字典转换，之后以desDel为分隔符合并;若字典中不存在，则保留原始值。
     *
     * @param srcStrings 源字符串
     * @param dicMap     转义字典
     * @param srcDel     原分隔符
     * @param desDel     目标分隔符
     * @return 将srcDel分隔的字符串转换为单项后，按Map进行单独字典转换，之后以desDel为分隔符合并;若字典中不存在，则保留原始值。
     */
    public static String parseString(String srcStrings, Map<String, String> dicMap,
                                     String srcDel, String desDel) {
        if (nullSafeSize(srcDel) != 0 && nullSafeSize(desDel) != 0
                && nullSafeSize(dicMap) != 0) {
            StringBuilder sb = new StringBuilder();
            String[] srcArr = StrUtils.split(srcStrings, srcDel);
            for (String aSrcArr : srcArr) {
                String desStr = (String) dicMap.get(aSrcArr);
                if (nullSafeSize(desStr) == 0) {// 若字典中不存在，则保留原始值
                    sb.append(aSrcArr).append(desDel);
                } else {
                    sb.append(desStr).append(desDel);
                }
            }
            if (sb.length() > 0)
                sb.delete(sb.length() - desDel.length(), sb.length());// 删除末尾desDel
            return sb.toString();
        } else {
            return srcStrings;
        }
    }

    public static String getServerYear() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        return formatter.format(new Date());
    }

    public static String getServerMonth() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        return formatter.format(new Date());
    }

    public static String getServerDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        return formatter.format(new Date());
    }

    /**
     * 合并二维List的列，若符合patternStr，则合并后对目标列进行修改
     *
     * @param dataList   源二维List
     * @param foot       步长
     * @param srcInd     要被合并的列索引(步长内索引，从1开始)
     * @param desInd     合并后修改的列索引(步长内索引，从1开始)
     * @param patternStr 合并列对应的修改条件
     * @param headStr    若符合patternStr，则合并后的修改列所要添加的头内容
     * @param tailStr    若符合patternStr，则合并后的修改列所要添加的尾内容
     * @return 合并二维List的列，若符合patternStr，则合并后对目标列进行修改
     */
    public static ArrayList<ArrayList<Object>> mergeListCol(ArrayList<ArrayList<Object>> dataList, int foot, int srcInd,
                                                            int desInd, String patternStr, String headStr, String tailStr) {
        if (foot <= 0 || srcInd <= 0 || desInd <= 0 || srcInd > foot
                || desInd > foot || srcInd >= desInd) {
            return null;// 实际上算异常
        }
        ArrayList<ArrayList<Object>> retList = new ArrayList<ArrayList<Object>>();
        Pattern pattern = Pattern.compile(patternStr);
        for (ArrayList<Object> aDataList : dataList) {
            ArrayList<Object> retTempList = new ArrayList<Object>();
            if (srcInd >= aDataList.size() || desInd > aDataList.size()) {// 长度合法性校验
                return null;// 实际上算异常
            }
            int j = 0;
            for (; j < aDataList.size() - (aDataList.size() % foot); ) {// 数据区长度能被步长整除部分
                String srcStr = (String) aDataList.get(j + srcInd - 1);// 要被合并值
                String desTempStr = (String) aDataList.get(j + desInd - 1);// 合并后要修改的值
                String desStr = desTempStr;
                Matcher matcher = pattern.matcher(srcStr);
                if (matcher.matches())
                    desStr = new StringBuffer().append(headStr).append(
                            desTempStr).append(tailStr).toString();// 修改后的值
                for (int k = 0; k < foot; k++) {
                    if (k != (srcInd - 1) && k != (desInd - 1)) {
                        retTempList.add(aDataList.get(j + k));
                    } else if (k == (desInd - 1)) {// 目标
                        retTempList.add(desStr);
                    }
                }
                j = j + foot;
            }
            if (aDataList.size() % foot != 0) {// 至少走过一个区块，最后剩余的区块（即数据区长度不能被步长整除）
                // //不做处理，当异常
                int spaInd = aDataList.size() - j;// 剩余的区块长度
                if (srcInd > spaInd) {// 合并列校验不通过，不做合并处理
                    for (int k = 0; k < spaInd; k++) {// 此处应取剩余长度
                        retTempList.add(aDataList.get(j + k));
                    }
                } else if (desInd > spaInd) {// 合并列校验通过，目标列校验不通过，做合并处理
                    for (int k = 0; k < spaInd; k++) {// 此处应取剩余长度
                        if (k != (srcInd - 1)) {
                            retTempList.add(aDataList.get(j + k));
                        }
                    }
                } else {
                    String srcStr = (String) aDataList.get(j + srcInd - 1);// 要被合并值
                    String desTempStr = (String) aDataList.get(j + desInd - 1);// 合并后要修改的值
                    String desStr = desTempStr;
                    Matcher matcher = pattern.matcher(srcStr);
                    if (matcher.matches())
                        desStr = new StringBuffer().append(headStr).append(
                                desTempStr).append(tailStr).toString();// 修改后的值
                    for (int k = 0; k < spaInd; k++) {// 此处应取剩余长度
                        if (k != (srcInd - 1) && k != (desInd - 1)) {
                            retTempList.add(aDataList.get(j + k));
                        } else if (k == (desInd - 1)) {// 目标
                            retTempList.add(desStr);
                        }
                    }
                }
            }
            retList.add(retTempList);
        }
        return retList;
    }

    /**
     * 取指定年月的月末日期
     *
     * @param srcdate 6位或者8位数字
     * @return 取指定年月的月末日期
     */
    public static int getLastDay(String srcdate) {
        int lastday = 0;
        Pattern p8 = Pattern.compile("^\\d{8}$");
        Matcher m8 = p8.matcher(srcdate);
        Pattern p6 = Pattern.compile("^\\d{6}$");
        Matcher m6 = p6.matcher(srcdate);
        if (m8.matches() || m6.matches()) {// 确定为8位或6位数字
            String yearStr = srcdate.substring(0, 4);
            String monthStr = srcdate.substring(4, 6);
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            if (1900 <= year && month <= 12) {
//				String[] mon31Arr = { "1", "3", "5", "7", "8", "10", "12" };
//				String[] mon30Arr = { "4", "6", "9", "11" };
//				String[] mon28Arr = { "2" };
                Pattern p31 = Pattern.compile("^1|3|5|7|8|10|12$");
                Matcher m31 = p31.matcher(String.valueOf(month));
                Pattern p30 = Pattern.compile("^4|6|9|11$");
                Matcher m30 = p30.matcher(String.valueOf(month));
                if (m31.matches()) {
                    lastday = 31;
                } else if (m30.matches()) {
                    lastday = 30;
                } else if (month == 2) {// 2月
                    if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {// 闰年
                        lastday = 29;
                    } else {// 非闰年
                        lastday = 28;
                    }
                }
            }
        }
        return lastday;
    }

    /**
     * 校验8位日期
     *
     * @param srcdate srcdate
     * @return 校验8位日期
     */
    public static boolean check8date(String srcdate) {
        Pattern p = Pattern.compile("^\\d{8}$");
        Matcher m = p.matcher(srcdate);
        if (m.matches()) {// 确定为8位数字
            String yearStr = srcdate.substring(0, 4);
            String monthStr = srcdate.substring(4, 6);
            String dateStr = srcdate.substring(6, 8);
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int date = Integer.parseInt(dateStr);
            if (1900 <= year && month <= 12) {
                Pattern p31 = Pattern.compile("^1|3|5|7|8|10|12$");
                Matcher m31 = p31.matcher(String.valueOf(month));
                Pattern p30 = Pattern.compile("^4|6|9|11$");
                Matcher m30 = p30.matcher(String.valueOf(month));
                if (m31.matches()) {
                    if (date <= 31) {
                        return true;
                    }
                } else if (m30.matches()) {
                    if (date <= 30) {
                        return true;
                    }
                } else if (month == 2) {// 2月
                    if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {// 闰年
                        if (date <= 29) {
                            return true;
                        }
                    } else {// 非闰年
                        if (date <= 28) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 校验8位日期
     *
     * @param srcdate srcdate
     * @param flag    是否允许00月00日
     * @return 校验8位日期
     */
    public static boolean check8date(String srcdate, boolean flag) {
        Pattern p = Pattern.compile("^\\d{8}$");
        Matcher m = p.matcher(srcdate);
        if (m.matches()) {// 确定为8位数字
            String yearStr = srcdate.substring(0, 4);
            String monthStr = srcdate.substring(4, 6);
            String dateStr = srcdate.substring(6, 8);
            if (flag) {
                if ("00".equals(monthStr) && "00".equals(dateStr)) {
                    return true;
                } else if ("99".equals(monthStr) && "99".equals(dateStr)) {
                    return true;
                }
            }
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int date = Integer.parseInt(dateStr);
            if (1900 <= year && month <= 12) {
                Pattern p31 = Pattern.compile("^1|3|5|7|8|10|12$");
                Matcher m31 = p31.matcher(String.valueOf(month));
                Pattern p30 = Pattern.compile("^4|6|9|11$");
                Matcher m30 = p30.matcher(String.valueOf(month));
                if (m31.matches()) {
                    if (date <= 31) {
                        return true;
                    }
                } else if (m30.matches()) {
                    if (date <= 30) {
                        return true;
                    }
                } else if (month == 2) {// 2月
                    if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {// 闰年
                        if (date <= 29) {
                            return true;
                        }
                    } else {// 非闰年
                        if (date <= 28) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 生成Option Html
     *
     * @param map        值域
     * @param selectdKey 选中key
     * @return 生成Option Html
     */
    public static String genOptionHtml(LinkedHashMap<String, String> map, String selectdKey) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            sb.append("<option value='").append(key).append("'");
            if (selectdKey.equals(key)) {
                sb.append(" selected ");
            }
            sb.append(">").append((String) map.get(key)).append("</option>");
        }
        return sb.toString();
    }

    /**
     * 拆分成ArrayList
     *
     * @param src src
     * @param del del
     * @return 拆分成ArrayList
     */
    public static ArrayList<String> splitToAList(String src, String del) {
        String[] arr = StrUtils.split(src, del);
        ArrayList<String> retList = new ArrayList<String>();
        Collections.addAll(retList, arr);
        return retList;
    }

    /**
     * 拆分成LinkedHashMap
     *
     * @param src   src
     * @param pDel  参数分隔符
     * @param kvDel 键值分隔符
     * @return 拆分成LinkedHashMap
     */
    public static LinkedHashMap<String, String> splitToLHM(String src,
                                                           String pDel, String kvDel) {
        String[] arr = StrUtils.split(src, pDel);
        LinkedHashMap<String, String> retLHM = new LinkedHashMap<String, String>();
        for (String kvStr : arr) {
            String[] tempArr = StrUtils.split(kvStr, kvDel);
            if (nullSafeSize(tempArr) == 2) {
                retLHM.put(tempArr[0], tempArr[1]);
            }
        }
        return retLHM;
    }

    /**
     * 初始化年LHM 对于默认选择日期的阀值限制请在调用前自行初始化，
     * 如“若系统日期在可选范围内，则初始化为系统日期；否则若小于sdate，则选择sdate，大于edate，则选择edate”
     *
     * @param sdate        起始日期
     * @param edate        终止日期
     * @param selectedDate 所选日期
     * @return 初始化年LHM
     */
    public static LinkedHashMap<String, String> getYear(String sdate,
                                                        String edate, String selectedDate) {
        LinkedHashMap<String, String> retLHM = new LinkedHashMap<String, String>();
        int eYear = new Integer(edate.substring(0, 4));
        int sYear = new Integer(sdate.substring(0, 4));
        for (int i = eYear; i >= sYear; i--) {
            String year = Integer.toString(i);
            retLHM.put(year, year);
        }
        return retLHM;
    }

    /**
     * 初始化月LHM 对于默认选择日期的阀值限制请在调用前自行初始化，
     * 如“若系统日期在可选范围内，则初始化为系统日期；否则若小于sdate，则选择sdate，大于edate，则选择edate”
     *
     * @param sdate        起始日期
     * @param edate        终止日期
     * @param selectedDate 选择日期
     * @return 初始化月LHM
     */
    public static LinkedHashMap<String, String> getMonth(String sdate,
                                                         String edate, String selectedDate) {
        LinkedHashMap<String, String> retLHM = new LinkedHashMap<String, String>();
        int sMon = new Integer(sdate.substring(4, 6));
        int eMon = new Integer(edate.substring(4, 6));
        String selectedYear = selectedDate.substring(0, 4);
//		String selectedMonDate = selectedDate.substring(4, 8);// 月日
        if (sdate.substring(0, 4).equals(selectedYear)) {// 只能从开始那年的月开始选
            for (int i = sMon; i <= 12; i++) {
                String month = i < 10 ? ("0".concat(String.valueOf(i)))
                        : String.valueOf(i);
                retLHM.put(month, month);
            }
        } else if (edate.substring(0, 4).equals(selectedYear)) {
            for (int i = 1; i <= eMon; i++) {// 只能从选到结束那年的月
                String month = i < 10 ? ("0".concat(String.valueOf(i)))
                        : String.valueOf(i);
                retLHM.put(month, month);
            }
        } else {
            for (int i = 1; i <= 12; i++) {
                String month = i < 10 ? ("0".concat(String.valueOf(i)))
                        : String.valueOf(i);
                retLHM.put(month, month);
            }
        }
        return retLHM;
    }

    /**
     * 初始化日LHM 对于默认选择日期的阀值限制请在调用前自行初始化，
     * 如“若系统日期在可选范围内，则初始化为系统日期；否则若小于sdate，则选择sdate，大于edate，则选择edate”
     *
     * @param sdate        起始日期
     * @param edate        终止日期
     * @param selectedDate 所选日期
     * @return 初始化日LHM
     */
    public static LinkedHashMap<String, String> getDay(String sdate,
                                                       String edate, String selectedDate) {
        LinkedHashMap<String, String> retLHM = new LinkedHashMap<String, String>();
        int sDay = new Integer(sdate.substring(6, 8));
        int eDay = new Integer(edate.substring(6, 8));
//		String selectedMonDate = selectedDate.substring(4, 8);// 月日
        String selectedYearMon = selectedDate.substring(0, 6);// 年月
        int selectedLastday = CoreUtils.getLastDay(selectedDate);// 月末日期
        if (sdate.substring(0, 6).equals(selectedYearMon)) {// 只能从开始那年月的日开始选
            for (int i = sDay; i <= selectedLastday; i++) {
                String month = i < 10 ? ("0".concat(String.valueOf(i)))
                        : String.valueOf(i);
                retLHM.put(month, month);
            }
        } else if (edate.substring(0, 6).equals(selectedYearMon)) {
            for (int i = 1; i <= eDay; i++) {// 只能从选到结束那年月的日
                String month = i < 10 ? ("0".concat(String.valueOf(i)))
                        : String.valueOf(i);
                retLHM.put(month, month);
            }
        } else {
            for (int i = 1; i <= selectedLastday; i++) {
                String month = i < 10 ? ("0".concat(String.valueOf(i)))
                        : String.valueOf(i);
                retLHM.put(month, month);
            }
        }
        return retLHM;
    }

    /**
     * 使用分隔符将源字符串分隔为ArrayList
     *
     * @param str 源字符串
     * @param del 分隔符
     * @param len 指定数组长度，当lenM小等于0时，取能分隔的最大个数
     * @return 使用分隔符将源字符串分隔为ArrayList
     */
    public static ArrayList<String> genListBySep(String str, String del, int len) {
        ArrayList<String> retList = new ArrayList<String>();
        String[] arr = StrUtils.split(str, del);
        int loopLen = len <= 0 ? arr.length : len;
        retList.addAll(Arrays.asList(arr).subList(0, loopLen));
        return retList;
    }

    /**
     * 将2个String数组组合成2维List（纵向），以长度最短数组长度的为基准
     *
     * @param strArr1 源字符串1
     * @param strArr2 源字符串2
     * @return 将2个String数组组合成2维List（纵向），以长度最短数组长度的为基准
     */
    public static ArrayList<ArrayList<String>> con2StrArrsTo2DList(
            String[] strArr1, String[] strArr2) {
        ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
        int loopLen = strArr1.length <= strArr2.length ? strArr1.length
                : strArr2.length;
        for (int i = 0; i < loopLen; i++) {
            ArrayList<String> tempList = new ArrayList<String>();
            String str1 = strArr1[i];
            String str2 = strArr2[i];
            tempList.add(str1);
            tempList.add(str2);
            retList.add(tempList);
        }
        return retList;
    }

    /**
     * 生成指定长度的数组
     *
     * @param str    填充值
     * @param length 长度
     * @return 生成指定长度的数组
     */
    public static String[] conFixLengthArr(String str, int length) {
        String[] retArr = new String[length];
        for (int i = 0; i < length; i++) {
            retArr[i] = str;
        }
        return retArr;
    }

    /**
     * 将String Array构造为ArrayList
     *
     * @param strArr 源数组
     * @return 构造后的ArrayList
     */
    public static ArrayList<String> conALFromStrArr(String[] strArr) {
        ArrayList<String> retList = new ArrayList<String>();
        Collections.addAll(retList, strArr);
        return retList;
    }

    /**
     * 将InputStream转换为byte[]
     *
     * @param is 输入流
     * @return 转换后的byte[]
     */
    public static byte[] conInputStreamToByteArr(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream fis = new BufferedInputStream(is);
        byte[] ret = null;
        int byteread = 0;
        byte[] buffer = new byte[65536];
        try {
            while ((byteread = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, byteread);
            }
            ret = baos.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(baos);
        }
        return ret;
        // byte[] ba = new byte[baos.toByteArray().length - 1];
        // System.arraycopy(baos.toByteArray(), 0, ba, 0,
        // baos.toByteArray().length - 1);
        // return ba;
    }

    /**
     * 从String中取出对应参数组合
     *
     * @param str       源String
     * @param del       分隔符
     * @param entryKey  键
     * @param keyValDel 键值分隔符
     * @return 从String中取出对应参数组合
     */
    public static String retrieveEntryValFromStr(String str, String del,
                                                 String entryKey, String keyValDel) {
        if (CoreUtils.nullSafeSize(str) > 0) {
            String[] arr = StrUtils.split(str, del);// 使用substring方式容错性差效率稍高,后续考虑是否调整优化
            for (String string : arr) {
                String[] entryArr = StrUtils.split(string, keyValDel);
                if (entryArr != null && entryArr.length == 2) {
                    String tempKey = entryArr[0];
                    if (tempKey.equalsIgnoreCase(entryKey)) {
                        return entryArr[1];
                    }
                }
            }
        }
        return "";
    }

    /**
     * 转换Map为String
     *
     * @param map       源Map
     * @param del       组合分隔符
     * @param keyValDel 键值分隔符
     * @return 转换Map为String
     */
    public static String convertMap2Str(Map<String, String> map, String del,
                                        String keyValDel) {
        StringBuilder sb = new StringBuilder();
        if (CoreUtils.nullSafeSize(map) > 0) {
            for (Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key).append(keyValDel).append(value).append(del);
            }
            if (sb.length() > 0)
                sb.delete(sb.length() - del.length(), sb.length());
        }
        return sb.toString();
    }

    /**
     * 转换Map为String
     *
     * @param map       源Map
     * @param del       组合分隔符
     * @param keyValDel 键值分隔符
     * @return 转换Map为String
     */
    public static String convertMap2StrEnc(Map<String, String> map, String del,
                                           String keyValDel) {
        StringBuilder sb = new StringBuilder();
        if (CoreUtils.nullSafeSize(map) > 0) {
            for (Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key).append(keyValDel).append(value).append(del);
            }
            if (sb.length() > 0)
                sb.delete(sb.length() - del.length(), sb.length());
        }
        return sb.toString();
    }

    /**
     * 根据二维AL拼简单表格
     *
     * @param srcList   源二维列表
     * @param defTrBg
     * @param otherTrBg
     * @return 根据二维AL拼简单表格
     */
    public static String genSimpleTableHtml(ArrayList<ArrayList<String>> srcList, String defTrBg, String otherTrBg) {
        StringBuffer sb = new StringBuffer();
        sb.append("<table width='100%' border='1' style='border-spacing:0px;border-collapse:collapse;'>").append("\n");
        int i = 0;
        String trBg;
        for (ArrayList<String> subList : srcList) {
            if (i % 2 == 0) {
                trBg = defTrBg;
            } else {
                trBg = otherTrBg;
            }
            sb.append("  <tr style='background:").append(trBg).append(";'>").append("\n");
            sb.append("  <td>Line ").append(i + 1).append("</td>").append("\n");
            for (String string : subList) {
                sb.append("  	<td>").append(string).append("</td>")
                        .append("\n");
            }
            sb.append("  </tr>").append("\n");
            i++;
        }
        sb.append("</table>").append("\n");
        return sb.toString();
    }

    /**
     * 将ResultSet转换为二维List，外层List内含ArrayList
     *
     * @param rs ResultSet对象
     * @return List对象
     * @throws SQLException
     */
    public static ArrayList<ArrayList<String>> getListFromRs(ResultSet rs)
            throws SQLException {
        ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
        ResultSetMetaData rsmeta = rs.getMetaData();
        int columnCount = rsmeta.getColumnCount();
        String recordValue;
        ArrayList<String> oneList;
        while (rs.next()) {
            oneList = new ArrayList<String>();
            for (int i = 1; i <= columnCount; i++) {
                recordValue = rs.getString(i);
                oneList.add(recordValue == null ? "" : String.valueOf(recordValue));
            }
            retList.add(oneList);
        }
        return retList;
    }

    /**
     * 将ResultSet转换为分隔符分隔的字符串
     *
     * @param rs       rs
     * @param rowSep   rowSep
     * @param colSep   colSep
     * @param colShift colShift
     * @return 字符串
     * @throws SQLException
     */
    public static String getSepStringFromRs(ResultSet rs, String rowSep, String colSep, int colShift)
            throws SQLException {
        return getSepStringFromRs(rs, rowSep, colSep, false, colShift);
    }

    /**
     * 获取游标中的列名
     *
     * @param rsmeta   rsmeta
     * @param colShift colShift
     * @return 列名
     * @throws SQLException
     */
    public static String[] getColNameArr(ResultSetMetaData rsmeta, int colShift) throws SQLException {
        String[] retArr = null;
        if (rsmeta != null) {
            int columnCount = rsmeta.getColumnCount();
            retArr = new String[columnCount - colShift];
            for (int i = colShift; i < columnCount; i++) {
                retArr[i - colShift] = rsmeta.getColumnName(i + 1);
            }
        }
        return retArr;
    }

    /**
     * 将ResultSet转换为分隔符分隔的字符串
     *
     * @param rs        rs
     * @param rowSep    rowSep
     * @param colSep    colSep
     * @param titleFlag 是否包含标题列
     * @param colShift  列偏移
     * @return 字符串
     * @throws SQLException
     */
    public static String getSepStringFromRs(ResultSet rs, String rowSep, String colSep, boolean titleFlag, int colShift)
            throws SQLException {
        StringBuilder sb = new StringBuilder();
        ResultSetMetaData rsmeta = rs.getMetaData();
        int columnCount = rsmeta.getColumnCount();
        String recordValue = "";
        if (titleFlag) {//若要标题列
            String[] colNameArr = getColNameArr(rsmeta, colShift);
            String colnameStr = conStrArrToStr(colNameArr, colSep);
            sb.append(colnameStr).append(rowSep);
        }
        while (rs.next()) {
            StringBuilder dataSb = new StringBuilder();
            for (int i = 1 + colShift; i <= columnCount; i++) {
                recordValue = rs.getString(i);
                dataSb.append(recordValue == null ? "" : String.valueOf(recordValue)).append(colSep);
            }
            if (dataSb.length() > 0) {
                dataSb.delete(dataSb.length() - colSep.length(), dataSb.length());
                dataSb.append(rowSep);
            }
            sb.append(dataSb);
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - rowSep.length(), sb.length());
        }
        return sb.toString();
    }

    /**
     * 返回一个新的List，它是原List的一个子List。
     *
     * @param aal      原二维ArrayList
     * @param index    指定索引号
     * @param nullflag 为true时,若子元素值为null,则使用index的值代替;为false,则仍然使用null
     * @return List
     */
    public static ArrayList<String> subAListByIndex(
            ArrayList<ArrayList<String>> aal, int index, boolean nullflag) {
        ArrayList<String> tempList = new ArrayList<String>();
        for (ArrayList<String> al : aal) {
            String string = al.get(index);
            if (nullflag) {
                if (nullSafeSize(string) > 0) {
                    tempList.add(string);
                } else {
                    tempList.add(String.valueOf(index));
                }
            } else {
                tempList.add(string);
            }

        }
        return tempList;
    }

    /**
     * 使用指定的编码机制将字符串转换为 application/x-www-form-urlencoded 格式
     *
     * @param urlEncoded 编码过的url
     * @param encoding   编码
     * @return 使用指定的编码机制将字符串转换为 application/x-www-form-urlencoded 格式
     * @throws UnsupportedEncodingException
     */
    public static String formatUrlParam(String urlEncoded, String encoding)
            throws UnsupportedEncodingException {
        String ret = urlEncoded;
        String strAsk = "?";
        String strAnd = "&";
        String strEql = "=";
        String urlDecoded = URLDecoder.decode(urlEncoded, encoding);

        String[] urlArr = StrUtils.split(urlDecoded, strAsk);
        if (urlArr.length > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(urlArr[0]).append(strAsk);
            String[] arr = StrUtils.split(urlArr[1], strAnd);
            StringBuilder kvSb = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                String kvStr = arr[i];
                String[] tempArr = StrUtils.split(kvStr, strEql);
                if (nullSafeSize(tempArr) > 0) {
                    kvSb.append(tempArr[0]).append(strEql);
                    String val = tempArr[1].substring(tempArr[1]
                            .indexOf(strAnd)
                            + strAnd.length());
                    kvSb.append(URLEncoder.encode(val, encoding));
                    kvSb.append(strAnd);
                }
            }
            if (kvSb.length() > 0)
                kvSb.delete(kvSb.length() - strAnd.length(), kvSb.length());
            sb.append(kvSb);
            ret = sb.toString();
        }
        return ret;
    }

    /**
     * 使用指定的编码机制将字符串转换为 application/x-www-form-urlencoded 格式
     *
     * @param url      url
     * @param encoding 编码
     * @return 使用指定的编码机制将字符串转换为 application/x-www-form-urlencoded 格式
     * @throws UnsupportedEncodingException
     */
    public static String formatUrlParamNoAsk(String url, String encoding)
            throws UnsupportedEncodingException {
        String ret = url;
        String strAnd = "&";
        String strEql = "=";

        StringBuilder sb = new StringBuilder();
        String[] arr = StrUtils.split(url, strAnd);
        StringBuilder kvSb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            String kvStr = arr[i];
            String[] tempArr = StrUtils.split(kvStr, strEql);
            if (nullSafeSize(tempArr) > 0) {
                kvSb.append(tempArr[0]).append(strEql);
                String val = tempArr[1].substring(tempArr[1].indexOf(strAnd)
                        + strAnd.length());
                kvSb.append(URLEncoder.encode(val, encoding));
                kvSb.append(strAnd);
            }
        }
        if (kvSb.length() > 0)
            kvSb.delete(kvSb.length() - strAnd.length(), kvSb.length());
        sb.append(kvSb);
        ret = sb.toString();
        return ret;
    }

    /**
     * 生成简单hashcode
     *
     * @param str str
     * @return 简单hashcode
     */
    public static String genHashcode(String str) {
        String hashCode = "";
        try {
            MessageDigest alga = MessageDigest
                    .getInstance("SHA-1");// 20位
            // MessageDigest
            // alga=java.security.MessageDigest.getInstance("MD5");//15位
            alga.update(str.getBytes());
            byte[] digesta = alga.digest();
            hashCode = byte2hex(digesta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashCode;
    }

    /**
     * byte转hex
     *
     * @param b
     * @return hex
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
            if (n < b.length - 1)
                hs.append("");
        }
        return hs.toString().toUpperCase();
    }

    /**
     * 连接字符串
     *
     * @param arr 源数组
     * @return 字符串
     */
    public static String concat(String[] arr) {
        StringBuilder sb = new StringBuilder("");
        for (String str : arr) {
            if (nullSafeSize(str) > 0) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * 连接字符串
     *
     * @param list list
     * @return 字符串
     */
    public static String concat(List<String> list) {
        StringBuilder sb = new StringBuilder("");
        for (String str : list) {
            if (nullSafeSize(str) > 0) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * 取消科学计数法
     *
     * @param doubleStr str
     * @return 字符串
     */
    public static String formatSN(String doubleStr) {
        String ret = "0";
        try {
            BigDecimal bd = new BigDecimal(doubleStr);
            ret = bd.toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 取消科学计数法
     *
     * @param d double
     * @return 字符串
     */
    public static String formatSN(Double d) {
        String ret = "0";
        try {
            BigDecimal bd = new BigDecimal(d);
            ret = bd.toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    /**
     * 将字符串转换为Vector
     *
     * @param str str
     * @param sep 分隔符
     * @return vector
     */
    public static Vector<String> delStrToVector(String str, String sep) {
        Vector<String> ret = new Vector<String>();
        String[] tempArr = StrUtils.split(str, sep);
        Collections.addAll(ret, tempArr);
        return ret;
    }

    /**
     * 将字符串中分隔符进行替换
     *
     * @param str       要转换的字符串
     * @param oldSubdel 原子节点分隔符
     * @param oldDel    原节点分隔符
     * @param subdel    子节点分隔符
     * @param del       节点分隔符
     * @return 字符串
     */
    public static String delStringToDelString(String str, String oldSubdel,
                                              String oldDel, String subdel, String del) {
        str = str.replace(oldSubdel, subdel);
        str = str.replace(oldDel, del);
        return str;
    }

    /**
     * 判断前台传入的分隔符，如为空则返回指定分隔符，否则进行解码后返回
     *
     * @param str 前台传入的分隔符
     * @param del 指定分隔符
     * @return 实际使用分隔符
     */
    public static String decodeDelString(String str, String del) {
        if (nullSafeSize(str) == 0) {
            return del;
        } else {
            try {
                return URLDecoder.decode(str, Constants.ENCODING_DEF);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return del;
    }

    /**
     * 日期格式转换,支持分隔符字符串,若为单值,srcSep和desSep分隔符填空格即可
     *
     * @param datestr   日期字符串
     * @param srcFormat 源格式
     * @param desFormat 目标格式
     * @param srcSep    源分隔符
     * @param desSep    目标分隔符
     * @return
     */
    public static String convertDateFormat(String datestr, String srcFormat, String desFormat, String srcSep, String desSep) {
        StringBuilder sb = new StringBuilder();
        if (nullSafeSize(datestr) > 0) {
            String[] dateArr = StrUtils.split(datestr, srcSep);
            SimpleDateFormat sdf = new SimpleDateFormat(srcFormat);
            SimpleDateFormat sdfNew = new SimpleDateFormat(desFormat);
            for (String date : dateArr) {
                String temp = date;
                try {
                    Date d = sdf.parse(date);
                    temp = sdfNew.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sb.append(temp).append(desSep);
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - desSep.length(), sb.length());
            }
        }
        return sb.toString();
    }

    /**
     * 将String转换为int
     *
     * @param str str
     * @return int
     */
    public static int strToInt(String str) {
        int ret = 0;
        if (str != null) {
            try {
                ret = Integer.parseInt(str);
            } catch (Exception e) {

            }
        }
        return ret;
    }

    /**
     * 合并数组
     *
     * @param arr1 数组1
     * @param arr2 数组2
     * @return
     */
    public static String[] appendArray(String[] arr1, String[] arr2) {
        int len = arr1.length;
        String[] retArr = new String[len + arr2.length];
        System.arraycopy(arr1, 0, retArr, 0, len);
        System.arraycopy(arr2, 0, retArr, len + 0, arr2.length);
        return retArr;
    }

    /**
     * 行列互换
     *
     * @param doubleArr doubleArr
     * @return 行列互换
     */
    public static String[][] convertDoubleArr(String[][] doubleArr) {
        String[][] retArr = null;
        int srcRowSize = nullSafeSize(doubleArr);
        if (srcRowSize > 0) {
            String[] firstRow = doubleArr[0];
            int srcColSize = nullSafeSize(firstRow);
            if (srcColSize > 0) {
                retArr = new String[srcColSize][srcRowSize];
                for (int i = 0; i < doubleArr.length; i++) {
                    String[] subArr = doubleArr[i];
                    for (int j = 0; j < subArr.length; j++) {
                        String string = subArr[j];
                        retArr[j][i] = string;
                    }
                }
            }
        }
        return retArr;
    }

    public static void closeChannel(Channel channel) {
        if(channel!=null){
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Closeable closeable){
        if(closeable!=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
