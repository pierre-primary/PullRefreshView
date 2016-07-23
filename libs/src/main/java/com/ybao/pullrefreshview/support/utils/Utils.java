package com.ybao.pullrefreshview.support.utils;

/**
 * Created by Ybao on 16/7/24.
 */
public class Utils {

    public static final boolean isClassExists(String classFullName) {
        try {
            Class.forName(classFullName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
