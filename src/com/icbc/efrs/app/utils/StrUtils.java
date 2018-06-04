package com.icbc.efrs.app.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StrUtils {

    /**
     * 格式化字符串 如：
     * <p>
     * <pre>
     *
     *
     * </pre>
     *
     * @param templateStr  格式串，仅支持%s
     * @param replacements 替换字符串数组
     * @return 格式化后字符串
     */
    public static String format(String templateStr, String[] replacements) throws Exception {
        char[] templateChars = templateStr.toCharArray();
        int replaceCount = 0;
        for (int i = 0; i < templateChars.length - 1; i++) {
            if (templateChars[i] == '%') {
                switch (Character.toLowerCase(templateChars[i + 1])) {
                    case 's':
                        if (replaceCount < replacements.length && replacements[replaceCount] != null) {
                            templateChars = replace2CharsBeginAt(templateChars, i, replacements[replaceCount].toCharArray());
                            i += replacements[replaceCount].length() - 1;
                            replaceCount++;
                        } else {
                            templateChars = replace2CharsBeginAt(templateChars, i, new char[]{});
                            i += -1;
                            replaceCount++;
                        }
                        break;
                    case '%':
                        templateChars = replace2CharsBeginAt(templateChars, i, new char[]{'%'});
                        break;
                    default:
                        throw new Exception("Can't undestand [%" + templateChars[i + 1]
                                + "] in template [" + templateStr + "]");
                }
            }
        }
        return new String(templateChars);
    }

    /**
     * 字符数组替换(吃掉替换起始点以及其后的两个对应值,替换以另一字符数组)
     *
     * @param src         接受替换的字符数组(大)
     * @param beginAt     替换起始点
     * @param replaceWith 替换成的字符数组(小)
     * @return 替换完成的字符数组
     */
    private static char[] replace2CharsBeginAt(char[] src, int beginAt, char[] replaceWith) {
        char[] tmpChars = new char[src.length + replaceWith.length - 2];
        System.arraycopy(src, 0, tmpChars, 0, beginAt);
        System.arraycopy(replaceWith, 0, tmpChars, beginAt, replaceWith.length);
        System.arraycopy(src, beginAt + 2, tmpChars, beginAt + replaceWith.length, src.length - beginAt - 2);
        return tmpChars;
    }

    /**
     * 自JSP路径获得类路径方式
     *
     * @param jspPath jsp路径
     * @return 类路径方式
     */
    public static String getClassURL4JSP(String jspPath) {
        char[] jspPathChars = jspPath.toCharArray();
        int beginIndex = 0;
        while (jspPathChars[beginIndex] == '/')
            beginIndex++;
        if (jspPathChars[beginIndex] == 'f' && jspPathChars[beginIndex + 1] == 'o' && jspPathChars[beginIndex + 2] == 'r'
                && jspPathChars[beginIndex + 3] == 'm' && jspPathChars[beginIndex + 4] == 's'
                && jspPathChars[beginIndex + 5] == '/')
            beginIndex += 6;
        int endIndex = jspPathChars[jspPathChars.length - 4] == '.' ? jspPathChars.length - 5 : jspPathChars.length;
        for (int i = beginIndex; i <= endIndex; i++)
            if (jspPathChars[i] == '/')
                jspPathChars[i] = '.';
        return new String(jspPathChars, beginIndex, endIndex - beginIndex + 1);
    }

    /**
     * 返回一List中的各个对象的类名所拼接成的字符串
     *
     * @param list 一指定的list
     * @return 类名拼接所得的字符串
     */
    public static String getClassesNameFromList(List list) {
        StringBuilder ret = new StringBuilder();
        for (Object aList : list) {
            ret.append(aList.getClass().getName());
        }
        return ret.toString();
    }

    /**
     * 从指定字符分割的字符串中提取字符串元素
     *
     * @param separator    作为分割符号的String
     * @param targetString 被分割的字符串
     * @return 提取出的字符串List
     */
    public static ArrayList<String> disSymbolString(String separator, String targetString) {
        if (targetString == null)
            return null;
        ArrayList<String> substrings = new ArrayList<String>();
        if (null == separator || "".equals(separator)) {
            substrings.add(targetString);
            return substrings;
        }

        int len = targetString.length();
        if (len == 0)
            return substrings;
        int separatorLength = separator.length();

        int begin = 0;
        int end = 0;
        while (end < len) {
            end = targetString.indexOf(separator, begin);

            if (end > -1) {
                if (end > begin) {
                    // 有出现
                    substrings.add(targetString.substring(begin, end));
                } else {
                    // 空出现
                    substrings.add("");
                }
                begin = end + separatorLength;
            } else {
                substrings.add(targetString.substring(begin));
                break;
            }
        }

        return substrings;
    }

    /**
     * 从指定字符分割的字符串中提取字符串元素
     *
     * @param separator    作为分割符号的String
     * @param targetString 被分割的字符串
     * @return 提取出的字符串List
     */
    public static String[] split(String targetString, String separator) {
        return list2StringArr(disSymbolString(separator, targetString));
    }

    /**
     * 将List转化为字符串数组
     *
     * @param list list
     * @return 字符串数组
     */
    public static String[] list2StringArr(List list) {
        if (list == null)
            return null;
        String[] ret = new String[list.size()];
        Object obj;
        for (int i = 0; i < ret.length; i++) {
            obj = list.get(i);
            ret[i] = obj == null ? null : obj.toString();
        }
        return ret;
    }

    /**
     * 按指定分隔符合并字符串数组
     *
     * @param separator 作为分隔符号的String
     * @param strings   待合并的字符串数组
     * @return 合并完成的字符串
     */
    public static String enSymbolString(String separator, List strings) {
        if (strings == null)
            return null;
        if (strings.size() == 0)
            return "";
        if (separator == null)
            separator = "";

        int bufSize = strings.size();
        String firstString = (String) strings.get(0);
        bufSize *= ((firstString == null ? 16 : firstString.length()) + separator.length());

        StringBuilder buf = new StringBuilder(bufSize);

        buf.append(firstString);
        for (int i = 1; i < strings.size(); i++) {
            buf.append(separator);
            buf.append((String) strings.get(i));
        }
        return buf.toString();
    }

    /**
     * 按指定分隔符合并字符串数组并加上单引号
     *
     * @param separator 作为分隔符号的String
     * @param strings   待合并的字符串数组
     * @return 合并完成的字符串
     */
    public static String enSymbolString2(String separator, List strings) {
        if (strings == null)
            return null;
        if (strings.size() == 0)
            return "";
        if (separator == null)
            separator = "";

        int bufSize = strings.size();
        String firstString = "'" + (String) strings.get(0) + "'";
        bufSize *= firstString.length() + separator.length();

        StringBuilder buf = new StringBuilder(bufSize);

        buf.append(firstString);
        for (int i = 1; i < strings.size(); i++) {
            buf.append(separator);
            buf.append("'").append((String) strings.get(i)).append("'");
        }
        return buf.toString();
    }

    /**
     * 将null格式化为空串
     *
     * @param in 待格式化字符串
     * @return 格式化后字符串
     */
    static public String formatNull2E(String in) {
        return in == null ? "" : in;
    }

    /**
     * 将null格式化为空串
     *
     * @param in 待格式化字符串
     * @return 格式化后字符串
     */
    static public String n2nbsp(Object in) {
        return in == null || "".equals(in.toString()) ? "&nbsp;" : in.toString();
    }

    /**
     * 实现Object的toString方法，null时转换为空串
     *
     * @param in 待toString的Object
     * @return 格式化后字符串
     */
    static public String n2e(Object in) {
        return in == null ? "" : in.toString();
    }

    /**
     * 获取数据库返回数据中的第一行作为字符串数组返回
     *
     * @param resu List of Map/List 的数据库返回
     * @return 第一行数据的数组
     */
    static public String[] get1stRowFromDBList(List resu) {
        String[] ret = null;
        if (resu.get(0) instanceof Map) {
            Map m = (Map) resu.get(0);
            Iterator it = m.keySet().iterator();
            ret = new String[m.size()];
            for (int i = 0; i < ret.length; i++)
                ret[i] = n2e(it.next());
        } else if (resu.get(0) instanceof List) {
            List m = (List) resu.get(0);
            ret = new String[m.size()];
            for (int i = 0; i < ret.length; i++)
                ret[i] = n2e(m.get(i));
        }
        return ret;
    }

    /**
     * 获取有序map的值作为String数组
     *
     * @param map 有序map
     * @return 值的数组
     */
    static public String[] getStringArrayFromMap(Map map) {
        String[] ret = new String[map.size()];
        Iterator it = map.values().iterator();
        Object o;
        for (int i = 0; i < ret.length; i++) {
            o = it.next();
            ret[i] = (o == null) ? null : o.toString();
        }
        return ret;
    }

    /**
     * 按对象数组建立有序hashmap
     *
     * @param objects 对象数组
     * @return 有序hashmap
     */
    static public LinkedHashMap createLinkedHashMap(Object[] objects) {
        LinkedHashMap m = new LinkedHashMap();
        for (int i = 0; i < objects.length; i += 2)
            m.put(objects[i], objects[i + 1]);
        return m;
    }

    /**
     * 数str中出现了多少个sub
     *
     * @param str 被检查大串
     * @param sub 检查小串
     * @return 小串出现次数
     */
    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * 为空判断
     *
     * @param str 检查串
     * @return 为空返回true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 取出str中出现sub的第times次时的位置
     *
     * @param str   str
     * @param sub   sub
     * @param times times
     * @return 位置，若找不到第times次则返回-1
     */
    public static int indexofTimes(String str, String sub, int times) {
        if (isEmpty(str) || isEmpty(sub)) {
            return -1;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            if (count == times)
                break;
            idx += sub.length();
        }
        return idx;
    }

    /**
     * 取出str中出现sub的第times次时的位置
     *
     * @param str   str
     * @param sub   sub
     * @param times times
     * @return 位置，若找不到第times次则返回-1
     */
    public static int indexofTimes(StringBuffer str, String sub, int times) {
        if (str == null || str.length() == 0 || isEmpty(sub)) {
            return -1;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            if (count == times)
                break;
            idx += sub.length();
        }
        return idx;
    }

    /**
     * 替换 > 的字符数组
     */
    public static final char[] LT_CHARS = "&lt;".toCharArray();
    /**
     * 替换 < 的字符数组
     */
    public static final char[] GT_CHARS = "&gt;".toCharArray();
    /**
     * 替换 " 的字符数组
     */
    public static final char[] QUOTE_CHARS = "&quot;".toCharArray();
    /**
     * 替换 & 的字符数组
     */
    public static final char[] AMP_CHARS = "&amp;".toCharArray();
    /**
     * 替换 = 的字符数组
     */
    public static final char[] EQU_CHARS = "=3D".toCharArray();
    /**
     * 替换 回车 的字符数组
     */
    public static final char[] BR_CHARS = "<br>".toCharArray();

    /**
     * 将字符串转换成mht内编码
     *
     * @param inStr 待转换字符串
     * @return 转换完成的字符串
     */
    public static String MHTEncode(String inStr) {
        if (inStr == null)
            return "";
        char[] inChars = inStr.toCharArray();

        char oneChar;
        for (int i = 0; i < inChars.length; i++) {
            oneChar = inChars[i];
            switch (oneChar) {
                case '<':
                    inChars = replaceCharsBeginAt(inChars, i, LT_CHARS);
                    i += (LT_CHARS.length - 1);
                    break;
                case '>':
                    inChars = replaceCharsBeginAt(inChars, i, GT_CHARS);
                    i += (GT_CHARS.length - 1);
                    break;
                case '\"':
                    inChars = replaceCharsBeginAt(inChars, i, QUOTE_CHARS);
                    i += (QUOTE_CHARS.length - 1);
                    break;
                case '&':
                    inChars = replaceCharsBeginAt(inChars, i, AMP_CHARS);
                    i += (AMP_CHARS.length - 1);
                    break;
                case '=':
                    inChars = replaceCharsBeginAt(inChars, i, EQU_CHARS);
                    i += (EQU_CHARS.length - 1);
                    break;
                case '\n':
                    inChars = replaceCharsBeginAt(inChars, i, BR_CHARS);
                    i += (BR_CHARS.length - 1);
                    break;
                case '\r':
                    char[] tmpChars = new char[inChars.length - 1];
                    System.arraycopy(inChars, 0, tmpChars, 0, i);
                    System.arraycopy(inChars, i + 1, tmpChars, i, inChars.length - i - 1);
                    inChars = tmpChars;
                    i += -1;
                    break;
            }
        }
        return new String(inChars);
    }

    /**
     * 字符数组替换(吃掉替换起始点的对应值,替换以另一字符数组)
     *
     * @param src         接受替换的字符数组(大)
     * @param beginAt     替换起始点
     * @param replaceWith 替换成的字符数组(小)
     * @return 替换完成的字符数组
     */
    private static char[] replaceCharsBeginAt(char[] src, int beginAt, char[] replaceWith) {
        char[] tmpChars = new char[src.length + replaceWith.length - 1];
        System.arraycopy(src, 0, tmpChars, 0, beginAt);
        System.arraycopy(replaceWith, 0, tmpChars, beginAt, replaceWith.length);
        System.arraycopy(src, beginAt + 1, tmpChars, beginAt + replaceWith.length, src.length - beginAt - 1);
        return tmpChars;
    }

    /**
     * 取出在指定起始与结束字符串之间的字符串（不包含起始结束）
     *
     * @param bigStr   被寻找的字符串
     * @param beginStr 起始字符串
     * @param endStr   结束字符串
     * @return 起始结束间的字符串（不包含起始结束）
     */
    public static String getStringBetween(String bigStr, String beginStr, String endStr) {
        int findStart = bigStr.indexOf(beginStr);
        if (findStart == -1)
            return null;
        findStart += beginStr.length();
        int findEnd = bigStr.indexOf(endStr, findStart + 1);
        if (findEnd == -1)
            return null;
        return bigStr.substring(findStart, findEnd);
    }

    /**
     * 取出在指定起始与结束字符串之间的字符串（不包含起始结束），从最末尾出开始寻找
     *
     * @param bigStr   被寻找的字符串
     * @param beginStr 起始字符串
     * @param endStr   结束字符串
     * @return 起始结束间的字符串（不包含起始结束）
     */
    public static String getLastStringBetween(String bigStr, String beginStr, String endStr) {
        int findStart = bigStr.lastIndexOf(beginStr);
        if (findStart == -1)
            return null;
        findStart += beginStr.length();
        int findEnd = bigStr.indexOf(endStr, findStart + 1);
        if (findEnd == -1)
            return null;
        return bigStr.substring(findStart, findEnd);
    }

    /**
     * List 长度修改
     *
     * @param list   被修改长度的List
     * @param size   长度
     * @param filler 若长度变长时的填充对象
     */
    public static void listResize(List<Object> list, int size, Object filler) {
        if (list != null) {
            if (list.size() < size) {
                for (int i = size - list.size(); i > 0; i--) {
                    list.add(filler);
                }
            } else if (list.size() > size) {
                for (int i = list.size() - size; i > 0; i--)
                    list.remove(list.size() - 1);
            }
        }
    }

    /**
     * List 长度修改，变长时填充以空字符串
     *
     * @param list 被修改长度的List
     * @param size 长度
     */
    public static void listResize(List<Object> list, int size) {
        listResize(list, size, "");
    }

    /**
     * 返回内码由utf8转为iso的字符串
     *
     * @param utf8Str utf8字符串
     * @return iso字符串
     */
    public static String UTF82ISO(String utf8Str) {
        try {
            return utf8Str == null ? null : new String(utf8Str.getBytes("UTF8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回内码由gbk转为iso的字符串
     *
     * @param gbkStr utf8字符串
     * @return iso字符串
     */
    public static String GBK2ISO(String gbkStr) {
        try {
            return gbkStr == null ? null : new String(gbkStr.getBytes("GBK"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 首字母大写
     *
     * @param str 待转换字符串
     * @return 首字母大写后的字符串
     */
    public static String upperFirstChar(String str) {
        char[] chars = str.toCharArray();
        if (chars.length > 0)
            chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * 首字母小写
     *
     * @param str 待转换字符串
     * @return 首字母小写后的字符串
     */
    public static String lowerFirstChar(String str) {
        char[] chars = str.toCharArray();
        if (chars.length > 0)
            chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private static String CONTEXT_PATH = null;

    private static DecimalFormat df = new DecimalFormat();

    static {
        df.setMaximumIntegerDigits(Integer.MAX_VALUE);
        df.setMaximumFractionDigits(Integer.MAX_VALUE);
        df.setGroupingUsed(false);
    }

    /**
     * 将数值转为不带科学计数法的字符串表示
     *
     * @param value 数值
     * @return 不带科学计数法的字符串表示
     * @since 1.1.0.5
     */
    public static String double2PlainString(double value) {
        return df.format(value);
    }

    /**
     * 替换字符串
     *
     * @param str         源字符串
     * @param pattern     Pattern 默认为 \\s*|\tl\rl\n 替换所有空格，tab，换行
     * @param replaceChar 匹配到的字符要替换的目标字符  默认为空，即删除
     * @return 替换后的字符串
     */
    public static String replace(String str, String pattern, String replaceChar) {
        if (str == null) return null;
        if (pattern == null || pattern.length() < 1) {
            pattern = "\\s*|\tl\rl\n"; //默认替换所有空格，tab，换行
        }
        if (replaceChar == null || replaceChar.length() < 1) replaceChar = ""; //默认替换为空
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.replaceAll(replaceChar);
    }

    /**
     * 删除字符串中的所有空格，tab，换行
     *
     * @param str 源字符串
     * @return 替换后的字符串
     */
    public static String replaceBlank(String str) {
        return StrUtils.replace(str, null, null);
    }

    /**
     * str转bytes
     *
     * @param str      str
     * @param encoding encoding
     * @return str转bytes
     */
    public static byte[] getBytes(String str, String encoding) {
        byte[] ret;
        try {
            ret = str.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            ret = str.getBytes();
        }
        return ret;
    }

    /**
     * string decode
     *
     * @param str      str
     * @param encoding encoding
     * @return decode后的字符串
     */
    public static String decode(String str, String encoding) {
        String ret = "";
        if (CoreUtils.nullSafeSize(str) > 0) {
            ret = str;
            try {
                ret = URLDecoder.decode(str, encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
