package info.futureme.abs.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextHelper {
    public static final String EMPTY = "";

    // Common

    /**
     * Check if a string is empty or pure spaces only
     */
    public static boolean isEmptyOrSpaces(String value) {
        if (value != null) {
            for (int i = value.length() - 1; i >= 0; --i) {
                if (value.charAt(i) != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Ensure null string will be converted as ""
     */
    public static String ensureNotNull(String s) {
        return s == null ? "" : s;
    }

    // Multiline

    public static final String LINE_BREAK = System.getProperty("line.separator");

    public static String lines(String... lines) {
        StringBuilder sb = new StringBuilder();
        int end = lines.length - 1;
        for (int i = 0; i < end; ++i) {
            sb.append(lines[i]).append(LINE_BREAK);
        }
        sb.append(lines[end]);
        return sb.toString();
    }

    // Concat

    public static final String concat(String... objs) {
        Assert.r(objs.length > 1 && objs[0] != null);
        StringBuilder sb = new StringBuilder();
        return concat(sb, objs);
    }

    public static final String concat(StringBuilder sb, String... objs) {
        Assert.r(sb != null);
        for (int i = 0, len = objs.length; i < len; ++i) {
            if (objs[i] != null) {
                sb.append(objs[i]);
            }
        }
        return sb.toString();
    }

    // Split

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String[] split(String src, String splitter) {
        return split(src, splitter, true, false);
    }

    /**
     * Split string and make sure the result is predictable.
     * <p/>
     * <p>
     * For example, String.split() will return ["","","a"] for "``a", but ["a"]
     * for "a``" which is unpredictable.
     * </p>
     *
     * @param splitter         currently doesn't support regular expression
     * @param keepEmpty
     * @param pureSpaceIsEmpty
     * @return
     */
    public static String[] split(String src, String splitter, boolean keepEmpty, boolean pureSpaceIsEmpty) {
        final int srcLen = src.length();
        if (src == null || srcLen == 0) {
            return EMPTY_STRING_ARRAY;
        }

        int index;
        int lastIndex = 0;
        String subStr;
        ArrayList<String> tmpResult = new ArrayList<String>();

        while (lastIndex <= srcLen) {
            // when lastIndex == srcLen, no exception, index = -1
            index = src.indexOf(splitter, lastIndex);
            if (index < 0) {
                index = srcLen;
            }
            subStr = src.substring(lastIndex, index);
            lastIndex = index + 1;
            if (pureSpaceIsEmpty && isEmptyOrSpaces(subStr)) {
                subStr = "";
            }
            if (keepEmpty || subStr.length() > 0) {
                tmpResult.add(subStr);
            }
        }

        String[] result = new String[tmpResult.size()];
        tmpResult.toArray(result);
        return result;
    }

    public static boolean isEmail(String email){
        if(email== null) return false;
        return email.matches("^\\w+@\\w+\\.(com|cn)");
    }


    /**
     * 检测密码的合格性6-20个英文字符和数字
     * @param pwd
     * @return
     */
    public static boolean isPWD(String pwd){

        if(pwd == null)
            return false;

        boolean isDigit = false, isLetter = false;
        Pattern p = Pattern.compile("^[a-zA-Z0-9]{6,12}$");
        Matcher m = p.matcher(pwd);
        boolean b = m.matches();
        for(int i=0 ; i<pwd.length() ; i++) { //循环遍历字符串
            if (Character.isDigit(pwd.charAt(i))) {     //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
            if (Character.isLetter(pwd.charAt(i))) {   //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }

        if(b && isDigit && isLetter) {
            return true;
        }else{
            return false;
        }
    }
    /**
     * 检验手机合格性
     * @param phoneNum
     * @return
     */
    public static boolean isPhoneNum(String phoneNum){
//      Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        if(phoneNum == null)
            return false;

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNum);
        boolean b = m.matches();
        if(b) {
            return true;
        }else{
            return false;
        }
    }

    public static boolean isAccount(String account) {
        if(account.length() >= 0) {
            return true;
        }
        else {
            return  false;
        }
    }

    public static int getRealSize(int size,Activity context){
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        int rSize = (int) (size/dm.density);
        return rSize;
    }

    /**
     * Truncates a string to the number of characters that fit in X bytes avoiding multi byte characters being cut in
     * half at the cut off point. Also handles surrogate pairs where 2 characters in the string is actually one literal
     * character.
     *
     * Based on: http://www.jroller.com/holy/entry/truncating_utf_string_to_the
     */
    public static String truncateToFitUtf8ByteLength(String s, int maxBytes, String append) {
        if (s == null) {
            return null;
        }
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        byte[] sba = s.getBytes(charset);
        if (sba.length <= maxBytes) {
            return s;
        }
        // Ensure truncation by having byte buffer = maxBytes
        ByteBuffer bb = ByteBuffer.wrap(sba, 0, maxBytes);
        CharBuffer cb = CharBuffer.allocate(maxBytes);
        // Ignore an incomplete character
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        decoder.decode(bb, cb, true);
        decoder.flush(cb);
        return new String(cb.array(), 0, cb.position()) + append;
    }


    /**
     * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
     *
     * @param c 需要判断的字符
     * @return 返回true,Ascill字符
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param s 需要得到长度的字符串
     * @return i得到的字符串长度
     */
    public static int length(String s) {
        if (s == null)
        {
            return 0;
        }
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            //如果为汉，日，韩，则多加一位
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }



    /******************************* Second Method ****************************/

    /**
     * 截取一段字符的长度(汉、日、韩文字符长度为2),不区分中英文,如果数字不正好，则少取一个字符位
     *
     * @param str 原始字符串
     * @param srcPos 开始位置
     * @param specialCharsLength 截取长度(汉、日、韩文字符长度为2)
     * @return
     */
    public static String substring(String str, int srcPos, int specialCharsLength) {
        if (str == null || "".equals(str) || specialCharsLength < 1) {
            return "";
        }
        if(srcPos<0)
        {
            srcPos=0;
        }
        if(specialCharsLength<=0)
        {
            return "";
        }
        //获得字符串的长度
        char[] chars = str.toCharArray();
        if(srcPos>chars.length)
        {
            return "";
        }
        int charsLength = getCharsLength(chars, specialCharsLength);
        return new String(chars, srcPos, charsLength);
    }

    /**
     * 获取一段字符的长度，输入长度中汉、日、韩文字符长度为2，输出长度中所有字符均长度为1
     * @param chars 一段字符
     * @param specialCharsLength 输入长度，汉、日、韩文字符长度为2
     * @return 输出长度，所有字符均长度为1
     */
    private static int getCharsLength(char[] chars, int specialCharsLength) {
        int count = 0;
        int normalCharsLength = 0;
        for (int i = 0; i < chars.length; i++) {
            int specialCharLength = getSpecialCharLength(chars[i]);
            if (count <= specialCharsLength - specialCharLength) {
                count += specialCharLength;
                normalCharsLength++;
            } else {
                break;
            }
        }
        return normalCharsLength;
    }

    /**
     * 获取字符长度：汉、日、韩文字符长度为2，ASCII码等字符长度为1
     * @param c 字符
     * @return 字符长度
     */
    private static int getSpecialCharLength(char c) {
        if (isLetter(c)) {
            return 1;
        } else {
            return 2;
        }
    }
}
