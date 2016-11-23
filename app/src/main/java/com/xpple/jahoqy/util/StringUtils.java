package com.xpple.jahoqy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sengar Wu on 2015/7/29.
 */
public class StringUtils {
    /**
     * 检验邮箱格式是否正确
     */
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * 判断电话号码是否有效
     *
     * @return true 有效 / false 无效
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {

        boolean isValid = false;

        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]\\d-?\\d{8}$)|(^0[3-9] \\d{2}-?\\d{7,8}$)|(^0[1,2]\\d-?\\d{8}-(\\d{1,4})$)|(^0[3-9]\\d{2}-? \\d{7,8}-(\\d{1,4})$))";

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phoneNumber);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 检验密码长度是否正确
     */
    public static boolean isValidPassword(CharSequence target) {
        return target != null && target.length() > 5 && target.length() < 17;
    }
}
